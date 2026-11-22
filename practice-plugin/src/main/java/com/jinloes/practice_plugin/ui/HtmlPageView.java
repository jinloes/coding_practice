package com.jinloes.practice_plugin.ui;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

/** The Swing HTML fallback for {@link PageView}, used when JCEF is unavailable. Its pages are not interactive. */
final class HtmlPageView implements PageView {
    private PracticePage current;
    private final JEditorPane pane = PracticeViews.htmlPane(() -> current == null ? null : html(current));
    private final JBScrollPane scroll = new JBScrollPane(pane);

    @Override
    public JComponent component() {
        return scroll;
    }

    @Override
    public void show(PracticePage page) {
        current = page;
        PracticeViews.setHtmlBody(pane, html(page));
    }

    static String html(PracticePage page) {
        return switch (page) {
            case PracticePage.Problem problem -> problem.exercise() == null
                    ? "<p>No exercises match these filters. Use Reset filters above the exercise list to show all exercises.</p>"
                    : PracticeViews.problemText(problem.exercise(), problem.passed());
            case PracticePage.Study study -> StudyGuideView.html(study.guide(), study.selected(), study.passed());
        };
    }
}
