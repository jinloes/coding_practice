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
    }

    @Test
    void rejectsMalformedOrUnknownMessages() {
        for (String message : new String[]{
                "not json", "[]", "{}", "{\"type\":\"selectGuide\"}", "{\"id\":\"graphs\"}",
                "{\"type\":\"deleteEverything\",\"id\":\"graphs\"}", "{\"type\":{},\"id\":\"graphs\"}"}) {
            assertThatIllegalArgumentException()
                    .as(message)
                    .isThrownBy(() -> PageEvent.parse(message));
        }
    }
}
