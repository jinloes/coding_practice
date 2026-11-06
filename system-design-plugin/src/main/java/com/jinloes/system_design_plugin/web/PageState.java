package com.jinloes.system_design_plugin.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Serializes the tool window's state for {@code window.designRender}. The contract is mirrored in
 * {@code web/src/types.ts}; change both together.
 *
 * <p>Statement and hint text is converted with {@link MarkdownHtml}, which escapes it, so the page may insert
 * those fields as HTML. Every other string is plain text. A problem's interviewer-only
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

    public record AttemptInfo(String id, String label) {
    }

    /** Everything the tool window shows. {@code selected} and {@code feedback} may be {@code null}. */
    public record View(Set<String> reviewed, Problem selected, List<AttemptInfo> attempts, String selectedAttempt,
                       int hintsRevealed, boolean running, InterviewFeedback.Stored feedback, boolean stale,
                       String error) {
        public View {
            reviewed = Set.copyOf(reviewed);
            attempts = List.copyOf(attempts);
            selectedAttempt = Objects.requireNonNullElse(selectedAttempt, "");
            error = Objects.requireNonNullElse(error, "");
        }
    }

    public static String toJson(View view, Theme theme) {
        JsonObject state = new JsonObject();
        state.add("theme", GSON.toJsonTree(theme));
        JsonArray problems = new JsonArray();
        for (Problem problem : DesignCatalog.all()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", problem.id());
            item.addProperty("title", problem.title());
            item.addProperty("difficulty", problem.difficulty());
            item.addProperty("summary", problem.summary());
            item.addProperty("reviewed", view.reviewed().contains(problem.id()));
            item.addProperty("selected", view.selected() != null && view.selected().id().equals(problem.id()));
            problems.add(item);
        }
        state.add("problems", problems);
        state.add("selected", view.selected() == null ? null : selected(view));
        state.addProperty("running", view.running());
        state.add("feedback", view.feedback() == null ? null : feedback(view.feedback()));
        state.addProperty("stale", view.feedback() != null && view.stale());
        state.addProperty("error", view.error());
        return GSON.toJson(state);
    }

    private static JsonObject selected(View view) {
        Problem problem = view.selected();
        JsonObject selected = new JsonObject();
        selected.addProperty("id", problem.id());
        selected.addProperty("title", problem.title());
        selected.addProperty("difficulty", problem.difficulty());
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
            attempts.add(item);
        }
        selected.add("attempts", attempts);
        selected.addProperty("selectedAttempt", view.selectedAttempt());
        return selected;
    }

    private static JsonObject feedback(InterviewFeedback.Stored stored) {
        InterviewFeedback feedback = stored.feedback();
        JsonObject json = new JsonObject();
        json.addProperty("parsed", feedback.parsed());
        json.addProperty("verdict", feedback.verdict());
        json.addProperty("summary", feedback.summary());
        JsonArray dimensions = new JsonArray();
        for (InterviewFeedback.Dimension dimension : feedback.dimensions()) {
            JsonObject item = new JsonObject();
            item.addProperty("name", dimension.name());
            item.addProperty("score", dimension.score());
            item.add("strengths", GSON.toJsonTree(dimension.strengths()));
            item.add("gaps", GSON.toJsonTree(dimension.gaps()));
            dimensions.add(item);
        }
        json.add("dimensions", dimensions);
        json.add("followUpQuestions", GSON.toJsonTree(feedback.followUpQuestions()));
        json.add("missedExpectations", GSON.toJsonTree(feedback.missedExpectations()));
        json.addProperty("raw", feedback.raw());
        json.addProperty("model", stored.model());
        json.addProperty("createdAt", stored.createdAt());
        return json;
    }
}
