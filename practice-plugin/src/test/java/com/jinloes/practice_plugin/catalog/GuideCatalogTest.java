package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuideCatalogTest {
    private static final List<String> LESSON_SECTIONS =
            List.of("How it works", "When to reach for it", "Common patterns", "Pitfalls");

    @Test
    void listsTheGuidesInOrderWithTheirExercisesEasiestFirst() {
        Map<String, List<String>> expected = new java.util.LinkedHashMap<>();
        expected.put("hash-maps", List.of(
                "pair-sum", "valid-anagram", "first-unique-character", "majority-element", "duplicate-check",
                "unique-by-first-name", "remove-selected-characters", "alien-word-sort", "score-leaderboard",
                "ransom-letter-check", "nearest-repeated-word-distance", "palindrome-permutation-check",
                "word-count-report", "subarray-sum-count", "longest-consecutive-run", "group-anagrams",
                "top-k-frequent", "sudoku-board-check", "least-recently-used-cache", "rank-teams-by-votes",
                "best-three-score-average", "group-equal-keys-in-place", "chained-hash-table",
                "smallest-keyword-cover", "in-memory-file-system", "popularity-tracker", "all-one-key-counter"));
        expected.put("two-pointers", List.of(
                "valid-palindrome", "move-zeroes", "merge-sorted-lists", "add-strings", "linked-list-cycle",
                "sorted-array-dedup", "sorted-arrays-intersection", "substring-index", "longest-nondecreasing-run",
                "kth-node-from-end", "reverse-word-order", "zero-sum-triplets", "largest-water-container",
                "remove-nth-from-end", "sorted-pair-sum", "duplicate-in-range", "dutch-flag-partition",
                "next-lexicographic-permutation", "apply-index-permutation", "minimum-rescue-boats",
                "linked-list-cycle-entry", "linked-list-intersection-node", "merge-sorted-arrays-in-place",
                "one-edit-away", "replace-and-remove-chars", "trapped-rainwater"));
        expected.put("sliding-window", List.of(
                "max-stock-profit", "longest-unique-substring", "longest-repeat-after-replacement",
                "permutation-substring", "high-access-employees", "longest-bounded-difference-subarray",
                "longest-k-repeating-substring", "longest-k-distinct-substring", "smallest-keyword-cover",
                "sliding-window-maximum", "minimum-covering-window", "substring-word-concatenations"));
        expected.put("stacks-queues", List.of(
                "balanced-delimiters", "array-stack", "min-tracking-stack", "postfix-evaluation", "warmer-day-wait",
                "car-fleets", "max-tracking-queue", "rolling-hit-counter", "sort-stack-with-stack",
                "sunset-view-buildings", "sliding-window-maximum", "largest-histogram-rectangle",
                "max-stack-with-pop-max"));
        expected.put("linked-lists", List.of(
                "reverse-linked-list", "merge-sorted-lists", "linked-list-cycle", "odd-even-position-buckets",
                "delete-linked-list-node", "kth-node-from-end", "deduplicate-linked-list", "remove-nth-from-end",
                "reorder-list", "random-pointer-list-copy", "add-digit-lists", "duplicate-in-range",
                "least-recently-used-cache", "linked-list-cycle-entry", "linked-list-intersection-node",
                "reverse-linked-list-subrange", "merge-k-sorted-lists", "reverse-in-groups",
                "linked-list-intersection-with-cycles"));
        expected.put("binary-search", List.of(
                "binary-search", "sorted-insert-position", "guess-number-by-feedback", "rotated-array-minimum",
                "rotated-array-search", "sorted-matrix-search", "minimum-eating-speed",
                "timestamped-key-value-store", "salary-cap-threshold", "first-greater-search-tree-value",
                "sorted-target-range", "fixed-point-in-sorted-array", "first-greater-than-key",
                "row-column-sorted-matrix-search", "k-closest-sorted-values", "sorted-arrays-median"));
        expected.put("prefix-sums", List.of(
                "subarray-sum-count", "product-except-self", "max-subarray-sum", "weighted-random-choice",
                "root-prefix-sum-count", "two-target-subarrays-min-length"));
        expected.put("heaps", List.of(
                "kth-largest-stream", "heaviest-stones-smash", "binary-min-heap", "top-k-frequent",
                "minimum-meeting-rooms", "k-closest-points", "kth-largest-element", "task-cooldown-schedule",
                "social-feed", "signal-delay-time", "smallest-range-from-sorted-lists", "single-threaded-cpu-order",
                "best-three-score-average", "sort-increasing-decreasing-array", "sort-nearly-sorted-list",
                "weighted-server-task-assignment", "k-closest-sorted-values", "reorganize-adjacent-characters",
                "merge-k-sorted-lists", "running-median"));
        expected.put("graphs", List.of(
                "clone-graph", "ocean-drainage", "island-count", "valid-tree-graph", "connected-component-count",
                "largest-island-area", "captured-regions", "rot-spread-time", "nearest-gate-distance",
                "course-order", "redundant-edge", "min-cost-connect-points", "signal-delay-time",
                "cheapest-flights-k-stops", "employee-news-spread-time", "course-schedule", "alien-alphabet-order",
                "word-ladder-length", "itinerary-reconstruction", "rising-water-path", "grid-obstacle-shortest-path",
                "grouped-dependency-order"));
        expected.put("trees", List.of(
                "tree-max-depth", "same-tree", "mirror-tree", "subtree-match", "tree-diameter",
                "height-balanced-tree", "symmetric-binary-tree", "root-to-leaf-path-sum",
                "leaf-values-left-to-right", "binary-tree-traversal-orders", "level-order-values",
                "tree-from-traversals", "valid-search-tree", "kth-smallest-in-search-tree",
                "search-tree-common-ancestor", "right-side-view", "good-node-count",
                "sorted-array-balanced-search-tree", "first-greater-search-tree-value",
                "search-tree-k-largest-values", "binary-tree-boundary-values", "binary-tree-common-ancestor",
                "employee-news-spread-time", "root-prefix-sum-count", "binary-tree-inorder-successor",
                "binary-root-path-sum", "link-tree-level-neighbors", "max-path-sum", "tree-codec"));
        expected.put("tries", List.of(
                "prefix-tree", "wildcard-word-dictionary", "one-character-difference", "grid-multi-word-search"));
        expected.put("backtracking", List.of(
                "binary-wildcard-expansion", "combination-sum", "grid-word-search", "parentheses-generation",
                "all-subsets", "all-permutations", "subsets-with-duplicates", "unique-combination-sum",
                "palindrome-partitions", "phone-letter-combinations", "ip-address-splits", "matchstick-square",
                "unique-permutations", "fixed-size-combinations", "subset-sum-decision", "grid-multi-word-search",
                "queens-placement"));
        expected.put("dynamic-programming", List.of(
                "stair-climbing-ways", "min-cost-stairs", "pascal-triangle-rows", "non-adjacent-max-sum",
                "circular-non-adjacent-max-sum", "digit-decodings", "grid-paths", "fewest-coins",
                "longest-increasing-subsequence", "longest-common-subsequence", "word-segmentation",
                "max-subarray-sum", "max-subarray-product", "longest-palindromic-substring",
                "palindromic-substring-count", "reachable-end", "equal-subset-partition", "stock-profit-cooldown",
                "coin-change-ways", "target-sign-sum", "string-interleaving", "edit-distance", "max-grid-path-sum",
                "longest-zigzag-subsequence", "subset-sum-decision", "microwave-timer-min-cost",
                "max-painting-profit", "longest-increasing-grid-path", "distinct-subsequence-count",
                "balloon-burst-max", "dot-star-matching", "max-stock-profit-two-transactions",
                "multiplier-end-pick-score"));
        expected.put("intervals", List.of(
                "meeting-conflicts", "summarize-integer-ranges", "merge-intervals", "insert-interval",
                "remove-overlapping-intervals", "minimum-meeting-rooms", "minimum-interval-point-cover",
                "tweet-counts-by-frequency", "cinema-schedule-gaps", "interval-query-min"));
        expected.put("bit-manipulation", List.of(
                "count-set-bits", "bit-count-table", "reverse-bits", "missing-number", "unpaired-value",
                "sum-without-plus", "reverse-digits"));
        expected.put("matrices", List.of(
                "zero-matrix-lines", "spiral-order", "rotate-square-matrix", "grid-paths", "island-count",
                "ocean-drainage", "grid-word-search", "sudoku-board-check", "sorted-matrix-search",
                "largest-island-area", "captured-regions", "rot-spread-time", "nearest-gate-distance",
                "grid-pattern-search", "max-grid-path-sum", "row-column-sorted-matrix-search", "rising-water-path",
                "longest-increasing-grid-path", "grid-obstacle-shortest-path"));
        expected.put("greedy", List.of(
                "team-photo-feasibility", "minimize-total-wait-time", "min-jumps-to-end", "gas-station-circuit",
                "consecutive-hand-groups", "triplet-merge-target", "label-partitions", "wildcard-parentheses-check",
                "task-cooldown-schedule", "reachable-end", "max-subarray-sum", "salary-cap-threshold",
                "longest-zigzag-subsequence", "minimum-rescue-boats", "optimal-task-pair-assignment",
                "minimum-interval-point-cover", "reorganize-adjacent-characters", "cinema-schedule-gaps"));
        expected.put("math", List.of(
                "happy-number", "increment-digits", "add-strings", "base-digit-validator", "integer-palindrome",
                "pascal-triangle-rows", "string-integer-conversion", "fast-power", "multiply-strings",
                "axis-square-counter", "reverse-digits", "rotate-square-matrix", "offline-random-sample",
                "reservoir-sample", "weighted-random-choice", "enumerate-primes", "base-conversion",
                "rectangle-intersection"));

        assertThat(GuideCatalog.all()).extracting(Guide::id).containsExactlyElementsOf(expected.keySet());
        for (Guide guide : GuideCatalog.all()) {
            assertThat(guide.exercises()).extracting(Exercise::id)
                    .as("exercises of %s", guide.id())
                    .containsExactlyElementsOf(expected.get(guide.id()));
            assertThat(guide.exercises()).extracting(exercise -> GuideCatalog.DIFFICULTIES.indexOf(exercise.difficulty()))
                    .as("%s lists exercises Easy, then Medium, then Hard", guide.id())
                    .isSorted();
            assertThat(GuideCatalog.find(guide.id())).isSameAs(guide);
        }
        assertThat(GuideCatalog.all()).extracting(Guide::title).doesNotHaveDuplicates();
    }

    @Test
    void everyGuideHasASummaryAndAllLessonSections() {
        for (Guide guide : GuideCatalog.all()) {
            assertThat(guide.summary()).as("summary of %s", guide.id()).isNotBlank();
            for (String section : LESSON_SECTIONS) {
                assertThat(guide.lesson().lines().map(String::strip))
                        .as("%s lesson section label", guide.id())
                        .contains(section);
            }
        }
    }

    @Test
    void findsTheGuidesThatIncludeAnExercise() {
        assertThat(GuideCatalog.containing("merge-sorted-lists")).extracting(Guide::id)
                .containsExactly("two-pointers", "linked-lists");
        assertThat(GuideCatalog.containing("roman-to-integer")).isEmpty();
        assertThatThrownBy(() -> GuideCatalog.find("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void ordersByDifficultyAndKeepsManifestOrderWithinALevel() {
        Map<String, Exercise> fakes = Map.of(
                "a", fake("a", "Hard"), "b", fake("b", "Easy"), "c", fake("c", "Medium"), "d", fake("d", "Easy"));
        Guide guide = GuideCatalog.parse("demo", manifest("demo", "\"a\", \"b\", \"c\", \"d\""), lookup(fakes));

        assertThat(guide.exercises()).extracting(Exercise::id).containsExactly("b", "d", "c", "a");
        assertThat(guide.exercisesAt("Easy")).extracting(Exercise::id).containsExactly("b", "d");
    }

    @Test
    void rejectsInvalidManifestsNamingTheGuide() {
        Map<String, Exercise> fakes = Map.of("a", fake("a", "Easy"), "x", fake("x", "Expert"));
        Function<String, Exercise> lookup = lookup(fakes);

        assertThat(GuideCatalog.parse("demo", manifest("demo", "\"a\""), lookup).exercises())
                .as("valid control")
                .extracting(Exercise::id).containsExactly("a");
        assertInvalid(manifest("other", "\"a\""), lookup);
        assertInvalid("{\"id\": \"demo\", \"summary\": \"s\", \"lesson\": \"l\", \"exercises\": [\"a\"]}", lookup);
        assertInvalid("{\"id\": \"demo\", \"title\": \"t\", \"summary\": \" \", \"lesson\": \"l\", "
                + "\"exercises\": [\"a\"]}", lookup);
        assertInvalid("{\"id\": \"demo\", \"title\": \"t\", \"summary\": \"s\", \"exercises\": [\"a\"]}", lookup);
        assertInvalid(manifest("demo", ""), lookup);
        assertInvalid(manifest("demo", "1"), lookup);
        assertInvalid(manifest("demo", "\"unknown\""), lookup);
        assertInvalid(manifest("demo", "\"a\", \"a\""), lookup);
        assertInvalid(manifest("demo", "\"x\""), lookup);
        assertInvalid("not json", lookup);
    }

    private static void assertInvalid(String json, Function<String, Exercise> lookup) {
        assertThatThrownBy(() -> GuideCatalog.parse("demo", json, lookup))
                .as("manifest %s", json)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("demo");
    }

    private static String manifest(String id, String exercises) {
        return "{\"id\": \"" + id + "\", \"title\": \"Demo\", \"summary\": \"A demo.\", "
                + "\"lesson\": \"How it works\", \"exercises\": [" + exercises + "]}";
    }

    private static Function<String, Exercise> lookup(Map<String, Exercise> exercises) {
        return id -> {
            Exercise exercise = exercises.get(id);
            if (exercise == null) {
                throw new IllegalArgumentException("Unknown exercise ID: " + id);
            }
            return exercise;
        };
    }

    private static Exercise fake(String id, String difficulty) {
        return new Exercise(id, id.toUpperCase(), "Topic", difficulty, "Statement", List.of(), List.of(),
                "Input: x", "x", "O(1)", "O(1)", 0, 0);
    }
}
