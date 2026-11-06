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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Turns an Excalidraw scene into plain text the interviewer can read: labeled components, free text, and
 * connections ({@code source -> target: label}) grouped by frame. Output depends only on the scene content,
 * not on element z-order.
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

        List<Element> live = new ArrayList<>();
        int omitted = 0;
        for (JsonElement value : array) {
            if (!(value instanceof JsonObject json) || json.has("isDeleted") && isTrue(json.get("isDeleted"))) {
                continue;
            }
            String type = string(json, "type");
            if (type.isEmpty()) {
                continue;
            }
            if (live.size() >= MAX_ELEMENTS) {
                omitted++;
                continue;
            }
            live.add(new Element(json, string(json, "id"), type));
        }
        if (live.isEmpty()) {
            return EMPTY;
        }

        Map<String, Element> byId = new HashMap<>();
        Map<String, String> labels = new HashMap<>();
        for (Element element : live) {
            if (!element.id().isEmpty()) {
                byId.put(element.id(), element);
            }
        }
        for (Element element : live) {
            String container = string(element.json(), "containerId");
            if ("text".equals(element.type()) && byId.containsKey(container)) {
                labels.merge(container, text(element), (first, second) -> first + " " + second);
            }
        }
        Map<String, String> frameNames = new HashMap<>();
        for (Element element : live) {
            if (isFrame(element.type())) {
                String name = string(element.json(), "name");
                frameNames.put(element.id(), name.isBlank() ? "Untitled frame" : name.strip());
            }
        }

        Map<String, Section> sections = new TreeMap<>(Comparator.comparing((String key) -> !key.isEmpty())
                .thenComparing(key -> frameNames.getOrDefault(key, "")).thenComparing(key -> key));
        for (Element element : live) {
            if (isFrame(element.type())) {
                continue;
            }
            String frameId = frameNames.containsKey(element.frameId()) ? element.frameId() : NO_FRAME;
            Section section = sections.computeIfAbsent(frameId, ignored -> new Section());
            switch (element.type()) {
                case "text" -> {
                    if (!byId.containsKey(string(element.json(), "containerId")) && !text(element).isBlank()) {
                        section.texts.add(quote(text(element)));
                    }
                }
                case "arrow", "line" -> section.connections.add(connection(element, byId, labels));
                case "rectangle", "ellipse", "diamond" -> section.components.add(
                        labels.containsKey(element.id())
                                ? element.type() + " " + quote(labels.get(element.id()))
                                : element.type() + " (unlabeled)");
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

    private static String connection(Element arrow, Map<String, Element> byId, Map<String, String> labels) {
        String source = endpoint(arrow.json(), "startBinding", byId, labels);
        String target = endpoint(arrow.json(), "endBinding", byId, labels);
        String label = labels.getOrDefault(arrow.id(), "");
        String line = source + " -> " + target + (label.isBlank() ? "" : ": " + label);
        boolean unconnected = source.equals(UNCONNECTED) || target.equals(UNCONNECTED);
        return (unconnected ? "[unconnected " + arrow.type() + "] " : "") + line;
    }

    private static String endpoint(JsonObject arrow, String member, Map<String, Element> byId,
                                   Map<String, String> labels) {
        if (!(arrow.get(member) instanceof JsonObject binding)) {
            return UNCONNECTED;
        }
        Element target = byId.get(string(binding, "elementId"));
        if (target == null) {
            return UNCONNECTED;
        }
        if ("text".equals(target.type())) {
            return quote(text(target));
        }
        String label = labels.get(target.id());
        return label == null ? target.type() + " (unlabeled)" : quote(label);
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
