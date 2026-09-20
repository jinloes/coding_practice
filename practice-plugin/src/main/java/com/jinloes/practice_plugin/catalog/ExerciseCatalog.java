package com.jinloes.practice_plugin.catalog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ExerciseCatalog {
    /** Which exercises exist, in the order the UI lists them. */
    private static final List<String> IDS = List.of(
            "pair-sum",
            "binary-search",
            "balanced-delimiters",
            "reverse-linked-list",
            "array-stack",
            "binary-min-heap");

    private static final Pattern TEST_ANNOTATION = Pattern.compile("@Test\\b");

    private static final List<Exercise> EXERCISES = loadFromManifests();

    private static final Map<String, Exercise> BY_ID = EXERCISES.stream()
            .collect(java.util.stream.Collectors.toUnmodifiableMap(Exercise::id, exercise -> exercise));

    private ExerciseCatalog() {
    }

    public record Exercise(
            String id,
            String title,
            String topic,
            String difficulty,
            String statement,
            List<Example> examples,
            List<String> hints,
            String inputSyntax,
            String sampleInput,
            String intendedTime,
            String intendedSpace,
            int exampleCount,
            int fullCount
    ) {
        public Exercise {
            id = Objects.requireNonNull(id, "id");
            title = Objects.requireNonNull(title, "title");
            topic = Objects.requireNonNull(topic, "topic");
            difficulty = Objects.requireNonNull(difficulty, "difficulty");
            statement = Objects.requireNonNull(statement, "statement");
            examples = List.copyOf(Objects.requireNonNull(examples, "examples"));
            hints = List.copyOf(Objects.requireNonNull(hints, "hints"));
            inputSyntax = Objects.requireNonNull(inputSyntax, "inputSyntax");
            sampleInput = Objects.requireNonNull(sampleInput, "sampleInput");
            intendedTime = Objects.requireNonNull(intendedTime, "intendedTime");
            intendedSpace = Objects.requireNonNull(intendedSpace, "intendedSpace");
            if (inputSyntax.isBlank() || sampleInput.isBlank()
                    || intendedTime.isBlank() || intendedSpace.isBlank()) {
                throw new IllegalArgumentException(
                        "Exercise input syntax, sample, and intended complexity must not be blank");
            }
            if (exampleCount < 0 || fullCount < exampleCount || examples.size() != exampleCount) {
                throw new IllegalArgumentException("Invalid exercise test counts");
            }
        }
    }

    public record Example(String input, String output) {
        public Example {
            input = Objects.requireNonNull(input, "input");
            output = Objects.requireNonNull(output, "output");
            if (input.isBlank() || output.isBlank()) {
                throw new IllegalArgumentException("Example input and output must not be blank");
            }
        }
    }

    public static List<Exercise> all() {
        return EXERCISES;
    }

    public static Exercise find(String id) {
        Exercise exercise = BY_ID.get(id);
        if (exercise == null) {
            throw new IllegalArgumentException("Unknown exercise ID: " + id);
        }
        return exercise;
    }

    public static String harness(String filename) {
        if (!"ComplexityProbe.java".equals(filename)) {
            throw new IllegalArgumentException("Unknown harness resource: " + filename);
        }
        return read("/harness/" + filename);
    }

    public static String resource(Exercise exercise, String filename) {
        Objects.requireNonNull(exercise, "exercise");
        if (!BY_ID.containsKey(exercise.id())) {
            throw new IllegalArgumentException("Unknown exercise ID: " + exercise.id());
        }
        if (filename == null || filename.isEmpty()
                || !filename.matches("[A-Za-z0-9][A-Za-z0-9._-]*")
                || filename.contains("..")) {
            throw new IllegalArgumentException("Unsafe resource filename: " + filename);
        }
        return read("/exercises/" + exercise.id() + "/" + filename);
    }

    private static String read(String path) {
        try (InputStream input = ExerciseCatalog.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalArgumentException("Unknown exercise resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read exercise resource: " + path, exception);
        }
    }

    /**
     * Loads every exercise from its manifest, in the order the UI shows them. The ID list is the
     * one place that says which exercises exist and in what order; a classpath directory cannot be
     * listed reliably from inside a packaged plugin.
     */
    private static List<Exercise> loadFromManifests() {
        return IDS.stream().map(ExerciseCatalog::loadExercise).toList();
    }

    private static Exercise loadExercise(String id) {
        JsonObject manifest;
        try {
            manifest = JsonParser.parseString(read("/exercises/" + id + "/exercise.json")).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException exception) {
            throw new IllegalStateException("Malformed exercise manifest for " + id, exception);
        }
        if (!id.equals(text(manifest, id, "id"))) {
            throw new IllegalStateException("Exercise manifest ID does not match its directory: " + id);
        }
        String inputSyntax = text(manifest, id, "inputSyntax");
        String runner = read("/exercises/" + id + "/ExampleRunner.java");
        if (!runner.contains(inputSyntax)) {
            throw new IllegalStateException(
                    "Exercise " + id + " documents input syntax its ExampleRunner usage text does not show");
        }
        int exampleCount = countTests(read("/exercises/" + id + "/ExamplesTest.java"), id + "/ExamplesTest.java");
        int fullCount = exampleCount
                + countTests(read("/exercises/" + id + "/CorrectnessTest.java"), id + "/CorrectnessTest.java");
        return new Exercise(
                id,
                text(manifest, id, "title"),
                text(manifest, id, "topic"),
                text(manifest, id, "difficulty"),
                text(manifest, id, "statement"),
                array(manifest, id, "examples").asList().stream()
                        .map(element -> {
                            JsonObject example = object(element, id, "examples");
                            return new Example(text(example, id, "input"), text(example, id, "output"));
                        })
                        .toList(),
                array(manifest, id, "hints").asList().stream()
                        .map(element -> string(element, id, "hints"))
                        .toList(),
                inputSyntax,
                text(manifest, id, "sampleInput"),
                text(manifest, id, "intendedTime"),
                text(manifest, id, "intendedSpace"),
                exampleCount,
                fullCount);
    }

    private static String text(JsonObject manifest, String id, String member) {
        if (!manifest.has(member)) {
            throw new IllegalStateException("Exercise manifest " + id + " is missing '" + member + "'");
        }
        return string(manifest.get(member), id, member);
    }

    private static String string(JsonElement element, String id, String member) {
        if (!(element instanceof JsonPrimitive primitive) || !primitive.isString()) {
            throw new IllegalStateException("Exercise manifest " + id + " member '" + member + "' is not a string");
        }
        return primitive.getAsString();
    }

    private static JsonArray array(JsonObject manifest, String id, String member) {
        if (!(manifest.get(member) instanceof JsonArray values)) {
            throw new IllegalStateException("Exercise manifest " + id + " member '" + member + "' is not an array");
        }
        return values;
    }

    private static JsonObject object(JsonElement element, String id, String member) {
        if (!(element instanceof JsonObject value)) {
            throw new IllegalStateException(
                    "Exercise manifest " + id + " member '" + member + "' must hold objects");
        }
        return value;
    }

    /**
     * Counts the test methods a template declares. Comments and literals are removed first, so a
     * commented-out test does not count and an annotation argument does not hide one. Anything the
     * stripper cannot classify fails the load rather than producing a count the UI would show to a
     * learner as the number of hidden tests.
     */
    private static int countTests(String source, String label) {
        int count = 0;
        var matcher = TEST_ANNOTATION.matcher(stripCommentsAndLiterals(source, label));
        while (matcher.find()) {
            count++;
        }
        if (count == 0) {
            throw new IllegalStateException("No test methods found in " + label);
        }
        return count;
    }

    private static String stripCommentsAndLiterals(String source, String label) {
        StringBuilder code = new StringBuilder(source.length());
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '/' && index + 1 < source.length() && source.charAt(index + 1) == '/') {
                int newline = source.indexOf('\n', index);
                index = newline < 0 ? source.length() : newline;
                continue;
            }
            if (current == '/' && index + 1 < source.length() && source.charAt(index + 1) == '*') {
                int end = source.indexOf("*/", index + 2);
                if (end < 0) {
                    throw new IllegalStateException("Unterminated block comment in " + label);
                }
                index = end + 2;
                continue;
            }
            if (current == '"' && source.startsWith("\"\"\"", index)) {
                throw new IllegalStateException("Text blocks are not supported in " + label);
            }
            if (current == '"' || current == '\'') {
                index = skipLiteral(source, index, label);
                continue;
            }
            code.append(current);
            index++;
        }
        return code.toString();
    }

    private static int skipLiteral(String source, int start, String label) {
        char quote = source.charAt(start);
        int index = start + 1;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '\\') {
                index += 2;
                continue;
            }
            if (current == '\n') {
                break;
            }
            if (current == quote) {
                return index + 1;
            }
            index++;
        }
        throw new IllegalStateException("Unterminated literal in " + label);
    }
}
