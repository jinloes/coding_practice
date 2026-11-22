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
        assertThat(button.getText()).isEqualTo("Reveal Hint");
        assertThat(button.getToolTipText()).isEqualTo("Start or resume an attempt to reveal hints");

        hint.update(true);
        assertThat(button.isEnabled()).isTrue();
        assertThat(button.getText()).isEqualTo("Reveal Hint 1 of 3");
        assertThat(button.getToolTipText()).as("no reason is shown while enabled").isNull();

        hint.update(true, 2, 3);
        assertThat(button.isEnabled()).isTrue();
        assertThat(button.getText()).isEqualTo("Reveal Hint 3 of 3");
        assertThat(button.getToolTipText()).isNull();

        hint.update(true, 3, 3);
        assertThat(button.isEnabled()).isFalse();
        assertThat(button.getText()).isEqualTo("All 3 Hints Revealed");
        assertThat(button.getToolTipText()).isEqualTo("All hints for this exercise have been revealed");

        hint.update(false);
        assertThat(button.isEnabled()).isFalse();
        assertThat(button.getText()).isEqualTo("Reveal Hint");
        assertThat(button.getToolTipText()).isEqualTo("Start or resume an attempt to reveal hints");
    }

    @Test
    void tabTitleShowsRevealedCountOnlyWhenAnAttemptHasRevealedHints() {
        assertThat(HintButton.tabTitle(false, 2, 3)).isEqualTo("Hints");
        assertThat(HintButton.tabTitle(true, 0, 3)).isEqualTo("Hints");
        assertThat(HintButton.tabTitle(true, 2, 3)).isEqualTo("Hints (2/3)");
        assertThat(HintButton.tabTitle(true, 5, 3)).isEqualTo("Hints (3/3)");
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

    @Test
    void revealedHtmlFormatsHeadingsAndInlineCodeSafely() {
        List<String> hints = List.of("Track `Map<Integer, Integer>` indices.", "Check `< 0` bounds.", "Return early.");

        assertThat(HintButton.revealedHtml(hints, 0))
                .isEqualTo("<p>Hints are optional. Reveal one only when you want help.</p>");
        assertThat(HintButton.revealedHtml(hints, 2))
                .contains("<h3>Hint 1 of 3</h3>")
                .contains("<code>Map&lt;Integer, Integer&gt;</code>")
                .contains("<h3>Hint 2 of 3</h3>")
                .contains("<code>&lt; 0</code>")
                .doesNotContain("Hint 3 of 3");
    }
}
