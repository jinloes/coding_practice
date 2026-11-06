package com.jinloes.system_design_plugin.catalog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The bundled system design problems and the fixed interviewer rubric. Each problem manifest lives at
 * {@code /problems/<id>.json}; its {@code expectations} are interviewer-only and never shown to the learner.
 */
public final class DesignCatalog {
    /** Which problems exist, in the order the UI lists them. */
    private static final List<String> IDS = List.of(
            "url-shortener",
            "rate-limiter",
            "news-feed",
            "chat-messaging",
            "notification-service",
            "web-crawler",
            "distributed-cache",
            "video-streaming");

    public static final List<String> DIFFICULTIES = List.of("Easy", "Medium", "Hard");

    /** The rubric dimensions, in the order feedback reports them. */
    public static final List<String> DIMENSIONS = List.of(
            "Requirements and scope",
            "Capacity estimates",
            "API and data model",
            "High-level architecture",
            "Deep dives and scalability",
            "Reliability and tradeoffs",
            "Communication");

    /** Allowed verdicts, strongest first. */
    public static final List<String> VERDICTS =
            List.of("Strong hire", "Hire", "Lean hire", "Lean no hire", "No hire");

    public static final int HINT_COUNT = 3;
    public static final int MIN_EXPECTATIONS = 5;

    private static final List<Problem> PROBLEMS = load(DesignCatalog.class::getResourceAsStream);

    private static final Map<String, Problem> BY_ID = PROBLEMS.stream()
            .collect(Collectors.toUnmodifiableMap(Problem::id, problem -> problem));

    private DesignCatalog() {
    }

    public record Problem(String id, String title, String difficulty, String summary, String statement,
                          List<String> hints, List<String> expectations) {
        public Problem {
            id = Objects.requireNonNull(id, "id");
            title = Objects.requireNonNull(title, "title");
            difficulty = Objects.requireNonNull(difficulty, "difficulty");
            summary = Objects.requireNonNull(summary, "summary");
            statement = Objects.requireNonNull(statement, "statement");
            hints = List.copyOf(hints);
            expectations = List.copyOf(expectations);
        }
    }

    public static List<Problem> all() {
        return PROBLEMS;
    }

    public static Problem find(String id) {
        Problem problem = BY_ID.get(id);
        if (problem == null) {
            throw new IllegalArgumentException("Unknown problem ID: " + id);
        }
        return problem;
    }

    public static boolean exists(String id) {
        return BY_ID.containsKey(id);
    }

    /** The starting content of a new attempt's {@code notes.md}. */
    public static String notesTemplate() {
        return read("/notes-template.md", DesignCatalog.class::getResourceAsStream);
    }

    static List<Problem> load(Function<String, InputStream> opener) {
        return IDS.stream().map(id -> parse(id, read("/problems/" + id + ".json", opener))).toList();
    }

    /** Parses and validates one problem manifest; every failure names the problem. */
    static Problem parse(String id, String json) {
        JsonObject manifest;
        try {
            manifest = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException exception) {
            throw new IllegalStateException("Malformed problem manifest for " + id, exception);
        }
        if (!id.equals(text(manifest, id, "id"))) {
            throw new IllegalStateException("Problem manifest ID does not match its file name: " + id);
        }
        String difficulty = text(manifest, id, "difficulty");
        if (!DIFFICULTIES.contains(difficulty)) {
            throw new IllegalStateException("Problem " + id + " has unsupported difficulty " + difficulty);
        }
        List<String> hints = texts(manifest, id, "hints");
        if (hints.size() != HINT_COUNT) {
            throw new IllegalStateException("Problem " + id + " must have exactly " + HINT_COUNT + " hints");
        }
        List<String> expectations = texts(manifest, id, "expectations");
        if (expectations.size() < MIN_EXPECTATIONS) {
            throw new IllegalStateException(
                    "Problem " + id + " must have at least " + MIN_EXPECTATIONS + " expectations");
        }
        return new Problem(id, text(manifest, id, "title"), difficulty, text(manifest, id, "summary"),
                text(manifest, id, "statement"), hints, expectations);
    }

    private static String text(JsonObject manifest, String id, String member) {
        if (!(manifest.get(member) instanceof JsonPrimitive primitive) || !primitive.isString()
                || primitive.getAsString().isBlank()) {
            throw new IllegalStateException("Problem manifest " + id + " needs a non-blank '" + member + "'");
        }
        return primitive.getAsString();
    }

    private static List<String> texts(JsonObject manifest, String id, String member) {
        if (!(manifest.get(member) instanceof JsonArray array)) {
            throw new IllegalStateException("Problem manifest " + id + " needs a '" + member + "' array");
        }
        List<String> values = new ArrayList<>();
        for (JsonElement element : array) {
            if (!(element instanceof JsonPrimitive primitive) || !primitive.isString()
                    || primitive.getAsString().isBlank()) {
                throw new IllegalStateException("Problem " + id + " '" + member + "' must be non-blank strings");
            }
            values.add(primitive.getAsString());
        }
        return values;
    }

    static String read(String path, Function<String, InputStream> opener) {
        try (InputStream input = opener.apply(path)) {
            if (input == null) {
                throw new IllegalStateException("Missing design resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read design resource: " + path, exception);
        }
    }
}
