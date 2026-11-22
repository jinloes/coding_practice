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
    }

    @Test
    void rejectsMalformedOrUnknownMessages() {
        for (String message : new String[]{
                "not json", "[]", "{}", "{\"type\":\"selectGuide\"}", "{\"type\":\"selectGuide\",\"id\":\"  \"}",
                "{\"type\":\"selectExercise\",\"id\":42}", "{\"type\":true}", "{\"id\":\"graphs\"}",
                "{\"type\":\"deleteEverything\",\"id\":\"graphs\"}", "{\"type\":{},\"id\":\"graphs\"}"}) {
            assertThatIllegalArgumentException()
                    .as(message)
                    .isThrownBy(() -> PageEvent.parse(message));
        }
    }
}
