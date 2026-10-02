package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** The Study Guide tab: the selected guide's lesson and its problems grouped by difficulty. */
final class StudyGuideView {
    private final JEditorPane pane = PracticeViews.htmlPane();

    JComponent component() {
        return pane;
    }

    /** Shows {@code guide}, or, when it is null, which guides include {@code selected}. */
    void show(Guide guide, Exercise selected, Set<String> passed) {
        pane.setText("<html><body>" + html(guide, selected, passed) + "</body></html>");
        pane.setCaretPosition(0);
    }

    static String html(Guide guide, Exercise selected, Set<String> passed) {
        if (guide == null) {
            return chooser(selected);
        }
        StringBuilder html = new StringBuilder()
                .append("<h2>").append(MarkdownHtml.inline(guide.title())).append("</h2>")
                .append("<p><i>").append(MarkdownHtml.inline(guide.summary())).append("</i></p>")
                .append(MarkdownHtml.toHtml(guide.lesson()));
        for (String level : GuideCatalog.DIFFICULTIES) {
            List<Exercise> exercises = guide.exercisesAt(level);
            long done = exercises.stream().filter(exercise -> passed.contains(exercise.id())).count();
            html.append("<h3>").append(level).append(" (").append(done).append('/')
                    .append(exercises.size()).append(" passed)</h3>");
            if (exercises.isEmpty()) {
                html.append("<p>No ").append(level).append(" problems in this guide yet.</p>");
                continue;
            }
            html.append("<ul>");
            for (Exercise exercise : exercises) {
                html.append("<li>").append(MarkdownHtml.inline(exercise.title()))
                        .append(passed.contains(exercise.id()) ? " (passed)" : "").append("</li>");
            }
            html.append("</ul>");
        }
        return html.toString();
    }

    private static String chooser(Exercise selected) {
        StringBuilder html = new StringBuilder("<p>Choose a guide above to study a topic.</p>");
        if (selected != null) {
            List<Guide> guides = GuideCatalog.containing(selected.id());
            html.append("<p>").append(MarkdownHtml.inline(selected.title()));
            if (guides.isEmpty()) {
                html.append(" is not part of any guide yet.</p>");
            } else {
                html.append(" appears in: ").append(guides.stream()
                        .map(guide -> MarkdownHtml.inline(guide.title()))
                        .collect(Collectors.joining(", "))).append(".</p>");
            }
        }
        return html.toString();
    }
}
