package com.jinloes.system_design_plugin.interview;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jinloes.system_design_plugin.interview.DiagramSummary.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves explicit {@code containerId} bindings first, then attaches remaining unbound {@code text} elements whose
 * center falls inside an unlabeled shape in the same frame.
 */
final class DiagramLabels {
    private static final String NO_FRAME = "";

    private static final Comparator<CandidateShape> SHAPE_PRIORITY = Comparator
            .comparingDouble(CandidateShape::area)
            .thenComparingDouble(CandidateShape::y)
            .thenComparingDouble(CandidateShape::x)
            .thenComparing(shape -> shape.element().id());

    private static final Comparator<EnclosedText> TEXT_ORDER = Comparator
            .comparingDouble(EnclosedText::y)
            .thenComparingDouble(EnclosedText::x)
            .thenComparing(EnclosedText::id);

    private DiagramLabels() {
    }

    record Result(
            Map<String, String> labels,
            Set<Element> consumedElements,
            Map<String, String> consumedTextToShapeId,
            Map<String, String> consumedTexts) {
    }

    private record CandidateShape(Element element, String frameId, double x, double y, double width, double height) {
        double area() {
            return width * height;
        }

        boolean contains(double cx, double cy) {
            return cx >= x && cx <= x + width && cy >= y && cy <= y + height;
        }
    }

    private record EnclosedText(Element element, String text, double x, double y) {
        String id() {
            return element.id();
        }
    }

    static Result resolve(List<Element> raw, Map<String, Element> nonFramesById, Map<String, String> frameNames) {
        Map<String, String> labels = new HashMap<>();
        Set<Element> consumedElements = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Element element : raw) {
            String container = DiagramSummary.string(element.json(), "containerId");
            if ("text".equals(element.type()) && nonFramesById.containsKey(container)) {
                consumedElements.add(element);
                String labelText = DiagramSummary.text(element);
                if (!labelText.isEmpty()) {
                    labels.merge(container, labelText, (first, second) -> first + " " + second);
                }
            }
        }

        List<CandidateShape> candidates = new ArrayList<>();
        for (Element element : raw) {
            if (!DiagramSummary.isShape(element.type())
                    || element.id().isEmpty()
                    || labels.containsKey(element.id())) {
                continue;
            }
            Double x = finiteNumber(element.json(), "x");
            Double y = finiteNumber(element.json(), "y");
            double width = nonNegativeDimension(element.json(), "width");
            double height = nonNegativeDimension(element.json(), "height");
            if (x != null && y != null && width > 0.0 && height > 0.0) {
                candidates.add(new CandidateShape(
                        element, normalizedFrame(element, frameNames), x, y, width, height));
            }
        }

        Map<String, List<EnclosedText>> enclosedByShape = new HashMap<>();
        Map<String, String> consumedTextToShapeId = new HashMap<>();
        Map<String, String> consumedTexts = new HashMap<>();
        for (Element element : raw) {
            if (!"text".equals(element.type()) || consumedElements.contains(element)) {
                continue;
            }
            String labelText = DiagramSummary.text(element);
            if (labelText.isEmpty()) {
                consumedElements.add(element);
                continue;
            }
            Double x = finiteNumber(element.json(), "x");
            Double y = finiteNumber(element.json(), "y");
            if (x == null || y == null) {
                continue;
            }
            double cx = x + nonNegativeDimension(element.json(), "width") / 2.0;
            double cy = y + nonNegativeDimension(element.json(), "height") / 2.0;
            CandidateShape best = bestEnclosing(candidates, normalizedFrame(element, frameNames), cx, cy);
            if (best != null) {
                consumedElements.add(element);
                enclosedByShape.computeIfAbsent(best.element().id(), ignored -> new ArrayList<>())
                        .add(new EnclosedText(element, labelText, x, y));
                if (!element.id().isEmpty()) {
                    consumedTextToShapeId.put(element.id(), best.element().id());
                    consumedTexts.put(element.id(), labelText);
                }
            }
        }

        for (Map.Entry<String, List<EnclosedText>> entry : enclosedByShape.entrySet()) {
            List<EnclosedText> group = entry.getValue();
            group.sort(TEXT_ORDER);
            StringBuilder joined = new StringBuilder();
            for (int index = 0; index < group.size(); index++) {
                if (index > 0) {
                    joined.append(' ');
                }
                joined.append(group.get(index).text());
            }
            labels.put(entry.getKey(), joined.toString());
        }

        return new Result(labels, consumedElements, consumedTextToShapeId, consumedTexts);
    }

    private static CandidateShape bestEnclosing(
            List<CandidateShape> candidates, String frameId, double cx, double cy) {
        CandidateShape best = null;
        for (CandidateShape candidate : candidates) {
            if (!candidate.frameId().equals(frameId) || !candidate.contains(cx, cy)) {
                continue;
            }
            if (best == null || SHAPE_PRIORITY.compare(candidate, best) < 0) {
                best = candidate;
            }
        }
        return best;
    }

    private static String normalizedFrame(Element element, Map<String, String> frameNames) {
        String frameId = element.frameId();
        return frameNames.containsKey(frameId) ? frameId : NO_FRAME;
    }

    private static Double finiteNumber(JsonObject object, String member) {
        if (object.get(member) instanceof JsonPrimitive primitive && primitive.isNumber()) {
            double value = primitive.getAsDouble();
            if (Double.isFinite(value)) {
                return value;
            }
        }
        return null;
    }

    private static double nonNegativeDimension(JsonObject object, String member) {
        Double value = finiteNumber(object, member);
        return value != null && value > 0.0 ? value : 0.0;
    }
}
