package com.jinloes.practice_plugin.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import com.jinloes.practice_plugin.catalog.TrackCatalog;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Set;

/**
 * Serializes a {@link PracticePage} and the IDE theme into the {@code PageState} JSON the React pages
 * render. The contract is mirrored in {@code web/src/types.ts}; change both together.
 *
 * <p>Markdown fields (statement, summary, lesson, hints, and coach turns) are sent as raw Markdown text
 * and rendered safely by {@code react-markdown} + {@code remark-gfm} without raw HTML injection.
 */
final class PageJson {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private PageJson() {
    }

    /** The IDE colors and fonts a page adopts, as CSS values. */
    record Theme(boolean dark, String background, String foreground, String secondary, String border,
                 String accent, String fontFamily, String codeFontFamily, int fontSize) {

        static Theme current() {
            Font font = UIUtil.getLabelFont();
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
            int fontSize = Math.max(font.getSize(), scheme.getEditorFontSize());
            return new Theme(!JBColor.isBright(), css(UIUtil.getPanelBackground()), css(UIUtil.getLabelForeground()),
                    css(UIUtil.getContextHelpForeground()), css(JBColor.border()),
                    css(JBUI.CurrentTheme.Link.Foreground.ENABLED),
                    family(font.getFamily(), "system-ui, sans-serif"), family(scheme.getEditorFontName(), "monospace"),
                    fontSize);
        }

        private static String css(Color color) {
            return "#" + ColorUtil.toHex(color);
        }

        private static String family(String name, String fallback) {
            return "\"" + name.replace("\"", "") + "\", " + fallback;
        }
    }

    static String toJson(PracticePage page, Theme theme) {
        JsonObject state = new JsonObject();
        state.add("theme", GSON.toJsonTree(theme));
        state.add("page", switch (page) {
            case PracticePage.Problem problem -> problemPage(problem);
            case PracticePage.Study study when study.guide() != null ->
                    guidePage(study.guide(), study.selected(), study.passed());
            case PracticePage.Study study -> chooserPage(study.selected());
            case PracticePage.Hints hints -> hintsPage(hints);
            case PracticePage.Results results -> resultsPage(results);
            case PracticePage.Coach coach -> coachPage(coach);
        });
        return GSON.toJson(state);
    }

    private static JsonObject problemPage(PracticePage.Problem source) {
        Exercise exercise = source.exercise();
        JsonObject page = kind("problem");
        if (exercise == null) {
            page.add("problem", null);
            return page;
        }
        JsonObject problem = new JsonObject();
        problem.addProperty("id", exercise.id());
        problem.addProperty("title", exercise.title());
        problem.add("topics", stringArray(exercise.topics()));
        problem.addProperty("difficulty", exercise.difficulty());
        problem.addProperty("passed", source.passed().contains(exercise.id()));
        problem.addProperty("statement", exercise.statement());
        JsonArray examples = new JsonArray();
        exercise.examples().forEach(example -> {
            JsonObject item = new JsonObject();
            item.addProperty("input", example.input());
            item.addProperty("output", example.output());
            examples.add(item);
        });
        problem.add("examples", examples);
        problem.addProperty("inputSyntax", exercise.inputSyntax());
        problem.addProperty("sampleInput", exercise.sampleInput());
        problem.addProperty("intendedTime", exercise.intendedTime());
        problem.addProperty("intendedSpace", exercise.intendedSpace());
        problem.addProperty("hasAttempt", source.hasAttempt());
        problem.addProperty("canRunExamples", source.canRunExamples());
        addMemberships(problem, exercise);
        page.add("problem", problem);
        return page;
    }

    private static JsonObject guidePage(Guide guide, Exercise selected, Set<String> passed) {
        JsonObject body = new JsonObject();
        body.addProperty("id", guide.id());
        body.addProperty("title", guide.title());
        body.addProperty("kind", TrackCatalog.isTrack(guide.id()) ? "track" : "guide");
        body.addProperty("summary", guide.summary());
        body.addProperty("lesson", guide.lesson());
        JsonArray levels = new JsonArray();
        for (String difficulty : GuideCatalog.DIFFICULTIES) {
            JsonArray exercises = new JsonArray();
            for (Exercise exercise : guide.exercisesAt(difficulty)) {
                JsonObject item = new JsonObject();
                item.addProperty("id", exercise.id());
                item.addProperty("title", exercise.title());
                item.add("topics", stringArray(exercise.topics()));
                item.addProperty("passed", passed.contains(exercise.id()));
                item.addProperty("selected", exercise.equals(selected));
                exercises.add(item);
            }
            JsonObject level = new JsonObject();
            level.addProperty("level", difficulty);
            level.add("exercises", exercises);
            levels.add(level);
        }
        body.add("levels", levels);
        JsonObject page = kind("guide");
        page.add("guide", body);
        return page;
    }

    private static JsonObject chooserPage(Exercise selected) {
        JsonObject page = kind("chooser");
        if (selected == null) {
            page.add("selected", null);
            return page;
        }
        JsonObject membership = new JsonObject();
        membership.addProperty("title", selected.title());
        addMemberships(membership, selected);
        page.add("selected", membership);
        return page;
    }

    private static JsonObject hintsPage(PracticePage.Hints source) {
        JsonObject page = kind("hints");
        JsonObject hints = new JsonObject();
        Exercise exercise = source.exercise();
        if (exercise == null) {
            hints.add("exerciseTitle", null);
            hints.addProperty("hasAttempt", false);
            hints.addProperty("revealedCount", 0);
            hints.addProperty("totalCount", 0);
            hints.add("revealedHints", new JsonArray());
            page.add("hints", hints);
            return page;
        }
        List<String> all = exercise.hints();
        int revealed = source.hasAttempt() ? Math.min(source.revealedCount(), all.size()) : 0;
        hints.addProperty("exerciseTitle", exercise.title());
        hints.addProperty("hasAttempt", source.hasAttempt());
        hints.addProperty("revealedCount", revealed);
        hints.addProperty("totalCount", all.size());
        JsonArray revealedHints = new JsonArray();
        for (int index = 0; index < revealed; index++) {
            revealedHints.add(all.get(index));
        }
        hints.add("revealedHints", revealedHints);
        page.add("hints", hints);
        return page;
    }

    private static JsonObject resultsPage(PracticePage.Results source) {
        JsonObject page = kind("results");
        JsonObject results = new JsonObject();
        results.addProperty("tone", source.tone());
        results.addProperty("banner", source.banner());
        results.addProperty("checkedAt", source.checkedAt());
        results.addProperty("lastPassedAt", source.lastPassedAt());
        results.addProperty("stale", source.stale());
        results.addProperty("staleReason", source.staleReason());
        results.addProperty("askCoachEnabled", source.askCoachEnabled());
        results.addProperty("output", source.output());
        page.add("results", results);
        return page;
    }

    private static JsonObject coachPage(PracticePage.Coach source) {
        JsonObject page = kind("coach");
        JsonObject coach = new JsonObject();
        if (source.exercise() == null) {
            coach.add("exerciseId", null);
            coach.add("exerciseTitle", null);
        } else {
            coach.addProperty("exerciseId", source.exercise().id());
            coach.addProperty("exerciseTitle", source.exercise().title());
        }
        if (source.attemptId() == null) {
            coach.add("attemptId", null);
        } else {
            coach.addProperty("attemptId", source.attemptId());
        }
        coach.addProperty("unlocked", source.unlocked());
        coach.addProperty("lockReason", source.lockReason());
        coach.addProperty("running", source.running());
        coach.addProperty("checkSummary", source.checkSummary());
        coach.addProperty("stale", source.stale());
        coach.addProperty("error", source.error());
        coach.addProperty("modelLabel", source.modelLabel());
        JsonArray turns = new JsonArray();
        for (PracticePage.CoachTurn turn : source.turns()) {
            JsonObject item = new JsonObject();
            item.addProperty("role", turn.role());
            item.addProperty("markdown", turn.markdown());
            item.addProperty("createdAt", turn.createdAt());
            turns.add(item);
        }
        coach.add("turns", turns);
        page.add("coach", coach);
        return page;
    }

    private static void addMemberships(JsonObject target, Exercise exercise) {
        target.add("guides", links(GuideCatalog.containing(exercise.id())));
        target.add("tracks", links(TrackCatalog.containing(exercise.id())));
    }

    private static JsonArray links(List<Guide> guides) {
        JsonArray links = new JsonArray();
        guides.forEach(guide -> {
            JsonObject link = new JsonObject();
            link.addProperty("id", guide.id());
            link.addProperty("title", guide.title());
            links.add(link);
        });
        return links;
    }

    private static JsonArray stringArray(List<String> values) {
        JsonArray array = new JsonArray();
        values.forEach(array::add);
        return array;
    }

    private static JsonObject kind(String kind) {
        JsonObject page = new JsonObject();
        page.addProperty("kind", kind);
        return page;
    }
}
