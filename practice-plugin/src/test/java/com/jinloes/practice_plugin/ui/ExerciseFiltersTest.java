package com.jinloes.practice_plugin.ui;

import com.intellij.testFramework.EdtTestUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.TrackCatalog;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ExerciseFiltersTest {

    @Test
    void openGuideAndOpenTrackAreMutuallyExclusiveAndFireGuideListenerOnce() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();
            AtomicInteger onChangeCount = new AtomicInteger();
            AtomicInteger onGuideChangeCount = new AtomicInteger();
            filters.onChange(onChangeCount::incrementAndGet);
            filters.onGuideChange(onGuideChangeCount::incrementAndGet);

            filters.selectTrack("blind-75");
            onChangeCount.set(0);
            onGuideChangeCount.set(0);

            filters.openGuide("graphs");
            assertThat(filters.selectedTrack()).isNull();
            assertThat(filters.selectedGuide()).isEqualTo(GuideCatalog.find("graphs"));
            assertThat(filters.shownGuide()).isEqualTo(GuideCatalog.find("graphs"));
            assertThat(onGuideChangeCount).hasValue(1);
            assertThat(onChangeCount).hasValue(0);

            filters.openTrack("google");
            assertThat(filters.selectedGuide()).isNull();
            assertThat(filters.selectedTrack()).isEqualTo(TrackCatalog.find("google"));
            assertThat(filters.shownGuide()).isEqualTo(TrackCatalog.find("google"));
            assertThat(onGuideChangeCount).hasValue(2);
            assertThat(onChangeCount).hasValue(0);
        });
    }

    @Test
    void hasActiveFiltersAndResetAllClearAllControlsWithASingleNotification() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();
            AtomicInteger onChangeCount = new AtomicInteger();
            AtomicInteger onGuideChangeCount = new AtomicInteger();
            filters.onChange(onChangeCount::incrementAndGet);
            filters.onGuideChange(onGuideChangeCount::incrementAndGet);

            assertThat(filters.hasActiveFilters()).isFalse();

            filters.setSearchText("graph");
            filters.selectGuide("graphs");
            filters.selectTrack("blind-75");
            filters.selectTopic("Graphs");
            filters.selectDifficulty("Medium");
            filters.selectProgress("Not passed");
            assertThat(filters.hasActiveFilters()).isTrue();

            onChangeCount.set(0);
            onGuideChangeCount.set(0);
            filters.resetAll();

            assertThat(filters.hasActiveFilters()).isFalse();
            assertThat(filters.selectedGuide()).isNull();
            assertThat(filters.selectedTrack()).isNull();
            assertThat(filters.shownGuide()).isNull();
            assertThat(filters.visible(Set.of())).containsExactlyElementsOf(ExerciseCatalog.all());
            assertThat(onGuideChangeCount).as("resetting an active guide/track notifies guide listeners once").hasValue(1);
            assertThat(onChangeCount).hasValue(0);

            filters.setSearchText("pair");
            filters.selectDifficulty("Easy");
            assertThat(filters.hasActiveFilters()).isTrue();
            onChangeCount.set(0);
            onGuideChangeCount.set(0);

            filters.resetAll();
            assertThat(filters.hasActiveFilters()).isFalse();
            assertThat(onChangeCount).as("resetting non-guide filters notifies change listeners once").hasValue(1);
            assertThat(onGuideChangeCount).hasValue(0);

            onChangeCount.set(0);
            filters.setSearchText("   ");
            assertThat(filters.hasActiveFilters())
                    .as("whitespace-only search text is not treated as an active filter")
                    .isFalse();
            assertThat(filters.visible(Set.of()))
                    .as("whitespace-only search text must not hide exercises without Title+Topic spaces")
                    .containsExactlyElementsOf(ExerciseCatalog.all());
            int afterWhitespace = onChangeCount.get();
            javax.swing.JPanel header = new javax.swing.JPanel();
            filters.addTo(header);
            var searchBox = com.intellij.util.ui.UIUtil.findComponentOfType(
                    header, com.intellij.ui.SearchTextField.class);
            assertThat(searchBox.getText()).isEqualTo("   ");
            filters.resetAll();
            assertThat(searchBox.getText()).isEmpty();
            assertThat(filters.visible(Set.of())).containsExactlyElementsOf(ExerciseCatalog.all());
            assertThat(onChangeCount).hasValue(afterWhitespace);
        });
    }

    @Test
    void revealClearsBlockingControlsWithAtMostOneNotification() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();
            var target = ExerciseCatalog.find("roman-to-integer");
            filters.setSearchText("definitely-not-matching");
            filters.selectGuide("graphs");
            filters.selectTrack("google");
            filters.selectTopic("Graphs");
            filters.selectDifficulty("Hard");
            filters.selectProgress("Passed before");

            AtomicInteger notifications = new AtomicInteger();
            filters.onChange(notifications::incrementAndGet);
            filters.onGuideChange(notifications::incrementAndGet);

            filters.reveal(target);

            assertThat(filters.visible(Set.of())).contains(target);
            assertThat(notifications).as("reveal batches filter mutations into one refresh").hasValue(1);
        });
    }

    @Test
    void guideFilterListsItsProblemsEasiestFirstAndCombinesWithOtherFilters() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();

            filters.selectGuide("stacks-queues");
            var guide = filters.selectedGuide();
            assertThat(filters.visible(Set.of()))
                    .as("the guide's exercises, Easy then Medium then Hard")
                    .isNotEmpty()
                    .containsExactlyElementsOf(guide.exercises());

            filters.selectDifficulty("Medium");
            assertThat(filters.visible(Set.of()))
                    .as("the existing filters still narrow a guide")
                    .isNotEmpty()
                    .containsExactlyElementsOf(guide.exercisesAt("Medium"));

            filters.selectGuide(null);
            filters.selectDifficulty("All difficulties");
            assertThat(filters.selectedGuide()).isNull();
            assertThat(filters.visible(Set.of()))
                    .as("all guides keeps the catalog order")
                    .containsExactlyElementsOf(ExerciseCatalog.all());
        });
    }

    @Test
    void trackFilterListsItsProblemsAndIntersectsWithAGuide() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();

            filters.selectTrack("google");
            assertThat(filters.shownGuide()).isSameAs(filters.selectedTrack());
            assertThat(filters.visible(Set.of()))
                    .as("the track's exercises, Easy then Medium then Hard")
                    .containsExactlyElementsOf(filters.selectedTrack().exercises());

            var track = filters.selectedTrack();
            filters.selectGuide("stacks-queues");
            assertThat(filters.shownGuide()).isSameAs(filters.selectedGuide());
            assertThat(filters.visible(Set.of()))
                    .as("a guide narrowed to the problems in the track")
                    .isNotEmpty()
                    .containsExactlyElementsOf(filters.selectedGuide().exercises().stream()
                            .filter(track.exercises()::contains)
                            .toList());

            filters.selectGuide(null);
            filters.selectTrack(null);
            assertThat(filters.shownGuide()).isNull();
            assertThat(filters.visible(Set.of())).containsExactlyElementsOf(ExerciseCatalog.all());
        });
    }
}
