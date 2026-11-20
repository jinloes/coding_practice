package com.jinloes.system_design_plugin.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.catalog.GuideCatalog;
import com.jinloes.system_design_plugin.catalog.GuideCatalog.Guide;
import com.jinloes.system_design_plugin.catalog.TrackCatalog;
import com.jinloes.system_design_plugin.catalog.TrackCatalog.Track;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serializes the tool window's state for {@code window.designRender}. The contract is mirrored in
 * {@code web/src/types.ts}; change both together.
 *
 * <p>Statement, hint, and guide text is converted with {@link MarkdownHtml}, which escapes it, so the page may
 * insert those fields as HTML. Every other string is plain text. A problem's interviewer-only
 * {@link Problem#expectations() expectations} are never serialized; feedback may paraphrase them.
 */
public final class PageState {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private PageState() {
    }

    /** The IDE colors and fonts the pages adopt, as CSS values. */
    public record Theme(boolean dark, String background, String foreground, String secondary, String border,
                        String accent, String fontFamily, int fontSize) {
    }

    public record AttemptInfo(String id, String label, String customLabel) {
        public AttemptInfo(String id, String label) {
            this(id, label, "");
        }

        public AttemptInfo {
            id = Objects.requireNonNull(id, "id");
            label = Objects.requireNonNull(label, "label");
            customLabel = Objects.requireNonNullElse(customLabel, "");
        }
    }

    /** One saved review of an attempt and whether the attempt has changed since that review. */
    public record Review(InterviewFeedback.Stored stored, boolean stale) {
        public Review {
            Objects.requireNonNull(stored, "stored");
        }
    }

    /** Everything the tool window shows. {@code selected} may be {@code null}; {@code reviews} is newest first. */
    public record View(Map<String, String> verdicts, Problem selected, List<AttemptInfo> attempts,
                       String selectedAttempt, int hintsRevealed, String targetLevel, boolean running,
                       List<Review> reviews, String error) {
        public View(Set<String> reviewed, Problem selected, List<AttemptInfo> attempts, String selectedAttempt,
                    int hintsRevealed, boolean running, List<Review> reviews, String error) {
            this(reviewed, selected, attempts, selectedAttempt, hintsRevealed,
                    DesignCatalog.DEFAULT_LEVEL, running, reviews, error);
        }

        public View(Set<String> reviewed, Problem selected, List<AttemptInfo> attempts, String selectedAttempt,
                    int hintsRevealed, String targetLevel, boolean running, List<Review> reviews, String error) {
            this(reviewed.stream().collect(Collectors.toUnmodifiableMap(id -> id, ignored -> "")),
                    selected, attempts, selectedAttempt, hintsRevealed, targetLevel, running, reviews, error);
        }

        public View {
            verdicts = Map.copyOf(verdicts);
            attempts = List.copyOf(attempts);
            selectedAttempt = Objects.requireNonNullElse(selectedAttempt, "");
            targetLevel = DesignCatalog.normalizeLevel(targetLevel);
            reviews = List.copyOf(reviews);
            error = Objects.requireNonNullElse(error, "");
        }

        public Set<String> reviewed() {
            return verdicts.keySet();
        }

        public InterviewFeedback.Stored feedback() {
            return reviews.isEmpty() ? null : reviews.get(0).stored();
        }

        public boolean stale() {
            return !reviews.isEmpty() && reviews.get(0).stale();
        }
    }

    public static String toJson(View view, Theme theme) {
        JsonObject state = new JsonObject();
        state.add("theme", GSON.toJsonTree(theme));
        state.add("levels", GSON.toJsonTree(DesignCatalog.LEVELS));
        state.addProperty("targetLevel", view.targetLevel());
        state.add("tracks", tracks());
        state.add("guides", guides());
        JsonArray problems = new JsonArray();
        for (Problem problem : DesignCatalog.all()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", problem.id());
            item.addProperty("title", problem.title());
            item.addProperty("difficulty", problem.difficulty());
            item.addProperty("summary", problem.summary());
            item.add("tracks", GSON.toJsonTree(TrackCatalog.containing(problem.id())));
            item.addProperty("reviewed", view.verdicts().containsKey(problem.id()));
            item.addProperty("verdict", view.verdicts().getOrDefault(problem.id(), ""));
            item.addProperty("selected", view.selected() != null && view.selected().id().equals(problem.id()));
            problems.add(item);
        }
        state.add("problems", problems);
        state.add("selected", view.selected() == null ? null : selected(view));
        state.addProperty("running", view.running());
        JsonArray reviews = new JsonArray();
        for (Review item : view.reviews()) {
            reviews.add(review(item));
        }
        state.add("reviews", reviews);
        state.add("feedback", view.reviews().isEmpty() ? null : review(view.reviews().get(0)));
        state.addProperty("stale", view.stale());
        state.addProperty("error", view.error());
        return GSON.toJson(state);
    }

    private static JsonArray tracks() {
        JsonArray array = new JsonArray();
        for (Track track : TrackCatalog.all()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", track.id());
            item.addProperty("title", track.title());
            item.addProperty("summary", track.summary());
            item.add("problemIds", GSON.toJsonTree(track.problemIds()));
            array.add(item);
        }
        return array;
    }

    private static JsonArray guides() {
        JsonArray array = new JsonArray();
        for (Guide guide : GuideCatalog.all()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", guide.id());
            item.addProperty("title", guide.title());
            item.addProperty("summary", guide.summary());
            item.addProperty("bodyHtml", MarkdownHtml.toHtml(guide.body()));
            item.add("relatedProblemIds", GSON.toJsonTree(guide.relatedProblemIds()));
            array.add(item);
        }
        return array;
    }

    private static JsonObject selected(View view) {
        Problem problem = view.selected();
        JsonObject selected = new JsonObject();
        selected.addProperty("id", problem.id());
        selected.addProperty("title", problem.title());
        selected.addProperty("difficulty", problem.difficulty());
        selected.addProperty("promptHtml", MarkdownHtml.firstParagraphHtml(problem.statement()));
        selected.addProperty("statementHtml", MarkdownHtml.toHtml(problem.statement()));
        int revealed = Math.max(0, Math.min(view.hintsRevealed(), problem.hints().size()));
        JsonArray hints = new JsonArray();
        problem.hints().subList(0, revealed).forEach(hint -> hints.add(MarkdownHtml.inline(hint)));
        selected.add("hintsHtml", hints);
        selected.addProperty("hintCount", problem.hints().size());
        JsonArray attempts = new JsonArray();
        for (AttemptInfo attempt : view.attempts()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", attempt.id());
            item.addProperty("label", attempt.label());
            item.addProperty("customLabel", attempt.customLabel());
            attempts.add(item);
        }
        selected.add("attempts", attempts);
        selected.addProperty("selectedAttempt", view.selectedAttempt());
        return selected;
    }

    private static JsonObject review(Review item) {
        InterviewFeedback.Stored stored = item.stored();
        InterviewFeedback feedback = stored.feedback();
        JsonObject json = new JsonObject();
        json.addProperty("parsed", feedback.parsed());
        json.addProperty("verdict", feedback.verdict());
        json.addProperty("summary", feedback.summary());
        JsonArray dimensions = new JsonArray();
        for (InterviewFeedback.Dimension dimension : feedback.dimensions()) {
            JsonObject dim = new JsonObject();
            dim.addProperty("name", dimension.name());
            dim.addProperty("score", dimension.score());
            dim.add("strengths", GSON.toJsonTree(dimension.strengths()));
            dim.add("gaps", GSON.toJsonTree(dimension.gaps()));
            dimensions.add(dim);
        }
        json.add("dimensions", dimensions);
        json.add("followUpQuestions", GSON.toJsonTree(feedback.followUpQuestions()));
        json.add("missedExpectations", GSON.toJsonTree(feedback.missedExpectations()));
        json.addProperty("raw", feedback.raw());
        json.addProperty("model", stored.model());
        json.addProperty("createdAt", stored.createdAt());
        json.addProperty("level", stored.level());
        json.addProperty("round", stored.round());
        json.addProperty("followUpAnswers", stored.followUpAnswers());
        json.addProperty("stale", item.stale());
        return json;
    }
}
