package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuideCatalogTest {
    private static final List<String> LESSON_SECTIONS =
            List.of("How it works", "When to reach for it", "Common patterns", "Pitfalls");

    @Test
    void listsTheGuidesInOrderWithEveryManifestExerciseEasiestFirst() throws IOException {
        assertThat(GuideCatalog.all()).extracting(Guide::id).containsExactly(
                "hash-maps", "two-pointers", "sliding-window", "stacks-queues", "linked-lists", "binary-search",
                "prefix-sums", "heaps", "graphs", "trees", "tries", "backtracking", "dynamic-programming",
                "intervals", "bit-manipulation", "matrices", "greedy", "math");
        for (Guide guide : GuideCatalog.all()) {
            assertThat(guide.exercises()).extracting(Exercise::id)
                    .as("exercises of %s match its manifest", guide.id())
                    .containsExactlyInAnyOrderElementsOf(manifestExerciseIds(guide.id()));
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

    private static List<String> manifestExerciseIds(String guideId) throws IOException {
        try (var input = GuideCatalogTest.class.getResourceAsStream("/guides/" + guideId + ".json")) {
            assertThat(input).as("manifest of %s", guideId).isNotNull();
            var manifest = JsonParser.parseString(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            return manifest.getAsJsonObject().getAsJsonArray("exercises").asList().stream()
                    .map(element -> element.getAsString())
                    .toList();
        }
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
