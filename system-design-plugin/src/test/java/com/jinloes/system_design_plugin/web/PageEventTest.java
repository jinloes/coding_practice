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
                "revealHint", Type.REVEAL_HINT, "requestFeedback", Type.REQUEST_FEEDBACK);
        withId.forEach((wire, type) -> assertThat(PageEvent.parse("{\"type\":\"" + wire + "\",\"id\":\"url-1\"}"))
                .isEqualTo(new PageEvent(type, "url-1")));
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
                "{\"type\":\"selectProblem\",\"id\":\"A\"}", "{\"type\":\"newAttempt\",\"id\":\"" + "a".repeat(201) + "\"}"}) {
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
        assertThat(PageEvent.parseEditor("{\"type\":\"ready\"}")).isEqualTo(new EditorMessage(EditorMessage.Kind.READY, null));
        assertThat(PageEvent.parseEditor("{\"type\":\"flushed\"}"))
                .isEqualTo(new EditorMessage(EditorMessage.Kind.FLUSHED, null));
        String save = "{\"type\":\"save\",\"scene\":" + new Gson().toJson(SCENE) + "}";
        assertThat(PageEvent.parseEditor(save)).isEqualTo(new EditorMessage(EditorMessage.Kind.SAVE, SCENE));
    }

    @Test
    void rejectsBadEditorMessages() {
        for (String json : new String[]{null, "{}", "{\"type\":\"selectProblem\",\"id\":\"a\"}",
                "{\"type\":\"save\"}", "{\"type\":\"save\",\"scene\":\"not json\"}",
                "{\"type\":\"save\",\"scene\":\"{}\"}", "{\"type\":\"save\",\"scene\":\"{\\\"type\\\":\\\"other\\\"}\"}",
                "{\"type\":\"save\",\"scene\":\"{\\\"type\\\":{}}\"}"}) {
            assertThatThrownBy(() -> PageEvent.parseEditor(json)).as(String.valueOf(json))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void rejectsOversizeScenes() {
        String padding = "x".repeat(PageEvent.MAX_SCENE_BYTES);
        String bigScene = "{\\\"type\\\":\\\"excalidraw\\\",\\\"p\\\":\\\"" + padding + "\\\"}";
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"scene\":\"" + bigScene + "\"}"))
                .hasMessageContaining("was not saved");
        String multibyte = "é".repeat(PageEvent.MAX_SCENE_BYTES / 2 + 1);
        String scene = "{\\\"type\\\":\\\"excalidraw\\\",\\\"p\\\":\\\"" + multibyte + "\\\"}";
        assertThatThrownBy(() -> PageEvent.parseEditor("{\"type\":\"save\",\"scene\":\"" + scene + "\"}"))
                .hasMessageContaining("was not saved");
    }
}
