package com.jinloes.practice_plugin.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.NamedColorUtil;
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
 * <p>Statement, summary, and lesson text is converted with {@link MarkdownHtml}, which escapes it, so the
 * pages may insert those fields as HTML. Every other string is plain text.
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
            String editorFont = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
            return new Theme(!JBColor.isBright(), css(UIUtil.getPanelBackground()), css(UIUtil.getLabelForeground()),
                    css(NamedColorUtil.getInactiveTextColor()), css(JBColor.border()),
                    css(JBUI.CurrentTheme.Link.Foreground.ENABLED),
                    family(font.getFamily(), "system-ui, sans-serif"), family(editorFont, "monospace"),
                    font.getSize());
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
            case PracticePage.Problem problem -> problemPage(problem.exercise(), problem.passed());
            case PracticePage.Study study when study.guide() != null ->
                    guidePage(study.guide(), study.selected(), study.passed());
            case PracticePage.Study study -> chooserPage(study.selected());
        });
        return GSON.toJson(state);
    }

    private static JsonObject problemPage(Exercise exercise, Set<String> passed) {
        JsonObject page = kind("problem");
        if (exercise == null) {
            page.add("problem", null);
            return page;
        }
        JsonObject problem = new JsonObject();
        problem.addProperty("id", exercise.id());
        problem.addProperty("title", exercise.title());
        problem.addProperty("topic", exercise.topic());
        problem.addProperty("difficulty", exercise.difficulty());
        problem.addProperty("passed", passed.contains(exercise.id()));
        problem.addProperty("statementHtml", MarkdownHtml.toHtml(exercise.statement()));
        JsonArray examples = new JsonArray();
        exercise.examples().forEach(example -> {
            JsonObject item = new JsonObject();
            item.addProperty("input", example.input());
            item.addProperty("output", example.output());
            examples.add(item);
        });
        problem.add("examples", examples);
        problem.addProperty("inputSyntax", exercise.inputSyntax());
        problem.addProperty("intendedTime", exercise.intendedTime());
        problem.addProperty("intendedSpace", exercise.intendedSpace());
        addMemberships(problem, exercise);
        page.add("problem", problem);
        return page;
    }

    private static JsonObject guidePage(Guide guide, Exercise selected, Set<String> passed) {
        JsonObject body = new JsonObject();
        body.addProperty("id", guide.id());
        body.addProperty("title", guide.title());
        body.addProperty("kind", TrackCatalog.all().contains(guide) ? "track" : "guide");
        body.addProperty("summaryHtml", MarkdownHtml.inline(guide.summary()));
        body.addProperty("lessonHtml", MarkdownHtml.toHtml(guide.lesson()));
        JsonArray levels = new JsonArray();
        for (String difficulty : GuideCatalog.DIFFICULTIES) {
            JsonArray exercises = new JsonArray();
            for (Exercise exercise : guide.exercisesAt(difficulty)) {
                JsonObject item = new JsonObject();
                item.addProperty("id", exercise.id());
                item.addProperty("title", exercise.title());
                item.addProperty("topic", exercise.topic());
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

    private static JsonObject kind(String kind) {
        JsonObject page = new JsonObject();
        page.addProperty("kind", kind);
        return page;
    }
}
