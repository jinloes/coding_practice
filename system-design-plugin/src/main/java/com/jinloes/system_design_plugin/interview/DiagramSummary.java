package com.jinloes.system_design_plugin.interview;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Turns an Excalidraw scene into plain text the interviewer can read: labeled components, free text, and
 * directional or undirected connections ({@code ->}, {@code <-}, {@code <->}, {@code --}) grouped by frame.
 * Output depends only on the scene content, not on element z-order.
 */
public final class DiagramSummary {
    public static final int MAX_ELEMENTS = 500;
    static final String MALFORMED = "The diagram could not be read because its file is not valid Excalidraw JSON.";
    static final String EMPTY = "The diagram is empty.";
    private static final String NO_FRAME = "";
    private static final String UNCONNECTED = "(nothing)";

    private DiagramSummary() {
    }

    private record Element(JsonObject json, String id, String type) {
        String frameId() {
            return string(json, "frameId");
        }
    }

    private static final class Section {
        final List<String> components = new ArrayList<>();
        final List<String> texts = new ArrayList<>();
        final List<String> connections = new ArrayList<>();
        final Map<String, Integer> other = new TreeMap<>();
    }

    public static String summarize(String sceneJson) {
        JsonArray array;
        try {
            JsonElement root = JsonParser.parseString(sceneJson == null ? "" : sceneJson);
            if (!(root instanceof JsonObject object) || !(object.get("elements") instanceof JsonArray elements)) {
                return MALFORMED;
            }
            array = elements;
        } catch (JsonParseException exception) {
            return MALFORMED;
        }

        List<Element> raw = new ArrayList<>();
        Map<String, Element> nonFramesById = new HashMap<>();
        Map<String, String> frameNames = new HashMap<>();
        for (JsonElement value : array) {
            if (!(value instanceof JsonObject json) || json.has("isDeleted") && isTrue(json.get("isDeleted"))) {
                continue;
            }
            String type = string(json, "type");
            if (type.isEmpty()) {
                continue;
            }
            Element element = new Element(json, string(json, "id"), type);
            if (isFrame(type)) {
                if (!element.id().isEmpty()) {
                    String name = string(json, "name");
                    frameNames.put(element.id(), name.isBlank() ? "Untitled frame" : name.strip());
                }
                continue;
            }
            raw.add(element);
            if (!element.id().isEmpty()) {
                nonFramesById.put(element.id(), element);
            }
        }

        Map<String, String> labels = new HashMap<>();
        for (Element element : raw) {
            String container = string(element.json(), "containerId");
            if ("text".equals(element.type()) && nonFramesById.containsKey(container)) {
                String labelText = text(element);
                if (!labelText.isEmpty()) {
                    labels.merge(container, labelText, (first, second) -> first + " " + second);
                }
            }
        }

        List<Element> live = new ArrayList<>();
        int omitted = 0;
        for (Element element : raw) {
            if ("text".equals(element.type())
                    && (nonFramesById.containsKey(string(element.json(), "containerId")) || text(element).isEmpty())) {
                continue;
            }
            if (live.size() >= MAX_ELEMENTS) {
                omitted++;
                continue;
            }
            live.add(element);
        }
        if (live.isEmpty()) {
            return EMPTY;
        }

        Map<String, Element> byId = new HashMap<>();
        for (Element element : live) {
            if (!element.id().isEmpty()) {
                byId.put(element.id(), element);
            }
        }
        Map<String, String> shapeRefs = disambiguateShapes(live, byId, labels);

        Map<String, Section> sections = new TreeMap<>(Comparator.comparing((String key) -> !key.isEmpty())
                .thenComparing(key -> frameNames.getOrDefault(key, "")).thenComparing(key -> key));
        for (Element element : live) {
            String frameId = frameNames.containsKey(element.frameId()) ? element.frameId() : NO_FRAME;
            Section section = sections.computeIfAbsent(frameId, ignored -> new Section());
            switch (element.type()) {
                case "text" -> section.texts.add(quote(text(element)));
                case "arrow", "line" -> section.connections.add(connection(element, byId, frameNames, labels, shapeRefs));
                case "rectangle", "ellipse", "diamond" -> section.components.add(componentEntry(element, labels, shapeRefs));
                default -> section.other.merge(element.type(), 1, Integer::sum);
            }
        }

        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, Section> entry : sections.entrySet()) {
            Section section = entry.getValue();
            out.append(entry.getKey().isEmpty() ? "Outside any frame:" : "Frame " + quote(frameNames.get(entry.getKey())) + ":")
                    .append('\n');
            appendList(out, "Components", section.components);
            appendList(out, "Text", section.texts);
            appendList(out, "Connections", section.connections);
            if (!section.other.isEmpty()) {
                List<String> counts = new ArrayList<>();
                section.other.forEach((type, count) -> counts.add(count + " " + type));
                out.append("  Other elements: ").append(String.join(", ", counts)).append('\n');
            }
        }
        if (omitted > 0) {
            out.append("[").append(omitted).append(" more elements omitted; only the first ")
                    .append(MAX_ELEMENTS).append(" are summarized]\n");
        }
        return out.toString().stripTrailing();
    }

    private static Map<String, String> disambiguateShapes(List<Element> live, Map<String, Element> byId,
                                                          Map<String, String> labels) {
        Set<String> connected = new HashSet<>();
        for (Element element : live) {
            if ("arrow".equals(element.type()) || "line".equals(element.type())) {
                addBoundId(element.json(), "startBinding", byId, connected);
                addBoundId(element.json(), "endBinding", byId, connected);
            }
        }
        Map<String, List<Element>> byLabel = new HashMap<>();
        Map<String, List<Element>> unlabeledByType = new HashMap<>();
        for (Element element : live) {
            if (!isShape(element.type()) || element.id().isEmpty()) {
                continue;
            }
            String label = labels.get(element.id());
            if (label != null) {
                byLabel.computeIfAbsent(label, ignored -> new ArrayList<>()).add(element);
            } else {
                unlabeledByType.computeIfAbsent(element.type(), ignored -> new ArrayList<>()).add(element);
            }
        }
        Comparator<Element> spatialOrder = Comparator
                .comparingDouble((Element element) -> coordinate(element.json(), "y"))
                .thenComparingDouble(element -> coordinate(element.json(), "x"))
                .thenComparing(Element::id);
        Map<String, String> refs = new HashMap<>();
        for (Map.Entry<String, List<Element>> entry : byLabel.entrySet()) {
            List<Element> group = entry.getValue();
            if (group.size() == 1) {
                refs.put(group.get(0).id(), quote(entry.getKey()));
            } else {
                group.sort(spatialOrder);
                for (int index = 0; index < group.size(); index++) {
                    refs.put(group.get(index).id(), quote(entry.getKey()) + " (#" + (index + 1) + ")");
                }
            }
        }
        for (Map.Entry<String, List<Element>> entry : unlabeledByType.entrySet()) {
            List<Element> group = entry.getValue();
            if (group.size() > 1 && group.stream().anyMatch(element -> connected.contains(element.id()))) {
                group.sort(spatialOrder);
                for (int index = 0; index < group.size(); index++) {
                    refs.put(group.get(index).id(), entry.getKey() + " (unlabeled #" + (index + 1) + ")");
                }
            }
        }
        return refs;
    }

    private static void addBoundId(JsonObject arrow, String member, Map<String, Element> byId, Set<String> out) {
        if (arrow.get(member) instanceof JsonObject binding) {
            String targetId = string(binding, "elementId");
            if (byId.containsKey(targetId)) {
                out.add(targetId);
            }
        }
    }

    private static String componentEntry(Element shape, Map<String, String> labels, Map<String, String> shapeRefs) {
        if (labels.containsKey(shape.id())) {
            return shape.type() + " " + shapeRefs.getOrDefault(shape.id(), quote(labels.get(shape.id())));
        }
        return shapeRefs.getOrDefault(shape.id(), shape.type() + " (unlabeled)");
    }

    private static String connection(Element arrow, Map<String, Element> byId, Map<String, String> frameNames,
                                     Map<String, String> labels, Map<String, String> shapeRefs) {
        String source = endpoint(arrow.json(), "startBinding", byId, frameNames, labels, shapeRefs);
        String target = endpoint(arrow.json(), "endBinding", byId, frameNames, labels, shapeRefs);
        String label = labels.getOrDefault(arrow.id(), "");
        String line = source + connector(arrow) + target + (label.isBlank() ? "" : ": " + label);
        boolean unconnected = source.equals(UNCONNECTED) || target.equals(UNCONNECTED);
        return (unconnected ? "[unconnected " + arrow.type() + "] " : "") + line;
    }

    private static String connector(Element element) {
        boolean start = hasArrowhead(element.json(), "startArrowhead", false);
        boolean end = hasArrowhead(element.json(), "endArrowhead", "arrow".equals(element.type()));
        if (start && end) {
            return " <-> ";
        }
        if (start) {
            return " <- ";
        }
        return end ? " -> " : " -- ";
    }

    private static boolean hasArrowhead(JsonObject object, String member, boolean defaultWhenAbsent) {
        if (!object.has(member)) {
            return defaultWhenAbsent;
        }
        return object.get(member) instanceof JsonPrimitive primitive
                && primitive.isString() && !primitive.getAsString().isBlank();
    }

    private static String endpoint(JsonObject arrow, String member, Map<String, Element> byId,
                                   Map<String, String> frameNames, Map<String, String> labels,
                                   Map<String, String> shapeRefs) {
        if (!(arrow.get(member) instanceof JsonObject binding)) {
            return UNCONNECTED;
        }
        String targetId = string(binding, "elementId");
        Element target = byId.get(targetId);
        if (target == null) {
            String frameName = frameNames.get(targetId);
            return frameName == null ? UNCONNECTED : "frame " + quote(frameName);
        }
        if ("text".equals(target.type())) {
            return quote(text(target));
        }
        String ref = shapeRefs.get(target.id());
        if (ref != null) {
            return ref;
        }
        String label = labels.get(target.id());
        return label == null ? target.type() + " (unlabeled)" : quote(label);
    }

    private static boolean isShape(String type) {
        return "rectangle".equals(type) || "ellipse".equals(type) || "diamond".equals(type);
    }

    private static double coordinate(JsonObject object, String member) {
        return object.get(member) instanceof JsonPrimitive primitive && primitive.isNumber()
                ? primitive.getAsDouble() : 0.0;
    }

    private static void appendList(StringBuilder out, String title, List<String> values) {
        if (values.isEmpty()) {
            return;
        }
        List<String> sorted = new ArrayList<>(values);
        sorted.sort(null);
        out.append("  ").append(title).append(":\n");
        for (String value : sorted) {
            out.append("  - ").append(value).append('\n');
        }
    }

    private static boolean isFrame(String type) {
        return "frame".equals(type) || "magicframe".equals(type);
    }

    private static String text(Element element) {
        String original = string(element.json(), "originalText");
        String value = original.isEmpty() ? string(element.json(), "text") : original;
        return value.replaceAll("\\s+", " ").strip();
    }

    private static String quote(String value) {
        return "\"" + value + "\"";
    }

    private static boolean isTrue(JsonElement value) {
        return value instanceof JsonPrimitive primitive && primitive.isBoolean() && primitive.getAsBoolean();
    }

    private static String string(JsonObject object, String member) {
        return object.get(member) instanceof JsonPrimitive primitive && primitive.isString()
                ? primitive.getAsString() : "";
    }
}
