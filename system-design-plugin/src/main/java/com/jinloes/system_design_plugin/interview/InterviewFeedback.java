package com.jinloes.system_design_plugin.interview;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The interviewer's structured review. When the model's output cannot be validated, the result is
 * {@linkplain #parsed() unparsed} and keeps the raw text (bounded to {@value #MAX_RAW_BYTES} UTF-8 bytes).
 */
public record InterviewFeedback(boolean parsed, String verdict, String summary, List<Dimension> dimensions,
                                List<String> followUpQuestions, List<String> missedExpectations, String raw) {
    public static final int MAX_RAW_BYTES = 64 * 1024;
    public static final String ROUND_INITIAL = "initial";
    public static final String ROUND_FOLLOW_UP = "follow-up";
    private static final Pattern FENCED = Pattern.compile("```(?:json)?\\s*(\\{.*?})\\s*```", Pattern.DOTALL);
    private static final Gson GSON = new Gson();

    public record Dimension(String name, int score, List<String> strengths, List<String> gaps) {
        public Dimension {
            strengths = List.copyOf(strengths);
            gaps = List.copyOf(gaps);
        }
    }

    /** Feedback as saved to {@code feedback/<epochMillis>.json}. */
    public record Stored(InterviewFeedback feedback, String fingerprint, String model, String createdAt,
                         String level, String round, String followUpAnswers) {
        public Stored(InterviewFeedback feedback, String fingerprint, String model, String createdAt) {
            this(feedback, fingerprint, model, createdAt, "", ROUND_INITIAL, "");
        }

        public Stored {
            Objects.requireNonNull(feedback, "feedback");
            fingerprint = Objects.requireNonNullElse(fingerprint, "");
            model = Objects.requireNonNullElse(model, "");
            createdAt = Objects.requireNonNullElse(createdAt, "");
            level = level == null || level.isBlank() ? "" : DesignCatalog.normalizeLevel(level);
            round = ROUND_FOLLOW_UP.equals(round) ? ROUND_FOLLOW_UP : ROUND_INITIAL;
            followUpAnswers = boundRaw(followUpAnswers);
        }

        public String toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("fingerprint", fingerprint);
            json.addProperty("model", model);
            json.addProperty("createdAt", createdAt);
            json.addProperty("level", level);
            json.addProperty("round", round);
            if (!followUpAnswers.isEmpty()) {
                json.addProperty("followUpAnswers", followUpAnswers);
            }
            json.addProperty("parsed", feedback.parsed());
            if (feedback.parsed()) {
                json.add("feedback", feedback.toJsonObject());
            } else {
                json.addProperty("raw", feedback.raw());
            }
            return GSON.toJson(json);
        }

        /** @throws IllegalArgumentException when the file is corrupt or no longer valid */
        public static Stored fromJson(String text) {
            try {
                JsonObject json = JsonParser.parseString(text).getAsJsonObject();
                String fingerprint = requiredString(json, "fingerprint");
                String model = requiredString(json, "model");
                String createdAt = requiredString(json, "createdAt");
                String level = optionalString(json, "level");
                String round = optionalString(json, "round");
                String followUpAnswers = optionalString(json, "followUpAnswers");
                InterviewFeedback feedback;
                if (json.get("parsed") instanceof JsonPrimitive parsed && parsed.isBoolean() && parsed.getAsBoolean()) {
                    feedback = validate(json.getAsJsonObject("feedback"));
                } else {
                    feedback = unparsed(requiredString(json, "raw"));
                }
                return new Stored(feedback, fingerprint, model, createdAt, level, round, followUpAnswers);
            } catch (JsonParseException | IllegalStateException | ClassCastException | NullPointerException exception) {
                throw new IllegalArgumentException("Stored feedback is corrupt.", exception);
            }
        }
    }

    public InterviewFeedback {
        verdict = Objects.requireNonNullElse(verdict, "");
        summary = Objects.requireNonNullElse(summary, "");
        dimensions = List.copyOf(dimensions);
        followUpQuestions = List.copyOf(followUpQuestions);
        missedExpectations = List.copyOf(missedExpectations);
        raw = Objects.requireNonNullElse(raw, "");
    }

    public static InterviewFeedback unparsed(String raw) {
        return new InterviewFeedback(false, "", "", List.of(), List.of(), List.of(), boundRaw(raw));
    }

    /** Parses model output that is JSON, JSON in a fenced block, or JSON embedded in prose. */
    public static InterviewFeedback parse(String output) {
        String text = output == null ? "" : output.strip();
        for (String candidate : candidates(text)) {
            try {
                if (JsonParser.parseString(candidate) instanceof JsonObject object) {
                    return validate(object);
                }
            } catch (JsonParseException | IllegalStateException exception) {
                // Try the next candidate; an invalid result falls back to the raw text.
            }
        }
        return unparsed(text);
    }

    private static List<String> candidates(String text) {
        List<String> candidates = new ArrayList<>();
        candidates.add(text);
        Matcher fenced = FENCED.matcher(text);
        while (fenced.find()) {
            candidates.add(fenced.group(1));
        }
        int first = text.indexOf('{');
        int last = text.lastIndexOf('}');
        if (first >= 0 && last > first) {
            candidates.add(text.substring(first, last + 1));
        }
        return candidates;
    }

    static InterviewFeedback validate(JsonObject json) {
        String verdict = requiredString(json, "verdict");
        if (!DesignCatalog.VERDICTS.contains(verdict)) {
            throw new IllegalStateException("Unknown verdict: " + verdict);
        }
        String summary = requiredString(json, "summary");
        if (!(json.get("dimensions") instanceof JsonArray array)) {
            throw new IllegalStateException("Missing dimensions");
        }
        Map<String, Dimension> byName = new HashMap<>();
        for (JsonElement element : array) {
            if (!(element instanceof JsonObject object)) {
                throw new IllegalStateException("Dimension must be an object");
            }
            String name = requiredString(object, "name");
            if (!DesignCatalog.DIMENSIONS.contains(name) || byName.containsKey(name)) {
                throw new IllegalStateException("Unknown or repeated dimension: " + name);
            }
            if (!(object.get("score") instanceof JsonPrimitive score) || !score.isNumber()
                    || score.getAsDouble() != Math.floor(score.getAsDouble())
                    || score.getAsDouble() < 1 || score.getAsDouble() > 4) {
                throw new IllegalStateException("Score must be an integer from 1 to 4");
            }
            byName.put(name, new Dimension(name, score.getAsInt(), strings(object, "strengths"),
                    strings(object, "gaps")));
        }
        if (byName.size() != DesignCatalog.DIMENSIONS.size()) {
            throw new IllegalStateException("Every rubric dimension is required");
        }
        List<Dimension> ordered = DesignCatalog.DIMENSIONS.stream().map(byName::get).toList();
        return new InterviewFeedback(true, verdict, summary, ordered, strings(json, "followUpQuestions"),
                strings(json, "missedExpectations"), "");
    }

    JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("verdict", verdict);
        json.addProperty("summary", summary);
        JsonArray array = new JsonArray();
        for (Dimension dimension : dimensions) {
            JsonObject object = new JsonObject();
            object.addProperty("name", dimension.name());
            object.addProperty("score", dimension.score());
            object.add("strengths", array(dimension.strengths()));
            object.add("gaps", array(dimension.gaps()));
            array.add(object);
        }
        json.add("dimensions", array);
        json.add("followUpQuestions", array(followUpQuestions));
        json.add("missedExpectations", array(missedExpectations));
        return json;
    }

    private static JsonArray array(List<String> values) {
        JsonArray array = new JsonArray();
        values.forEach(array::add);
        return array;
    }

    private static String requiredString(JsonObject json, String member) {
        if (!(json.get(member) instanceof JsonPrimitive primitive) || !primitive.isString()) {
            throw new IllegalStateException("Missing string: " + member);
        }
        return primitive.getAsString();
    }

    private static String optionalString(JsonObject json, String member) {
        JsonElement element = json.get(member);
        if (element == null || element.isJsonNull()) {
            return "";
        }
        if (!(element instanceof JsonPrimitive primitive) || !primitive.isString()) {
            throw new IllegalStateException("Expected string: " + member);
        }
        return primitive.getAsString();
    }

    private static List<String> strings(JsonObject json, String member) {
        if (!(json.get(member) instanceof JsonArray array)) {
            throw new IllegalStateException("Missing array: " + member);
        }
        List<String> values = new ArrayList<>();
        for (JsonElement element : array) {
            if (!(element instanceof JsonPrimitive primitive) || !primitive.isString()) {
                throw new IllegalStateException(member + " must contain strings");
            }
            values.add(primitive.getAsString());
        }
        return values;
    }

    static String boundRaw(String raw) {
        String value = raw == null ? "" : raw;
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= MAX_RAW_BYTES) {
            return value;
        }
        int end = MAX_RAW_BYTES;
        while ((bytes[end] & 0xC0) == 0x80) {
            end--;
        }
        return new String(bytes, 0, end, StandardCharsets.UTF_8);
    }
}
