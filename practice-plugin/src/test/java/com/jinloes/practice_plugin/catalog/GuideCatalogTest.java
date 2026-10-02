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
                "subarray-sum-count", "longest-consecutive-run", "group-anagrams", "top-k-frequent"));
        expected.put("two-pointers", List.of(
                "valid-palindrome", "move-zeroes", "merge-sorted-lists", "add-strings", "linked-list-cycle",
                "zero-sum-triplets", "largest-water-container", "remove-nth-from-end"));
        expected.put("sliding-window", List.of(
                "max-stock-profit", "longest-unique-substring", "longest-repeat-after-replacement",
                "sliding-window-maximum", "minimum-covering-window"));
        expected.put("stacks-queues", List.of(
                "balanced-delimiters", "array-stack", "sliding-window-maximum"));
        expected.put("linked-lists", List.of(
                "reverse-linked-list", "merge-sorted-lists", "linked-list-cycle", "remove-nth-from-end",
                "reorder-list", "merge-k-sorted-lists"));
        expected.put("binary-search", List.of(
                "binary-search", "rotated-array-minimum", "rotated-array-search"));
        expected.put("prefix-sums", List.of(
                "subarray-sum-count", "product-except-self", "max-subarray-sum"));
        expected.put("heaps", List.of(
                "binary-min-heap", "top-k-frequent", "minimum-meeting-rooms", "merge-k-sorted-lists",
                "running-median"));
        expected.put("graphs", List.of(
                "clone-graph", "ocean-drainage", "island-count", "valid-tree-graph",
                "connected-component-count", "course-schedule", "alien-alphabet-order"));
        expected.put("trees", List.of(
                "tree-max-depth", "same-tree", "mirror-tree", "subtree-match", "level-order-values",
                "tree-from-traversals", "valid-search-tree", "kth-smallest-in-search-tree",
                "search-tree-common-ancestor", "max-path-sum", "tree-codec"));
        expected.put("tries", List.of("prefix-tree", "wildcard-word-dictionary", "grid-multi-word-search"));
        expected.put("backtracking", List.of("combination-sum", "grid-word-search", "grid-multi-word-search"));
        expected.put("dynamic-programming", List.of(
                "stair-climbing-ways", "non-adjacent-max-sum", "circular-non-adjacent-max-sum", "digit-decodings",
                "grid-paths", "fewest-coins", "longest-increasing-subsequence", "longest-common-subsequence",
                "word-segmentation", "max-subarray-sum", "max-subarray-product", "longest-palindromic-substring",
                "palindromic-substring-count", "reachable-end"));
        expected.put("intervals", List.of(
                "meeting-conflicts", "merge-intervals", "insert-interval", "remove-overlapping-intervals",
                "minimum-meeting-rooms"));
        expected.put("bit-manipulation", List.of(
                "count-set-bits", "bit-count-table", "reverse-bits", "missing-number", "sum-without-plus"));
        expected.put("matrices", List.of(
                "zero-matrix-lines", "spiral-order", "rotate-square-matrix", "grid-paths", "island-count",
                "ocean-drainage", "grid-word-search"));

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
