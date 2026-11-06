package com.jinloes.system_design_plugin.interview;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiagramSummaryTest {
    private static String scene(String... elements) {
        return "{\"type\":\"excalidraw\",\"elements\":[" + String.join(",", elements) + "]}";
    }

    private static String shape(String id, String type, String frameId) {
        return "{\"id\":\"" + id + "\",\"type\":\"" + type + "\""
                + (frameId == null ? "" : ",\"frameId\":\"" + frameId + "\"") + "}";
    }

    private static String label(String id, String containerId, String text) {
        return "{\"id\":\"" + id + "\",\"type\":\"text\",\"containerId\":\"" + containerId
                + "\",\"text\":\"" + text + "\",\"originalText\":\"" + text + "\"}";
    }

    private static String arrow(String id, String from, String to) {
        return "{\"id\":\"" + id + "\",\"type\":\"arrow\""
                + (from == null ? "" : ",\"startBinding\":{\"elementId\":\"" + from + "\"}")
                + (to == null ? "" : ",\"endBinding\":{\"elementId\":\"" + to + "\"}") + "}";
    }

    @Test
    void listsLabeledShapesTextAndConnectionsUsingBindings() {
        String summary = DiagramSummary.summarize(scene(
                shape("api", "rectangle", null), label("t1", "api", "API  gateway"),
                shape("db", "ellipse", null), label("t2", "db", "Postgres"),
                shape("blank", "diamond", null),
                "{\"id\":\"note\",\"type\":\"text\",\"text\":\"Read heavy\\n100:1\"}",
                "{\"id\":\"empty\",\"type\":\"text\",\"text\":\"  \"}",
                arrow("a1", "api", "db"), label("t3", "a1", "writes"),
                arrow("a2", "api", null),
                arrow("a3", "note", "blank"),
                arrow("a4", "ghost", "db"),
                "{\"id\":\"line\",\"type\":\"line\"}",
                "{\"id\":\"pen\",\"type\":\"freedraw\"}",
                "{\"id\":\"pen2\",\"type\":\"freedraw\"}",
                "{\"id\":\"img\",\"type\":\"image\"}"));

        assertThat(summary).isEqualTo("""
                Outside any frame:
                  Components:
                  - diamond (unlabeled)
                  - ellipse "Postgres"
                  - rectangle "API gateway"
                  Text:
                  - "Read heavy 100:1"
                  Connections:
                  - "API gateway" -> "Postgres": writes
                  - "Read heavy 100:1" -> diamond (unlabeled)
                  - [unconnected arrow] "API gateway" -> (nothing)
                  - [unconnected arrow] (nothing) -> "Postgres"
                  - [unconnected line] (nothing) -> (nothing)
                  Other elements: 2 freedraw, 1 image""");
    }

    @Test
    void groupsElementsByFrameAndSkipsDeletedOnes() {
        String summary = DiagramSummary.summarize(scene(
                "{\"id\":\"f2\",\"type\":\"frame\",\"name\":\"Write path\"}",
                "{\"id\":\"f1\",\"type\":\"magicframe\",\"name\":\" \"}",
                shape("svc", "rectangle", "f2"), label("t1", "svc", "Writer"),
                shape("cache", "rectangle", "f1"), label("t2", "cache", "Cache"),
                shape("loose", "rectangle", "missing-frame"),
                "{\"id\":\"gone\",\"type\":\"rectangle\",\"isDeleted\":true}",
                "{\"id\":\"kept\",\"type\":\"ellipse\",\"isDeleted\":false}",
                "{\"type\":\"\"}", "[]", "{\"id\":\"noType\"}"));

        assertThat(summary).isEqualTo("""
                Outside any frame:
                  Components:
                  - ellipse (unlabeled)
                  - rectangle (unlabeled)
                Frame "Untitled frame":
                  Components:
                  - rectangle "Cache"
                Frame "Write path":
                  Components:
                  - rectangle "Writer\"""");
    }

    @Test
    void outputDoesNotDependOnElementOrder() {
        List<String> elements = new ArrayList<>(List.of(
                shape("a", "rectangle", null), label("ta", "a", "A"),
                shape("b", "rectangle", null), label("tb", "b", "B"),
                arrow("x", "a", "b"), arrow("y", "b", "a")));
        String first = DiagramSummary.summarize(scene(elements.toArray(String[]::new)));
        Collections.reverse(elements);
        assertThat(DiagramSummary.summarize(scene(elements.toArray(String[]::new)))).isEqualTo(first);
    }

    @Test
    void capsTheNumberOfSummarizedElements() {
        List<String> elements = new ArrayList<>();
        for (int index = 0; index < DiagramSummary.MAX_ELEMENTS + 7; index++) {
            elements.add(shape("s" + index, "rectangle", null));
        }
        String summary = DiagramSummary.summarize(scene(elements.toArray(String[]::new)));
        assertThat(summary).endsWith("[7 more elements omitted; only the first 500 are summarized]");
        assertThat(summary.lines().filter(line -> line.contains("rectangle"))).hasSize(500);
    }

    @Test
    void reportsMalformedAndEmptyScenesClearly() {
        assertThat(DiagramSummary.summarize("{not json")).isEqualTo(DiagramSummary.MALFORMED);
        assertThat(DiagramSummary.summarize(null)).isEqualTo(DiagramSummary.MALFORMED);
        assertThat(DiagramSummary.summarize("[]")).isEqualTo(DiagramSummary.MALFORMED);
        assertThat(DiagramSummary.summarize("{\"elements\":{}}")).isEqualTo(DiagramSummary.MALFORMED);
        assertThat(DiagramSummary.summarize(scene())).isEqualTo(DiagramSummary.EMPTY);
        assertThat(DiagramSummary.summarize(scene("{\"id\":\"d\",\"type\":\"text\",\"isDeleted\":true}")))
                .isEqualTo(DiagramSummary.EMPTY);
    }
}
