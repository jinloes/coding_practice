package com.jinloes.practice_plugin.ui;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class HintButtonTest {
    @Test
    void startsDisabledWithItsReasonAndFollowsAttemptSelection() {
        HintButton hint = new HintButton(() -> { });
        var button = hint.component();
        assertThat(button.isEnabled()).isFalse();
        assertThat(button.getToolTipText()).isEqualTo("Start or resume an attempt to reveal hints");

        hint.update(true);
        assertThat(button.isEnabled()).isTrue();
        assertThat(button.getToolTipText()).as("no reason is shown while enabled").isNull();

        hint.update(false);
        assertThat(button.isEnabled()).isFalse();
        assertThat(button.getToolTipText()).isEqualTo("Start or resume an attempt to reveal hints");
    }

    @Test
    void clickingRunsTheActionOnlyWhileEnabled() {
        AtomicInteger clicks = new AtomicInteger();
        HintButton hint = new HintButton(clicks::incrementAndGet);

        hint.component().doClick();
        assertThat(clicks).as("a disabled button ignores clicks").hasValue(0);

        hint.update(true);
        hint.component().doClick();
        assertThat(clicks).hasValue(1);
    }

    @Test
    void revealedTextJoinsRevealedHintsOrInvitesTheLearner() {
        List<String> hints = List.of("first", "second", "third");

        assertThat(HintButton.revealedText(hints, 0))
                .isEqualTo("Hints are optional. Reveal one only when you want help.");
        assertThat(HintButton.revealedText(hints, 2)).isEqualTo("first\n\nsecond");
        assertThat(HintButton.revealedText(hints, 9)).as("capped at the hints available")
                .isEqualTo("first\n\nsecond\n\nthird");
    }
}
