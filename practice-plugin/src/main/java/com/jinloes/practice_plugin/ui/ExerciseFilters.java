package com.jinloes.practice_plugin.ui;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTextField;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** The exercise search box and guide, topic, difficulty, and progress filters above the catalog list. */
final class ExerciseFilters {
    private static final String ALL_GUIDES = "All guides";

    private final JComboBox<Object> guide = new JComboBox<>();
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
        topic.addItem("All topics");
        ExerciseCatalog.all().stream().map(Exercise::topic).distinct().sorted().forEach(topic::addItem);
        difficulty.addItem("All difficulties");
        ExerciseCatalog.all().stream().map(Exercise::difficulty).distinct().sorted().forEach(difficulty::addItem);
        guide.getAccessibleContext().setAccessibleName("Guide filter");
        topic.getAccessibleContext().setAccessibleName("Topic filter");
        difficulty.getAccessibleContext().setAccessibleName("Difficulty filter");
        progress.getAccessibleContext().setAccessibleName("Progress filter");
    }

    /** Adds the search row, the guide row, then the filter row, to a one-column header. */
    void addTo(JPanel header) {
        header.add(search);
        header.add(guide);
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

    void onGuideChange(Runnable changed) {
        guide.addActionListener(event -> changed.run());
    }

    /** The selected guide, or null for all guides. */
    Guide selectedGuide() {
        return guide.getSelectedItem() instanceof Guide selected ? selected : null;
    }

    /**
     * The exercises to list: the selected guide's, easiest first, or the whole catalog in its own
     * order, narrowed by the other filters.
     */
    List<Exercise> visible(Set<String> passed) {
        Guide selected = selectedGuide();
        List<Exercise> candidates = selected == null ? ExerciseCatalog.all() : selected.exercises();
        return candidates.stream().filter(exercise -> matches(exercise, passed.contains(exercise.id()))).toList();
    }

    void selectGuide(String id) {
        guide.setSelectedItem(id == null ? ALL_GUIDES : GuideCatalog.find(id));
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
