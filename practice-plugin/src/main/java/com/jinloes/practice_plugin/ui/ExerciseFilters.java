package com.jinloes.practice_plugin.ui;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTextField;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import com.jinloes.practice_plugin.catalog.TrackCatalog;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The exercise search box and guide, practice track, topic, difficulty, and progress filters above the
 * catalog list.
 */
final class ExerciseFilters {
    private static final String ALL_GUIDES = "All guides";
    private static final String ALL_TRACKS = "All tracks";

    private final JComboBox<Object> guide = new JComboBox<>();
    private final JComboBox<Object> track = new JComboBox<>();
    private final JComboBox<String> topic = new JComboBox<>();
    private final JComboBox<String> difficulty = new JComboBox<>();
    private final JComboBox<String> progress =
            new JComboBox<>(new String[]{"All progress", "Passed before", "Not passed"});
    private final JBTextField search = new JBTextField();

    ExerciseFilters() {
        search.getEmptyText().setText("Search exercises");
        search.getAccessibleContext().setAccessibleName("Search exercises");
        guide.addItem(ALL_GUIDES);
        GuideCatalog.all().forEach(guide::addItem);
        guide.setRenderer(PracticeViews.labelled(Guide.class, Guide::title));
        track.addItem(ALL_TRACKS);
        TrackCatalog.all().forEach(track::addItem);
        track.setRenderer(PracticeViews.labelled(Guide.class, Guide::title));
        track.setToolTipText("Hand-curated practice tracks such as Blind 75 and company lists; not official question lists");
        topic.addItem("All topics");
        ExerciseCatalog.all().stream().map(Exercise::topic).distinct().sorted().forEach(topic::addItem);
        difficulty.addItem("All difficulties");
        ExerciseCatalog.all().stream().map(Exercise::difficulty).distinct().sorted().forEach(difficulty::addItem);
        guide.getAccessibleContext().setAccessibleName("Guide filter");
        track.getAccessibleContext().setAccessibleName("Track filter");
        topic.getAccessibleContext().setAccessibleName("Topic filter");
        difficulty.getAccessibleContext().setAccessibleName("Difficulty filter");
        progress.getAccessibleContext().setAccessibleName("Progress filter");
    }

    /** Adds the search row, the guide and track row, then the filter row, to a one-column header. */
    void addTo(JPanel header) {
        header.add(search);
        JPanel collections = new JPanel(new GridLayout(1, 2, 4, 0));
        collections.add(guide);
        collections.add(track);
        header.add(collections);
        JPanel filters = new JPanel(new GridLayout(1, 3, 4, 0));
        filters.add(topic);
        filters.add(difficulty);
        filters.add(progress);
        header.add(filters);
    }

    void onChange(Runnable changed) {
        topic.addActionListener(event -> changed.run());
        difficulty.addActionListener(event -> changed.run());
        progress.addActionListener(event -> changed.run());
        search.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull javax.swing.event.DocumentEvent event) {
                changed.run();
            }
        });
    }

    /** Runs {@code changed} when the selected guide or practice track changes. */
    void onGuideChange(Runnable changed) {
        guide.addActionListener(event -> changed.run());
        track.addActionListener(event -> changed.run());
    }

    /** The selected guide, or null for all guides. */
    Guide selectedGuide() {
        return guide.getSelectedItem() instanceof Guide selected ? selected : null;
    }

    /** The selected practice track, or null for all tracks. */
    Guide selectedTrack() {
        return track.getSelectedItem() instanceof Guide selected ? selected : null;
    }

    /** What the Study Guide tab shows: the selected guide, else the selected practice track, else null. */
    Guide shownGuide() {
        Guide selected = selectedGuide();
        return selected != null ? selected : selectedTrack();
    }

    /**
     * The exercises to list: the selected guide's, else the selected track's, easiest first,
     * or the whole catalog in its own order. A selected track also narrows a selected guide
     * to the problems both share. The other filters then narrow the result.
     */
    List<Exercise> visible(Set<String> passed) {
        Guide shown = shownGuide();
        Guide selectedTrack = selectedTrack();
        List<Exercise> candidates = shown == null ? ExerciseCatalog.all() : shown.exercises();
        return candidates.stream()
                .filter(exercise -> selectedTrack == null || selectedTrack.exercises().contains(exercise))
                .filter(exercise -> matches(exercise, passed.contains(exercise.id())))
                .toList();
    }

    void selectGuide(String id) {
        guide.setSelectedItem(id == null ? ALL_GUIDES : GuideCatalog.find(id));
    }

    void selectTrack(String id) {
        track.setSelectedItem(id == null ? ALL_TRACKS : TrackCatalog.find(id));
    }

    void selectDifficulty(String value) {
        difficulty.setSelectedItem(value);
    }

    private boolean matches(Exercise exercise, boolean passed) {
        String query = search.getText().toLowerCase(Locale.ROOT);
        return (topic.getSelectedIndex() == 0 || exercise.topic().equals(topic.getSelectedItem()))
                && (difficulty.getSelectedIndex() == 0 || exercise.difficulty().equals(difficulty.getSelectedItem()))
                && (progress.getSelectedIndex() == 0 || passed == (progress.getSelectedIndex() == 1))
                && (exercise.title() + " " + exercise.topic()).toLowerCase(Locale.ROOT).contains(query);
    }
}
