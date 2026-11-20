package com.jinloes.system_design_plugin.web;

import com.google.gson.Gson;
import com.jinloes.system_design_plugin.web.PageEvent.EditorMessage;
import com.jinloes.system_design_plugin.web.PageEvent.Type;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageEventTest {
    private static final String SCENE = "{\"type\":\"excalidraw\",\"version\":2,\"elements\":[]}";

    @Test
    void parsesEveryToolWindowEvent() {
        Map<String, Type> withId = Map.of("selectProblem", Type.SELECT_PROBLEM, "newAttempt", Type.NEW_ATTEMPT,
                "selectAttempt", Type.SELECT_ATTEMPT, "openAttempt", Type.OPEN_ATTEMPT,
                "deleteAttempt", Type.DELETE_ATTEMPT,
                "revealHint", Type.REVEAL_HINT, "requestFeedback", Type.REQUEST_FEEDBACK);
        withId.forEach((wire, type) -> assertThat(PageEvent.parse("{\"type\":\"" + wire + "\",\"id\":\"url-1\"}"))
                .isEqualTo(new PageEvent(type, "url-1")));
        assertThat(PageEvent.parse("{\"type\":\"renameAttempt\",\"id\":\"url-1\",\"label\":\"  Cache\npass  \"}"))
                .isEqualTo(new PageEvent(Type.RENAME_ATTEMPT, "url-1", "Cache pass"));
        assertThat(PageEvent.parse("{\"type\":\"submitFollowUp\",\"id\":\"url-1\",\"answers\":\"  Sharded  \"}"))
                .isEqualTo(new PageEvent(Type.SUBMIT_FOLLOW_UP, "url-1", "Sharded"));
        assertThat(PageEvent.parse("{\"type\":\"selectLevel\",\"level\":\"Staff+\"}"))
                .isEqualTo(new PageEvent(Type.SELECT_LEVEL, "", "Staff+"));
        assertThat(PageEvent.parse("{\"type\":\"cancelFeedback\"}")).isEqualTo(new PageEvent(Type.CANCEL_FEEDBACK, ""));
        assertThat(PageEvent.parse("{\"type\":\"openSettings\",\"id\":7}"))
                .isEqualTo(new PageEvent(Type.OPEN_SETTINGS, ""));
    }

    @Test
    void rejectsUnknownOrMalformedEvents() {
        for (String json : new String[]{null, "", "[]", "{", "\"x\"", "{}", "{\"type\":3}",
                "{\"type\":\"deleteEverything\",\"id\":\"a\"}", "{\"type\":\"save\",\"scene\":\"{}\"}",
                "{\"type\":\"selectProblem\"}", "{\"type\":\"selectProblem\",\"id\":5}",
                "{\"type\":\"selectProblem\",\"id\":\"../x\"}", "{\"type\":\"selectProblem\",\"id\":\"\"}",
                "{\"type\":\"selectProblem\",\"id\":\"A\"}", "{\"type\":\"newAttempt\",\"id\":\"" + "a".repeat(201) + "\"}",
                "{\"type\":\"selectLevel\",\"level\":\"Principal\"}",
                "{\"type\":\"renameAttempt\",\"id\":\"url-1\",\"label\":\"" + "a".repeat(201) + "\"}",
                "{\"type\":\"submitFollowUp\",\"id\":\"url-1\",\"answers\":\"   \"}",
                "{\"type\":\"submitFollowUp\",\"id\":\"url-1\",\"answers\":\"" + "a".repeat(40_001) + "\"}",
                "{\"type\":\"submitFollowUp\",\"id\":\"url-1\",\"answers\":\"" + "a".repeat(45_000) + "\"}"}) {
            assertThatThrownBy(() -> PageEvent.parse(json)).as(String.valueOf(json))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void truncatesLongMessagesInErrors() {
        String json = "{\"x\":\"" + "a".repeat(500) + "\"}";
        assertThatThrownBy(() -> PageEvent.parse(json)).hasMessageEndingWith("...");
    }

    @Test
    void parsesEditorMessages() {
        assertThat(PageEvent.parseEditor("{\"type\":\"ready\"}"))
                .isEqualTo(new EditorMessage(EditorMessage.Kind.READY, 0L, null));
        assertThat(PageEvent.parseEditor("{\"type\":\"flushed\",\"seq\":3}"))
                .isEqualTo(new EditorMessage(EditorMessage.Kind.FLUSHED, 3L, null));
        String save = "{\"type\":\"save\",\"seq\":7,\"scene\":" + new Gson().toJson(SCENE) + "}";
        assertThat(PageEvent.parseEditor(save)).isEqualTo(new EditorMessage(EditorMessage.Kind.SAVE, 7L, SCENE));
    }

    @Test
    void rejectsBadEditorMessages() {
        for (String json : new String[]{null, "[]", "{", "{}", "{\"type\":4}",
                "{\"type\":\"selectProblem\",\"id\":\"a\"}",
                "{\"type\":\"flushed\"}", "{\"type\":\"flushed\",\"seq\":0}",
                "{\"type\":\"flushed\",\"seq\":-1}", "{\"type\":\"flushed\",\"seq\":\"3\"}",
                "{\"type\":\"save\"}", "{\"type\":\"save\",\"seq\":0,\"scene\":\"{}\"}",
                "{\"type\":\"save\",\"seq\":-1,\"scene\":\"{}\"}",
                "{\"type\":\"save\",\"seq\":1.5,\"scene\":\"{}\"}",
                "{\"type\":\"save\",\"seq\":\"1\",\"scene\":\"{}\"}",
                "{\"type\":\"save\",\"seq\":1}",
                "{\"type\":\"save\",\"seq\":1,\"scene\":\"not json\"}",
                "{\"type\":\"save\",\"seq\":1,\"scene\":\"[]\"}",
                "{\"type\":\"save\",\"seq\":1,\"scene\":\"{}\"}",
                "{\"type\":\"save\",\"seq\":1,\"scene\":\"{\\\"type\\\":\\\"other\\\"}\"}",
                "{\"type\":\"save\",\"seq\":1,\"scene\":\"{\\\"type\\\":{}}\"}"}) {
            assertThatThrownBy(() -> PageEvent.parseEditor(json)).as(String.valueOf(json))
                    .isInstanceOf(PageEvent.InvalidSceneException.class);
        }
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"flushed\"}"))
                .isInstanceOfSatisfying(PageEvent.InvalidSceneException.class,
                        rejected -> assertThat(rejected.saveFailure()).isFalse());
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"seq\":11,"))
                .isInstanceOfSatisfying(PageEvent.InvalidSceneException.class, rejected -> {
                    assertThat(rejected.seq()).isEqualTo(11L);
                    assertThat(rejected.saveFailure()).isTrue();
                });
    }

    @Test
    void rejectsOversizeScenesPreservingExtractedSequenceWhenPresent() {
        String overCharLimit = "x".repeat(PageEvent.MAX_SCENE_BYTES + 2_000);
        String hugeScene = "{\\\"type\\\":\\\"excalidraw\\\",\\\"p\\\":\\\"" + overCharLimit + "\\\"}";
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"seq\":42,\"scene\":\"" + hugeScene + "\"}"))
                .isInstanceOfSatisfying(PageEvent.InvalidSceneException.class, rejected -> {
                    assertThat(rejected).hasMessageContaining("was not saved");
                    assertThat(rejected.seq()).isEqualTo(42L);
                    assertThat(rejected.saveFailure()).isTrue();
                });
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"scene\":\"" + hugeScene + "\"}"))
                .isInstanceOfSatisfying(PageEvent.InvalidSceneException.class, rejected -> {
                    assertThat(rejected).hasMessageContaining("was not saved");
                    assertThat(rejected.seq()).isEqualTo(-1L);
                    assertThat(rejected.saveFailure()).isTrue();
                });
        String multibyte = "é".repeat(PageEvent.MAX_SCENE_BYTES / 2 + 1);
        String scene = "{\\\"type\\\":\\\"excalidraw\\\",\\\"p\\\":\\\"" + multibyte + "\\\"}";
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"seq\":9,\"scene\":\"" + scene + "\"}"))
                .isInstanceOfSatisfying(PageEvent.InvalidSceneException.class, rejected -> {
                    assertThat(rejected).hasMessageContaining("was not saved");
                    assertThat(rejected.seq()).isEqualTo(9L);
                    assertThat(rejected.saveFailure()).isTrue();
                });
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"scene\":\"" + scene + "\"}"))
                .isInstanceOf(PageEvent.InvalidSceneException.class);
        assertThat(PageEvent.extractSeq("{\"seq\":0}")).isEqualTo(-1L);
    }
}
