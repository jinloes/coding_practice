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
                  - [unconnected line] (nothing) -- (nothing)
                  Other elements: 2 freedraw, 1 image""");
    }

    @Test
    void preservesReverseBidirectionalAndUndirectedConnectors() {
        String summary = DiagramSummary.summarize(scene(
                shape("a", "rectangle", null), label("ta", "a", "Client"),
                shape("b", "rectangle", null), label("tb", "b", "Gateway"),
                "{\"id\":\"rev\",\"type\":\"arrow\",\"startBinding\":{\"elementId\":\"a\"},"
                        + "\"endBinding\":{\"elementId\":\"b\"},\"startArrowhead\":\"arrow\",\"endArrowhead\":null}",
                "{\"id\":\"bi\",\"type\":\"arrow\",\"startBinding\":{\"elementId\":\"a\"},"
                        + "\"endBinding\":{\"elementId\":\"b\"},\"startArrowhead\":\"arrow\",\"endArrowhead\":\"arrow\"}",
                "{\"id\":\"plain\",\"type\":\"arrow\",\"startBinding\":{\"elementId\":\"a\"},"
                        + "\"endBinding\":{\"elementId\":\"b\"},\"startArrowhead\":null,\"endArrowhead\":null}",
                "{\"id\":\"ln\",\"type\":\"line\",\"startBinding\":{\"elementId\":\"a\"},"
                        + "\"endBinding\":{\"elementId\":\"b\"}}"));

        assertThat(summary).contains(
                "- \"Client\" -- \"Gateway\"",
                "- \"Client\" <- \"Gateway\"",
                "- \"Client\" <-> \"Gateway\"");
    }

    @Test
    void resolvesBoundLabelsAndFramesBeyondFiveHundredCapAndDisambiguatesDuplicates() {
        List<String> elements = new ArrayList<>();
        elements.add("{\"id\":\"r1\",\"type\":\"rectangle\",\"frameId\":\"f1\",\"x\":10,\"y\":20}");
        elements.add("{\"id\":\"r2\",\"type\":\"rectangle\",\"frameId\":\"f1\",\"x\":50,\"y\":20}");
        elements.add(arrow("conn", "r2", "r1"));
        for (int index = 0; index < DiagramSummary.MAX_ELEMENTS; index++) {
            elements.add(shape("extra-" + index, "ellipse", null));
        }
        elements.add(label("l1", "r1", "Redis"));
        elements.add(label("l2", "r2", "Redis"));
        elements.add("{\"id\":\"f1\",\"type\":\"frame\",\"name\":\"Cache tier\"}");

        String forward = DiagramSummary.summarize(scene(elements.toArray(String[]::new)));
        assertThat(forward).contains("Frame \"Cache tier\":")
                .contains("- rectangle \"Redis\" (#1)")
                .contains("- rectangle \"Redis\" (#2)")
                .contains("- \"Redis\" (#2) -> \"Redis\" (#1)")
                .endsWith("[3 more elements omitted; only the first 500 are summarized]");

        List<String> reversed = new ArrayList<>(List.of(
                "{\"id\":\"r2\",\"type\":\"rectangle\",\"x\":50,\"y\":20}",
                "{\"id\":\"r1\",\"type\":\"rectangle\",\"x\":10,\"y\":20}",
                label("l2", "r2", "Redis"),
                label("l1", "r1", "Redis"),
                arrow("conn", "r2", "r1")));
        String firstOrder = DiagramSummary.summarize(scene(reversed.toArray(String[]::new)));
        Collections.reverse(reversed);
        assertThat(DiagramSummary.summarize(scene(reversed.toArray(String[]::new)))).isEqualTo(firstOrder);

        String connectedUnlabeled = DiagramSummary.summarize(scene(
                "{\"id\":\"u2\",\"type\":\"rectangle\",\"x\":80,\"y\":10}",
                "{\"id\":\"u1\",\"type\":\"rectangle\",\"x\":10,\"y\":10}",
                arrow("a", "u1", "u2")));
        assertThat(connectedUnlabeled).contains("- rectangle (unlabeled #1)", "- rectangle (unlabeled #2)",
                "- rectangle (unlabeled #1) -> rectangle (unlabeled #2)");
    }

    @Test
    void groupsElementsByFrameAndSkipsDeletedOnes() {
        String summary = DiagramSummary.summarize(scene(
                "{\"id\":\"f2\",\"type\":\"frame\",\"name\":\"Write path\"}",
                "{\"id\":\"f1\",\"type\":\"magicframe\",\"name\":\" \"}",
                shape("svc", "rectangle", "f2"), label("t1", "svc", "Writer"),
                shape("cache", "rectangle", "f1"), label("t2", "cache", "Cache"),
                arrow("fconn", "f2", "f1"),
                shape("loose", "rectangle", "missing-frame"),
                "{\"id\":\"gone\",\"type\":\"rectangle\",\"isDeleted\":true}",
                "{\"id\":\"kept\",\"type\":\"ellipse\",\"isDeleted\":false}",
                "{\"type\":\"\"}", "[]", "{\"id\":\"noType\"}"));

        assertThat(summary).isEqualTo("""
                Outside any frame:
                  Components:
                  - ellipse (unlabeled)
                  - rectangle (unlabeled)
                  Connections:
                  - frame "Write path" -> frame "Untitled frame"
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
        assertThat(DiagramSummary.summarize(scene("{\"id\":\"f\",\"type\":\"frame\",\"name\":\"Empty\"}")))
                .isEqualTo(DiagramSummary.EMPTY);
        assertThat(DiagramSummary.summarize(scene("{\"id\":\"blank\",\"type\":\"text\",\"text\":\"   \"}")))
                .isEqualTo(DiagramSummary.EMPTY);
    }
}
