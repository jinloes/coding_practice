package com.jinloes.practice_plugin.ui;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

/** The Swing HTML fallback for {@link PageView}, used when JCEF is unavailable. Its pages are not interactive. */
final class HtmlPageView implements PageView {
    private PracticePage current;
    private final JEditorPane pane = createPane();
    private final JBScrollPane scroll = new JBScrollPane(pane);

    private JEditorPane createPane() {
        JEditorPane editor = new JEditorPane() {
            @Override
            public void updateUI() {
                int savedCaret = getDocument() == null ? 0 : getCaretPosition();
                super.updateUI();
                PracticeViews.applyHtmlTheme(this);
                if (current != null) {
                    setText("<html><body>" + html(current) + "</body></html>");
                    setCaretPosition(Math.min(savedCaret, getDocument().getLength()));
                }
            }
        };
        editor.setEditable(false);
        editor.setOpaque(true);
        editor.setBorder(JBUI.Borders.empty());
        PracticeViews.applyHtmlTheme(editor);
        return editor;
    }

    @Override
    public JComponent component() {
        return scroll;
    }

    @Override
    public void show(PracticePage page) {
        current = page;
        pane.setText("<html><body>" + html(page) + "</body></html>");
        pane.setCaretPosition(0);
    }

    static String html(PracticePage page) {
        return switch (page) {
            case PracticePage.Problem problem -> problem.exercise() == null
                    ? "<p>No exercises match these filters.</p>"
                    : PracticeViews.problemText(problem.exercise(), problem.passed());
            case PracticePage.Study study -> StudyGuideView.html(study.guide(), study.selected(), study.passed());
        };
    }
}
