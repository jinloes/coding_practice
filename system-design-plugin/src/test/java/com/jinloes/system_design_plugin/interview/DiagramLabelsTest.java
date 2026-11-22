package com.jinloes.system_design_plugin.interview;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiagramLabelsTest {
    private static String scene(String... elements) {
        return "{\"type\":\"excalidraw\",\"elements\":[" + String.join(",", elements) + "]}";
    }

    private static String box(String id, String type, double x, double y, double width, double height, String frameId) {
        return "{\"id\":\"" + id + "\",\"type\":\"" + type + "\",\"x\":" + x + ",\"y\":" + y
                + ",\"width\":" + width + ",\"height\":" + height
                + (frameId == null ? "" : ",\"frameId\":\"" + frameId + "\"") + "}";
    }

    private static String freeText(String id, double x, double y, double width, double height, String text, String frameId) {
        return "{" + (id == null ? "" : "\"id\":\"" + id + "\",")
                + "\"type\":\"text\",\"x\":" + x + ",\"y\":" + y
                + ",\"width\":" + width + ",\"height\":" + height
                + ",\"text\":\"" + text + "\""
                + (frameId == null ? "" : ",\"frameId\":\"" + frameId + "\"") + "}";
    }

    private static String arrow(String id, String from, String to) {
        return "{\"id\":\"" + id + "\",\"type\":\"arrow\""
                + (from == null ? "" : ",\"startBinding\":{\"elementId\":\"" + from + "\"}")
                + (to == null ? "" : ",\"endBinding\":{\"elementId\":\"" + to + "\"}") + "}";
    }

    @Test
    void attachesUnboundTextInsideShapesAndPreservesConnectedFlow() {
        String summary = DiagramSummary.summarize(scene(
                box("gw", "rectangle", 10, 20, 120, 60, null),
                freeText("tgw", 30, 40, 80, 20, "API Gateway", null),
                box("cache", "ellipse", 200, 20, 100, 60, null),
                freeText("tcache", 220, 40, 60, 20, "Redis", null),
                box("router", "diamond", 360, 20, 100, 80, null),
                freeText(null, 410, 60, -10, 0, "Shard Router", null),
                arrow("a1", "gw", "cache"),
                arrow("a2", "cache", "router")));

        assertThat(summary).isEqualTo("""
                Outside any frame:
                  Components:
                  - diamond "Shard Router"
                  - ellipse "Redis"
                  - rectangle "API Gateway"
                  Connections:
                  - "API Gateway" -> "Redis"
                  - "Redis" -> "Shard Router\"""");
    }

    @Test
    void picksSmallestEnclosingShapeBreaksEqualAreaTiesAndOrdersMultiLineLabelsDeterministically() {
        List<String> elements = new ArrayList<>(List.of(
                box("outer", "rectangle", 0, 0, 300, 200, null),
                box("inner", "rectangle", 40, 40, 100, 80, null),
                freeText("line2", 55, 80, 60, 16, "Cluster", null),
                freeText("line1", 55, 55, 60, 16, "Postgres", null),
                freeText("outerLabel", 200, 20, 80, 20, "VPC", null),
                box("tieB", "rectangle", 400, 20, 100, 50, null),
                box("tieA", "rectangle", 400, 20, 100, 50, null),
                freeText("tieText", 420, 35, 40, 16, "Worker", null)));

        String forward = DiagramSummary.summarize(scene(elements.toArray(String[]::new)));
        Collections.reverse(elements);
        String reversed = DiagramSummary.summarize(scene(elements.toArray(String[]::new)));

        assertThat(forward).isEqualTo(reversed).isEqualTo("""
                Outside any frame:
                  Components:
                  - rectangle "Postgres Cluster"
                  - rectangle "VPC"
                  - rectangle "Worker"
                  - rectangle (unlabeled)""");
    }

    @Test
    void explicitContainerLabelsNeverAbsorbOverlappingFreeTextAndFramesIsolateContainment() {
        String summary = DiagramSummary.summarize(scene(
                "{\"id\":\"f1\",\"type\":\"frame\",\"name\":\"Data plane\"}",
                "{\"id\":\"f2\",\"type\":\"frame\",\"name\":\"Control plane\"}",
                box("explicit", "rectangle", 10, 10, 120, 60, null),
                "{\"id\":\"bound\",\"type\":\"text\",\"containerId\":\"explicit\",\"text\":\"Auth\"}",
                freeText("callout", 20, 25, 60, 16, "p99 < 15ms", null),
                box("framedBox", "rectangle", 10, 10, 120, 60, "f1"),
                freeText("unframedAtSameCoords", 20, 25, 60, 16, "Outside note", "missing-frame"),
                freeText("otherFrameAtSameCoords", 20, 25, 60, 16, "Control note", "f2"),
                freeText("matchingFrameText", 20, 25, 60, 16, "Ingestor", "f1")));

        assertThat(summary).isEqualTo("""
                Outside any frame:
                  Components:
                  - rectangle "Auth"
                  Text:
                  - "Outside note"
                  - "p99 < 15ms"
                Frame "Control plane":
                  Text:
                  - "Control note"
                Frame "Data plane":
                  Components:
                  - rectangle "Ingestor\"""");
    }

    @Test
    void checksAllFourBoundingEdgesAndHandlesInvalidCoordinatesGracefully() {
        String summary = DiagramSummary.summarize(scene(
                box("target", "rectangle", 100, 100, 100, 50, null),
                freeText("onEdge", 100, 100, 0, 0, "Edge", null),
                freeText("left", 90, 120, 0, 0, "Left", null),
                freeText("right", 210, 120, 0, 0, "Right", null),
                freeText("above", 150, 90, 0, 0, "Above", null),
                freeText("below", 150, 160, 0, 0, "Below", null),
                freeText("tieB", 120, 120, 0, 0, "Zeta", null),
                freeText("tieA", 120, 120, 0, 0, "Alpha", null),
                "{\"type\":\"rectangle\",\"x\":0,\"y\":0,\"width\":50,\"height\":50}",
                "{\"id\":\"badX\",\"type\":\"rectangle\",\"x\":\"bad\",\"y\":0,\"width\":50,\"height\":50}",
                "{\"id\":\"infY\",\"type\":\"rectangle\",\"x\":0,\"y\":1e400,\"width\":50,\"height\":50}",
                "{\"id\":\"zeroW\",\"type\":\"rectangle\",\"x\":0,\"y\":0,\"width\":0,\"height\":50}",
                "{\"id\":\"negH\",\"type\":\"rectangle\",\"x\":0,\"y\":0,\"width\":50,\"height\":-5}",
                "{\"id\":\"textNoX\",\"type\":\"text\",\"y\":120,\"text\":\"NoX\"}",
                "{\"id\":\"textInfY\",\"type\":\"text\",\"x\":120,\"y\":1e400,\"text\":\"InfY\"}",
                "{\"id\":\"danglingContainer\",\"type\":\"text\",\"containerId\":\"missing\",\"x\":200,\"y\":150,\"text\":\"Corner\"}"));

        assertThat(summary).contains(
                "- rectangle \"Edge Alpha Zeta Corner\"",
                "- \"Above\"",
                "- \"Below\"",
                "- \"InfY\"",
                "- \"Left\"",
                "- \"NoX\"",
                "- \"Right\"");
    }

    @Test
    void redirectsArrowsBoundToConsumedTextIncludingWhenEnclosingShapeExceedsElementCap() {
        String redirected = DiagramSummary.summarize(scene(
                box("s1", "rectangle", 10, 10, 100, 50, null),
                freeText("t1", 30, 25, 40, 16, "Worker", null),
                box("s2", "rectangle", 200, 10, 100, 50, null),
                freeText("t2", 220, 25, 40, 16, "Worker", null),
                arrow("arr", "t2", "t1")));

        assertThat(redirected).contains(
                "- rectangle \"Worker\" (#1)",
                "- rectangle \"Worker\" (#2)",
                "- \"Worker\" (#2) -> \"Worker\" (#1)");

        List<String> capped = new ArrayList<>();
        capped.add(box("front", "rectangle", 10, 10, 100, 50, null));
        capped.add(freeText("tfront", 30, 25, 40, 16, "Gateway", null));
        capped.add(arrow("toOmitted", "tfront", "tomitted"));
        for (int index = 0; index < DiagramSummary.MAX_ELEMENTS; index++) {
            capped.add("{\"id\":\"pad-" + index + "\",\"type\":\"ellipse\"}");
        }
        capped.add(box("omittedShape", "rectangle", 500, 10, 100, 50, null));
        capped.add(freeText("tomitted", 520, 25, 40, 16, "Billing DB", null));

        String cappedSummary = DiagramSummary.summarize(scene(capped.toArray(String[]::new)));
        assertThat(cappedSummary)
                .contains("- \"Gateway\" -> \"Billing DB\"")
                .doesNotContain("[unconnected arrow]")
                .endsWith("[3 more elements omitted; only the first 500 are summarized]");
    }
}
