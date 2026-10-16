package com.jinloes.practice_plugin.catalog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Topic study guides: a short lesson on one data structure or technique plus the bundled exercises
 * that practice it. A guide lists exercise IDs; the levels come from each exercise's own difficulty,
 * so the two manifests cannot disagree about how hard a problem is.
 */
public final class GuideCatalog {
    /** Which guides exist, in the order the UI lists them. */
    private static final List<String> IDS = List.of(
            "hash-maps",
            "two-pointers",
            "sliding-window",
            "stacks-queues",
            "linked-lists",
            "binary-search",
            "prefix-sums",
            "heaps",
            "graphs",
            "trees",
            "tries",
            "backtracking",
            "dynamic-programming",
            "intervals",
            "bit-manipulation",
            "matrices",
            "greedy",
            "math");

    /** The levels a guide groups its exercises into, easiest first. */
    public static final List<String> DIFFICULTIES = List.of("Easy", "Medium", "Hard");

    private static final List<Guide> GUIDES = IDS.stream()
            .map(id -> parse(id, read("/guides/" + id + ".json"), ExerciseCatalog::find))
            .toList();

    private static final Map<String, Guide> BY_ID = GUIDES.stream()
            .collect(Collectors.toUnmodifiableMap(Guide::id, guide -> guide));

    private GuideCatalog() {
    }

    /** A guide whose exercises are already ordered Easy, then Medium, then Hard. */
    public record Guide(String id, String title, String summary, String lesson, List<Exercise> exercises) {
        public Guide {
            id = Objects.requireNonNull(id, "id");
            title = Objects.requireNonNull(title, "title");
            summary = Objects.requireNonNull(summary, "summary");
            lesson = Objects.requireNonNull(lesson, "lesson");
            exercises = List.copyOf(Objects.requireNonNull(exercises, "exercises"));
        }

        public List<Exercise> exercisesAt(String difficulty) {
            return exercises.stream().filter(exercise -> exercise.difficulty().equals(difficulty)).toList();
        }
    }

    public static List<Guide> all() {
        return GUIDES;
    }

    public static Guide find(String id) {
        Guide guide = BY_ID.get(id);
        if (guide == null) {
            throw new IllegalArgumentException("Unknown guide ID: " + id);
        }
        return guide;
    }

    /** The guides that include {@code exerciseId}, in guide order. */
    public static List<Guide> containing(String exerciseId) {
        return GUIDES.stream()
                .filter(guide -> guide.exercises().stream().anyMatch(exercise -> exercise.id().equals(exerciseId)))
                .toList();
    }

    /**
     * Parses and validates one guide manifest. Exercises are resolved through {@code lookup} so the
     * validation rules can be exercised with exercises the shipped catalog does not contain.
     */
    static Guide parse(String id, String json, Function<String, Exercise> lookup) {
        JsonObject manifest;
        try {
            manifest = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException exception) {
            throw new IllegalStateException("Malformed guide manifest for " + id, exception);
        }
        if (!id.equals(text(manifest, id, "id"))) {
            throw new IllegalStateException("Guide manifest ID does not match its file name: " + id);
        }
        String title = text(manifest, id, "title");
        String summary = text(manifest, id, "summary");
        String lesson = text(manifest, id, "lesson");
        if (!(manifest.get("exercises") instanceof JsonArray ids) || ids.isEmpty()) {
            throw new IllegalStateException("Guide " + id + " must list at least one exercise");
        }
        List<Exercise> exercises = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (JsonElement element : ids) {
            if (!(element instanceof JsonPrimitive primitive) || !primitive.isString()) {
                throw new IllegalStateException("Guide " + id + " exercises must be exercise ID strings");
            }
            String exerciseId = primitive.getAsString();
            if (!seen.add(exerciseId)) {
                throw new IllegalStateException("Guide " + id + " lists exercise " + exerciseId + " twice");
            }
            Exercise exercise;
            try {
                exercise = lookup.apply(exerciseId);
            } catch (IllegalArgumentException exception) {
                throw new IllegalStateException(
                        "Guide " + id + " lists unknown exercise " + exerciseId, exception);
            }
            if (!DIFFICULTIES.contains(exercise.difficulty())) {
                throw new IllegalStateException("Guide " + id + " exercise " + exerciseId
                        + " has unsupported difficulty " + exercise.difficulty());
            }
            exercises.add(exercise);
        }
        exercises.sort(Comparator.comparingInt(exercise -> DIFFICULTIES.indexOf(exercise.difficulty())));
        return new Guide(id, title, summary, lesson, exercises);
    }

    private static String text(JsonObject manifest, String id, String member) {
        if (!(manifest.get(member) instanceof JsonPrimitive primitive) || !primitive.isString()
                || primitive.getAsString().isBlank()) {
            throw new IllegalStateException("Guide manifest " + id + " needs a non-blank '" + member + "'");
        }
        return primitive.getAsString();
    }

    private static String read(String path) {
        try (InputStream input = GuideCatalog.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Missing guide resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read guide resource: " + path, exception);
        }
    }
}
