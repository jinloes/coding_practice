package com.jinloes.practice_plugin.ui;

import javax.swing.JButton;
import java.util.List;

/** The "Reveal Next Hint" button, enabled only while an attempt is selected. */
final class HintButton {
    static final String NO_ATTEMPT_TOOLTIP = "Start or resume an attempt to reveal hints";

    private final JButton button = new JButton("Reveal Next Hint");

    HintButton(Runnable onClick) {
        button.addActionListener(event -> onClick.run());
        update(false);
    }

    JButton component() {
        return button;
    }

    void update(boolean attemptSelected) {
        button.setEnabled(attemptSelected);
        button.setToolTipText(attemptSelected ? null : NO_ATTEMPT_TOOLTIP);
    }

    /** The hints revealed so far, or an invitation when none are. */
    static String revealedText(List<String> hints, int revealed) {
        String text = String.join("\n\n", hints.subList(0, Math.min(revealed, hints.size())));
        return text.isEmpty() ? "Hints are optional. Reveal one only when you want help." : text;
    }
}
