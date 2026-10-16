package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrackCatalogTest {
    private static final List<String> NOTE_SECTIONS = List.of("About this list", "What to expect", "How to prepare");

    @Test
    void listsTheTracksWithTheirExercisesEasiestFirst() {
        assertThat(TrackCatalog.all()).extracting(Guide::id).containsExactly("blind-75", "neetcode-150", "google");
        assertThat(TrackCatalog.all()).extracting(Guide::title)
                .containsExactly("Blind 75", "NeetCode 150", "Google");
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
            for (String section : NOTE_SECTIONS) {
                assertThat(track.lesson().lines().map(String::strip))
                        .as("%s note section label", track.id())
                        .contains(section);
            }
        }
        assertThat(TrackCatalog.find("google").summary()).contains("Approximate");
    }

    @Test
    void tracksAreSeparateFromTopicGuides() {
        assertThat(TrackCatalog.all()).extracting(Guide::id)
                .doesNotContainAnyElementsOf(GuideCatalog.all().stream().map(Guide::id).toList());
        assertThatThrownBy(() -> GuideCatalog.find("google")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findsTheTracksThatIncludeAnExercise() {
        assertThat(TrackCatalog.containing("course-schedule")).extracting(Guide::id)
                .containsExactly("blind-75", "neetcode-150", "google");
        assertThat(TrackCatalog.containing("queens-placement")).extracting(Guide::id)
                .containsExactly("neetcode-150");
        assertThat(TrackCatalog.containing("roman-to-integer")).isEmpty();
        assertThatThrownBy(() -> TrackCatalog.find("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
    }
}
