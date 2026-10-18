package com.jinloes.practice_plugin.ui;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

/** The Swing HTML fallback for {@link PageView}, used when JCEF is unavailable. Its pages are not interactive. */
final class HtmlPageView implements PageView {
    private final JEditorPane pane = PracticeViews.htmlPane();
    private final JBScrollPane scroll = new JBScrollPane(pane);

    @Override
    public JComponent component() {
        return scroll;
    }

    @Override
    public void show(PracticePage page) {
        pane.setText("<html><body>" + html(page) + "</body></html>");
        pane.setCaretPosition(0);
    }

    static String html(PracticePage page) {
        return switch (page) {
            case PracticePage.Problem problem -> problem.exercise() == null
                    ? "<p>No exercises match these filters.</p>"
                    : PracticeViews.problemText(problem.exercise());
            case PracticePage.Study study -> StudyGuideView.html(study.guide(), study.selected(), study.passed());
        };
    }
}
