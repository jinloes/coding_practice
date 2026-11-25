package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import com.jinloes.practice_plugin.catalog.TrackCatalog;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    static String problemText(Exercise exercise, Set<String> passed) {
        StringBuilder html = new StringBuilder()
                .append("<h2>").append(MarkdownHtml.inline(exercise.title()))
                .append(passed.contains(exercise.id()) ? " (passed)" : "").append("</h2>")
                .append("<p>").append(MarkdownHtml.inline(exercise.difficulty())).append(" &middot; ")
                .append(MarkdownHtml.inline(exercise.topicSummary())).append("<br>")
                .append("Target: ").append(MarkdownHtml.inline(exercise.intendedTime())).append(" time, ")
                .append(MarkdownHtml.inline(exercise.intendedSpace())).append(" space</p>")
                .append(MarkdownHtml.toHtml(exercise.statement()))
                .append("<p>Visible examples</p><ul>");
        for (int index = 0; index < exercise.examples().size(); index++) {
            var example = exercise.examples().get(index);
            html.append("<li>Example ").append(index + 1)
                    .append("<br>Input: <code>").append(MarkdownHtml.inline(example.input()))
                    .append("</code><br>Output: <code>").append(MarkdownHtml.inline(example.output()))
                    .append("</code></li>");
        }
        html.append("</ul><p>Custom input format<br><code>")
                .append(MarkdownHtml.inline(exercise.inputSyntax()))
                .append("</code></p>");
        List<Guide> guides = GuideCatalog.containing(exercise.id());
        html.append("<p>Study guides: ")
                .append(guides.isEmpty() ? "Not part of any guide yet." : titles(guides))
                .append("</p>");
        List<Guide> tracks = TrackCatalog.containing(exercise.id());
        if (!tracks.isEmpty()) {
            html.append("<p>Practice tracks: ").append(titles(tracks)).append("</p>");
        }
        return html.toString();
    }

    private static String titles(List<Guide> guides) {
        return guides.stream().map(guide -> MarkdownHtml.inline(guide.title())).collect(Collectors.joining(", "));
    }

    /**
     * Renders themed Markdown HTML panes. Uses Swing's own HTML kit rather than a platform-specific
     * pane so rendering does not depend on an IDE version's HTML API, and refreshes CSS rules across
     * Look-and-Feel changes while preserving the current scroll/caret position.
     */
    static JEditorPane htmlPane() {
        return htmlPane(null);
    }

    static JEditorPane htmlPane(Supplier<String> bodySupplier) {
        JEditorPane pane = new JEditorPane() {
            @Override
            public void updateUI() {
                int savedCaret = getDocument() == null ? 0 : getCaretPosition();
                super.updateUI();
                applyHtmlTheme(this);
                String body = bodySupplier == null ? null : bodySupplier.get();
                if (body != null) {
                    setText("<html><body>" + body + "</body></html>");
                    setCaretPosition(Math.min(savedCaret, getDocument().getLength()));
                }
            }
        };
        pane.setEditable(false);
        pane.setOpaque(true);
        pane.setBorder(JBUI.Borders.empty());
        return pane;
    }

    static void setHtmlBody(JEditorPane pane, String bodyHtml) {
        pane.setText("<html><body>" + bodyHtml + "</body></html>");
        pane.setCaretPosition(0);
    }

    static void applyHtmlTheme(JEditorPane pane) {
        Font font = UIUtil.getLabelFont();
        int fontSize = font.getSize();
        if (ApplicationManager.getApplication() != null) {
            EditorColorsManager colors = EditorColorsManager.getInstance();
            if (colors != null) {
                fontSize = Math.max(fontSize, colors.getGlobalScheme().getEditorFontSize());
            }
        }
        int readableSize = Math.max(14, fontSize);
        String body = ColorUtil.toHex(UIUtil.getLabelForeground());
        HTMLEditorKit kit = new HTMLEditorKit() {
            private final StyleSheet styles = createStyles(super.getStyleSheet(), font, readableSize, body);

            @Override
            public StyleSheet getStyleSheet() {
                return styles;
            }
        };
        pane.setEditorKit(kit);
        pane.setBackground(UIUtil.getPanelBackground());
    }

    private static StyleSheet createStyles(StyleSheet defaults, Font font, int fontSize, String bodyHex) {
        StyleSheet css = new StyleSheet();
        if (defaults != null) {
            css.addStyleSheet(defaults);
        }
        css.addRule("body { font-family: \"" + font.getFamily() + "\"; font-size: " + fontSize
                + "pt; line-height: 1.45; color: #" + bodyHex + "; margin: 10px; }");
        css.addRule("p { margin: 0 0 10px 0; }");
        css.addRule("ul { margin: 0 0 10px 0; }");
        css.addRule("li { margin: 0 0 6px 0; }");
        css.addRule("code { font-family: monospace; }");
        return css;
    }

    static JBTextArea textArea() {
        JBTextArea area = new JBTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(JBUI.Borders.empty(8));
        return area;
    }

    static final String LIST_ACTIVATION = "practiceListActivation";

    static DoubleClickListener installListActivation(JList<?> list, Runnable onActivate) {
        DoubleClickListener listener = new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent event) {
                int index = list.locationToIndex(event.getPoint());
                java.awt.Rectangle bounds = index < 0 ? null : list.getCellBounds(index, index);
                if (bounds == null || !bounds.contains(event.getPoint())) {
                    return false;
                }
                onActivate.run();
                return true;
            }
        };
        listener.installOn(list);
        list.putClientProperty(LIST_ACTIVATION, listener);
        list.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "practiceStartOrResume");
        list.getActionMap().put("practiceStartOrResume", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                onActivate.run();
            }
        });
        return listener;
    }

    static JPanel column(Component... rows) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        for (Component row : rows) {
            panel.add(row);
        }
        return panel;
    }

    static JPanel header(ExerciseFilters filters, JBLabel count, ActionLink resetFilters) {
        JPanel panel = column();
        filters.addTo(panel);
        count.setFont(JBFont.label());
        count.setForeground(UIUtil.getContextHelpForeground());
        resetFilters.setFont(JBFont.label());
        JPanel footer = new JPanel(new BorderLayout(8, 0));
        footer.add(count, BorderLayout.WEST);
        footer.add(resetFilters, BorderLayout.EAST);
        panel.add(footer);
        return panel;
    }

    /** Wraps the filter controls and scrollable exercise list into one collapsible catalog pane. */
    static JPanel catalogPane(
            ExerciseFilters filters,
            JBLabel count,
            ActionLink resetFilters,
            JBList<Exercise> exercises
    ) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.add(header(filters, count, resetFilters), BorderLayout.NORTH);
        panel.add(new JBScrollPane(exercises), BorderLayout.CENTER);
        return panel;
    }

    /** Natural-height footer that pairs the attempt bar and run toolbar on one row when wide. */
    static JPanel footer(JComponent attemptBar, JComponent toolbar, JBLabel status, boolean wide) {
        JPanel controls = new JPanel(new BorderLayout(8, 4));
        if (wide) {
            controls.add(attemptBar, BorderLayout.CENTER);
            controls.add(toolbar, BorderLayout.EAST);
        } else {
            controls.add(attemptBar, BorderLayout.NORTH);
            controls.add(toolbar, BorderLayout.SOUTH);
        }
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBorder(JBUI.Borders.emptyTop(2));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(status, BorderLayout.SOUTH);
        return panel;
    }

    static JBList<Exercise> exerciseList(
            DefaultListModel<Exercise> model,
            Supplier<Set<String>> passed,
            Runnable onActivate,
            Runnable onResetFilters
    ) {
        JBList<Exercise> list = new JBList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ExerciseCellRenderer(passed));
        list.getEmptyText().setText("No exercises match these filters");
        list.getEmptyText().appendSecondaryText(
                "Reset filters", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, _ -> onResetFilters.run());
        list.getAccessibleContext().setAccessibleName("Exercises");
        installListActivation(list, onActivate);
        return list;
    }

    static OnePixelSplitter splitter(JComponent first, JComponent second) {
        OnePixelSplitter split = new OnePixelSplitter(true, "algorithm.practice.splitter", 0.34f);
        split.setFirstComponent(first);
        split.setSecondComponent(second);
        return split;
    }
}
