package com.jinloes.practice_plugin.ui;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.Component;
import java.awt.Font;
import java.util.function.Function;

/** Stateless Swing building blocks for {@link PracticePanel}. */
final class PracticeViews {
    private PracticeViews() {
    }

    /** A list renderer that labels values of {@code type} and leaves anything else to the default. */
    static <T> ListCellRenderer<Object> labelled(Class<T> type, Function<T, String> label) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean focus
            ) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (type.isInstance(value)) {
                    setText(label.apply(type.cast(value)));
                }
                return this;
            }
        };
    }

    static String problemText(Exercise exercise) {
        StringBuilder html = new StringBuilder(MarkdownHtml.toHtml(exercise.statement()));
        html.append("<p>Visible examples</p><ul>");
        for (int index = 0; index < exercise.examples().size(); index++) {
            var example = exercise.examples().get(index);
            html.append("<li>Example ").append(index + 1)
                    .append("<br>Input: <code>").append(MarkdownHtml.inline(example.input()))
                    .append("</code><br>Output: <code>").append(MarkdownHtml.inline(example.output()))
                    .append("</code></li>");
        }
        return html.append("</ul>").toString();
    }

    /**
     * Renders the Problem tab, whose statements are Markdown. Uses Swing's own HTML kit rather than
     * a platform-specific pane so the rendering does not depend on an IDE version's HTML API.
     */
    static JEditorPane htmlPane() {
        HTMLEditorKit kit = new HTMLEditorKit();
        Font font = UIUtil.getLabelFont();
        String body = ColorUtil.toHex(UIUtil.getLabelForeground());
        StyleSheet css = kit.getStyleSheet();
        css.addRule("body { font-family: \"" + font.getFamily() + "\"; font-size: " + font.getSize()
                + "pt; color: #" + body + "; margin: 8px; }");
        css.addRule("p { margin: 0 0 10px 0; }");
        css.addRule("ul { margin: 0 0 10px 0; }");
        css.addRule("li { margin: 0 0 6px 0; }");
        css.addRule("code { font-family: monospace; }");

        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(kit);
        pane.setEditable(false);
        pane.setOpaque(true);
        pane.setBackground(UIUtil.getPanelBackground());
        pane.setBorder(JBUI.Borders.empty());
        return pane;
    }

    static JBTextArea textArea() {
        JBTextArea area = new JBTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(JBUI.Borders.empty(8));
        return area;
    }
}
