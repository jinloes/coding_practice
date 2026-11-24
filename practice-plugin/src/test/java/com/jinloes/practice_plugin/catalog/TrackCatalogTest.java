package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrackCatalogTest {
    private static final List<String> NOTE_SECTIONS = List.of("About this list", "What to expect", "How to prepare");
    private static final Set<String> STUDY_LIST_IDS = Set.of("blind-75", "neetcode-150");

    @Test
    void indexListsEveryTrackManifestOnceInDisplayOrder() throws IOException {
        List<String> indexed = TrackCatalog.indexedIds();
        assertThat(indexed).isNotEmpty().doesNotHaveDuplicates();
        assertThat(TrackCatalog.all()).extracting(Guide::id)
                .containsExactlyElementsOf(indexed);
        assertThat(TrackCatalog.all()).extracting(Guide::title)
                .doesNotHaveDuplicates()
                .allSatisfy(title -> assertThat(title).isNotBlank());
        try (var files = Files.list(Path.of("src/main/resources/tracks"))) {
            List<String> manifestIds = files
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString().replace(".json", ""))
                    .toList();
            assertThat(manifestIds)
                    .as("tracks/index.txt must match all tracks/*.json files")
                    .containsExactlyInAnyOrderElementsOf(indexed);
        }
        for (Guide track : TrackCatalog.all()) {
            assertThat(track.exercises()).extracting(exercise -> GuideCatalog.DIFFICULTIES.indexOf(exercise.difficulty()))
                    .as("%s lists Easy, then Medium, then Hard", track.id())
                    .isSorted();
        }
    }

    @Test
    void blindSeventyFiveListsSeventyFiveDistinctExercises() {
        Guide blind = TrackCatalog.find("blind-75");
        assertThat(blind.exercises()).hasSize(75).doesNotHaveDuplicates();
        assertThat(blind.exercises()).extracting(Exercise::id)
                .contains("pair-sum", "course-schedule", "merge-k-sorted-lists", "running-median",
                        "grid-multi-word-search");
        assertThat(blind.summary()).contains("Original practice versions");
    }

    @Test
    void neetCodeOneFiftyExtendsTheBlindSeventyFive() {
        Guide neetCode = TrackCatalog.find("neetcode-150");
        assertThat(neetCode.exercises()).hasSize(150).doesNotHaveDuplicates()
                .containsAll(TrackCatalog.find("blind-75").exercises());
        assertThat(neetCode.exercises()).extracting(Exercise::id)
                .contains("binary-search", "sliding-window-maximum", "trapped-rainwater", "queens-placement",
                        "edit-distance", "axis-square-counter");
        assertThat(neetCode.summary()).contains("Original practice versions");
    }

    @Test
    void everyTrackDisclaimsOfficialStatusAndHasAllNoteSections() {
        for (Guide track : TrackCatalog.all()) {
            assertThat(track.summary()).as("summary of %s", track.id())
                    .contains("not an official or verified question list");
            assertThat(track.exercises()).as("%s has distinct exercises", track.id()).doesNotHaveDuplicates();
            for (String section : NOTE_SECTIONS) {
                assertThat(track.lesson().lines().map(String::strip))
                        .as("%s note section label", track.id())
                        .contains(section);
            }
            if (!STUDY_LIST_IDS.contains(track.id())) {
                assertThat(track.summary()).as("company summary %s", track.id()).contains("Approximate");
                for (String difficulty : GuideCatalog.DIFFICULTIES) {
                    assertThat(track.exercisesAt(difficulty))
                            .as("%s %s exercises", track.id(), difficulty)
                            .isNotEmpty();
                }
            }
        }
    }

    @Test
    void tracksAreSeparateFromTopicGuides() {
        assertThat(TrackCatalog.all()).extracting(Guide::id)
                .doesNotContainAnyElementsOf(GuideCatalog.all().stream().map(Guide::id).toList());
        assertThat(TrackCatalog.isTrack("google")).isTrue();
        assertThat(TrackCatalog.isTrack("graphs")).isFalse();
        assertThatThrownBy(() -> GuideCatalog.find("google")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findsTheTracksThatIncludeAnExercise() {
        for (Exercise exercise : ExerciseCatalog.all()) {
            List<Guide> expected = TrackCatalog.all().stream()
                    .filter(track -> track.exercises().contains(exercise))
                    .toList();
            assertThat(TrackCatalog.containing(exercise.id()))
                    .as("tracks containing %s", exercise.id())
                    .containsExactlyElementsOf(expected);
        }
        assertThat(TrackCatalog.containing("roman-to-integer")).isEmpty();
        assertThatThrownBy(() -> TrackCatalog.find("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
    }
}
