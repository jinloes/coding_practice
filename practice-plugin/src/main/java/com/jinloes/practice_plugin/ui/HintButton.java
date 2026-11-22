package com.jinloes.practice_plugin.ui;

import javax.swing.JButton;
import java.util.List;

/** The progressive hint-reveal button and HTML/text formatters, enabled while an attempt has unrevealed hints. */
final class HintButton {
    static final String NO_ATTEMPT_TOOLTIP = "Start or resume an attempt to reveal hints";
    static final String ALL_REVEALED_TOOLTIP = "All hints for this exercise have been revealed";

    private final JButton button = new JButton("Reveal Hint");

    HintButton(Runnable onClick) {
        button.addActionListener(_ -> onClick.run());
        update(false, 0, 3);
    }

    JButton component() {
        return button;
    }

    void update(boolean attemptSelected) {
        update(attemptSelected, 0, 3);
    }

    void update(boolean attemptSelected, int revealed, int total) {
        if (!attemptSelected) {
            button.setEnabled(false);
            button.setText("Reveal Hint");
            button.setToolTipText(NO_ATTEMPT_TOOLTIP);
            return;
        }
        int safeTotal = Math.max(total, 0);
        int safeRevealed = Math.max(revealed, 0);
        if (safeRevealed < safeTotal) {
            button.setEnabled(true);
            button.setText("Reveal Hint " + (safeRevealed + 1) + " of " + safeTotal);
            button.setToolTipText(null);
            return;
        }
        button.setEnabled(false);
        button.setText("All " + safeTotal + " Hints Revealed");
        button.setToolTipText(ALL_REVEALED_TOOLTIP);
    }

    static String tabTitle(boolean attemptSelected, int revealed, int total) {
        int clamped = Math.min(Math.max(revealed, 0), Math.max(total, 0));
        return attemptSelected && clamped > 0 ? "Hints (" + clamped + "/" + Math.max(total, 0) + ")" : "Hints";
    }

    /** Renders revealed hints as numbered HTML sections with Markdown inline formatting. */
    static String revealedHtml(List<String> hints, int revealed) {
        int count = Math.min(Math.max(revealed, 0), hints.size());
        if (count == 0) {
            return "<p>Hints are optional. Reveal one only when you want help.</p>";
        }
        StringBuilder html = new StringBuilder();
        for (int index = 0; index < count; index++) {
            html.append("<h3>Hint ").append(index + 1).append(" of ").append(hints.size()).append("</h3>")
                    .append(MarkdownHtml.toHtml(hints.get(index)));
        }
        return html.toString();
    }

    /** The hints revealed so far as plain text, or an invitation when none are. */
    static String revealedText(List<String> hints, int revealed) {
        int count = Math.min(Math.max(revealed, 0), hints.size());
        String text = String.join("\n\n", hints.subList(0, count));
        return text.isEmpty() ? "Hints are optional. Reveal one only when you want help." : text;
    }
}
