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
            "binary-min-heap",
            "valid-anagram",
            "first-unique-character",
            "max-stock-profit",
            "move-zeroes",
            "merge-sorted-lists",
            "valid-palindrome",
            "add-strings",
            "roman-to-integer",
            "longest-common-prefix",
            "missing-number",
            "majority-element",
            "subarray-sum-count",
            "course-schedule",
            "sliding-window-maximum",
            "duplicate-check",
            "product-except-self",
            "max-subarray-sum",
            "max-subarray-product",
            "rotated-array-minimum",
            "rotated-array-search",
            "zero-sum-triplets",
            "largest-water-container",
            "longest-consecutive-run",
            "sum-without-plus",
            "count-set-bits",
            "bit-count-table",
            "reverse-bits",
            "insert-interval",
            "merge-intervals",
            "remove-overlapping-intervals",
            "meeting-conflicts",
            "minimum-meeting-rooms",
            "stair-climbing-ways",
            "fewest-coins",
            "longest-increasing-subsequence",
            "longest-common-subsequence",
            "word-segmentation",
            "combination-sum",
            "non-adjacent-max-sum",
            "circular-non-adjacent-max-sum",
            "digit-decodings",
            "grid-paths",
            "reachable-end",
            "clone-graph",
            "ocean-drainage",
            "island-count",
            "alien-alphabet-order",
            "valid-tree-graph",
            "connected-component-count",
            "linked-list-cycle",
            "merge-k-sorted-lists",
            "remove-nth-from-end",
            "reorder-list",
            "top-k-frequent",
            "running-median",
            "zero-matrix-lines",
            "spiral-order",
            "rotate-square-matrix",
            "grid-word-search",
            "longest-unique-substring",
            "longest-repeat-after-replacement",
            "minimum-covering-window",
            "group-anagrams",
            "longest-palindromic-substring",
            "palindromic-substring-count",
            "string-list-codec",
            "tree-max-depth",
            "same-tree",
            "mirror-tree",
            "max-path-sum",
            "level-order-values",
            "tree-codec",
            "subtree-match",
            "tree-from-traversals",
            "valid-search-tree",
            "kth-smallest-in-search-tree",
            "search-tree-common-ancestor",
            "prefix-tree",
            "wildcard-word-dictionary",
            "grid-multi-word-search",
            "sudoku-board-check",
            "sorted-pair-sum",
            "trapped-rainwater",
            "permutation-substring",
            "min-tracking-stack",
            "postfix-evaluation",
            "parentheses-generation",
            "warmer-day-wait",
            "car-fleets",
            "largest-histogram-rectangle",
            "sorted-matrix-search",
            "minimum-eating-speed",
            "timestamped-key-value-store",
            "sorted-arrays-median",
            "random-pointer-list-copy",
            "add-digit-lists",
            "duplicate-in-range",
            "least-recently-used-cache",
            "reverse-in-groups",
            "tree-diameter",
            "height-balanced-tree",
            "reverse-digits",
            "right-side-view",
            "good-node-count",
            "kth-largest-stream",
            "heaviest-stones-smash",
            "k-closest-points",
            "kth-largest-element",
            "task-cooldown-schedule",
            "unpaired-value",
            "social-feed",
            "all-subsets",
            "all-permutations",
            "subsets-with-duplicates",
            "unique-combination-sum",
            "palindrome-partitions",
            "phone-letter-combinations",
            "queens-placement",
            "largest-island-area",
            "captured-regions",
            "rot-spread-time",
            "nearest-gate-distance",
            "course-order",
            "redundant-edge",
            "word-ladder-length",
            "itinerary-reconstruction",
            "min-cost-connect-points",
            "signal-delay-time",
            "rising-water-path",
            "cheapest-flights-k-stops",
            "min-cost-stairs",
            "equal-subset-partition",
            "stock-profit-cooldown",
            "coin-change-ways",
            "target-sign-sum",
            "string-interleaving",
            "longest-increasing-grid-path",
            "distinct-subsequence-count",
            "edit-distance",
            "balloon-burst-max",
            "dot-star-matching",
            "min-jumps-to-end",
            "gas-station-circuit",
            "consecutive-hand-groups",
            "triplet-merge-target",
            "label-partitions",
            "wildcard-parentheses-check",
            "interval-query-min",
            "happy-number",
            "increment-digits",
            "fast-power",
            "multiply-strings",
            "axis-square-counter",
            "offline-random-sample",
            "reservoir-sample",
            "weighted-random-choice",
            "sorted-array-dedup",
            "enumerate-primes",
            "dutch-flag-partition",
            "next-lexicographic-permutation",
            "odd-even-position-buckets",
            "apply-index-permutation",
            "unique-by-first-name",
            "salary-cap-threshold",
            "sorted-arrays-intersection",
            "team-photo-feasibility",
            "high-access-employees",
            "rank-teams-by-votes",
            "sorted-array-balanced-search-tree",
            "smallest-range-from-sorted-lists",
            "first-greater-search-tree-value",
            "substring-index",
            "ip-address-splits",
            "remove-selected-characters",
            "one-character-difference",
            "summarize-integer-ranges",
            "longest-nondecreasing-run",
            "longest-bounded-difference-subarray",
            "grid-pattern-search",
            "max-grid-path-sum",
            "two-target-subarrays-min-length",
            "longest-zigzag-subsequence",
            "max-stock-profit-two-transactions",
            "minimum-rescue-boats",
            "single-threaded-cpu-order",
            "linked-list-cycle-entry",
            "delete-linked-list-node",
            "linked-list-intersection-node",
            "linked-list-intersection-with-cycles",
            "reverse-linked-list-subrange",
            "kth-node-from-end",
            "deduplicate-linked-list",
            "max-tracking-queue",
            "best-three-score-average",
            "matchstick-square",
            "unique-permutations",
            "fixed-size-combinations",
            "subset-sum-decision",
            "binary-wildcard-expansion",
            "base-conversion",
            "longest-k-repeating-substring",
            "longest-k-distinct-substring",
            "base-digit-validator",
            "integer-palindrome",
            "rectangle-intersection",
            "pascal-triangle-rows",
            "multiplier-end-pick-score",
            "microwave-timer-min-cost",
            "optimal-task-pair-assignment",
            "minimum-interval-point-cover",
            "minimize-total-wait-time",
            "rolling-hit-counter",
            "search-tree-k-largest-values",
            "tweet-counts-by-frequency",
            "binary-tree-boundary-values",
            "symmetric-binary-tree",
            "root-to-leaf-path-sum",
            "leaf-values-left-to-right",
            "binary-tree-common-ancestor",
            "employee-news-spread-time",
            "max-painting-profit",
            "sorted-target-range",
            "fixed-point-in-sorted-array",
            "first-greater-than-key",
            "row-column-sorted-matrix-search",
            "sorted-insert-position",
            "guess-number-by-feedback",
            "merge-sorted-arrays-in-place",
            "group-equal-keys-in-place",
            "root-prefix-sum-count",
            "binary-tree-inorder-successor",
            "binary-root-path-sum",
            "binary-tree-traversal-orders",
            "sort-increasing-decreasing-array",
            "sort-nearly-sorted-list",
            "weighted-server-task-assignment",
            "k-closest-sorted-values",
            "reorganize-adjacent-characters",
            "alien-word-sort",
            "all-one-key-counter",
            "chained-hash-table",
            "score-leaderboard",
            "substring-word-concatenations",
            "ransom-letter-check",
            "nearest-repeated-word-distance",
            "smallest-keyword-cover",
            "palindrome-permutation-check",
            "one-edit-away",
            "replace-and-remove-chars",
            "reverse-word-order",
            "string-integer-conversion",
            "word-count-report",
            "max-stack-with-pop-max",
            "sort-stack-with-stack",
            "sunset-view-buildings",
            "in-memory-file-system",
            "popularity-tracker",
            "cinema-schedule-gaps",
            "link-tree-level-neighbors",
            "grid-obstacle-shortest-path",
            "grouped-dependency-order");

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
