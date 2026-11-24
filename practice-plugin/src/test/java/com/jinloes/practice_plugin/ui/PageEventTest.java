package com.jinloes.practice_plugin.ui;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PageEventTest {

    @Test
    void parsesEachEventType() {
        assertThat(PageEvent.parse("{\"type\":\"selectExercise\",\"id\":\"pair-sum\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.SELECT_EXERCISE, "pair-sum"));
        assertThat(PageEvent.parse("{\"type\":\"selectGuide\",\"id\":\"graphs\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.SELECT_GUIDE, "graphs"));
        assertThat(PageEvent.parse("{\"type\":\"selectTrack\",\"id\":\"google\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.SELECT_TRACK, "google"));
        assertThat(PageEvent.parse("{\"type\":\"clearFilters\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.CLEAR_FILTERS, ""));
        assertThat(PageEvent.parse("{\"type\":\"startOrOpen\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.START_OR_OPEN, ""));
        assertThat(PageEvent.parse("{\"type\":\"runExamples\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.RUN_EXAMPLES, ""));
        assertThat(PageEvent.parse("{\"type\":\"runSample\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.RUN_SAMPLE, ""));
        assertThat(PageEvent.parse("{\"type\":\"revealHint\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.REVEAL_HINT, ""));
        assertThat(PageEvent.parse("{\"type\":\"openCoach\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.OPEN_COACH, ""));
        assertThat(PageEvent.parse("{\"type\":\"coachDiagnose\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_DIAGNOSE, ""));
        assertThat(PageEvent.parse("{\"type\":\"coachTranslate\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_TRANSLATE, ""));
        assertThat(PageEvent.parse("{\"type\":\"coachCompare\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_COMPARE, ""));
        assertThat(PageEvent.parse("{\"type\":\"coachFollowUp\",\"text\":\"Why O(n)?\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_FOLLOW_UP, "Why O(n)?"));
        assertThat(PageEvent.parse("{\"type\":\"coachCancel\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_CANCEL, ""));
        assertThat(PageEvent.parse("{\"type\":\"coachClear\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_CLEAR, ""));
        assertThat(PageEvent.parse("{\"type\":\"coachSettings\"}"))
                .isEqualTo(new PageEvent(PageEvent.Type.COACH_SETTINGS, ""));
    }

    @Test
    void rejectsMalformedOrUnknownMessages() {
        for (String message : new String[]{
                "not json", "[]", "{}", "{\"type\":\"selectGuide\"}", "{\"type\":\"selectGuide\",\"id\":\"  \"}",
                "{\"type\":\"selectExercise\",\"id\":42}", "{\"type\":true}", "{\"id\":\"graphs\"}",
                "{\"type\":\"coachFollowUp\"}", "{\"type\":\"coachFollowUp\",\"text\":\"   \"}",
                "{\"type\":\"deleteEverything\",\"id\":\"graphs\"}", "{\"type\":{},\"id\":\"graphs\"}"}) {
            assertThatIllegalArgumentException()
                    .as(message)
                    .isThrownBy(() -> PageEvent.parse(message));
        }
    }
}
