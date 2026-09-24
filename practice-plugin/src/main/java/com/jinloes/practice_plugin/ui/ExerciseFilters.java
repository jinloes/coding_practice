package com.jinloes.practice_plugin.ui;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTextField;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.Locale;

/** The exercise search box and topic, difficulty, and progress filters above the catalog list. */
final class ExerciseFilters {
    private final JComboBox<String> topic = new JComboBox<>();
    private final JComboBox<String> difficulty = new JComboBox<>();
    private final JComboBox<String> progress =
            new JComboBox<>(new String[]{"All progress", "Passed before", "Not passed"});
    private final JBTextField search = new JBTextField();

    ExerciseFilters() {
        search.getEmptyText().setText("Search exercises");
        search.getAccessibleContext().setAccessibleName("Search exercises");
        topic.addItem("All topics");
        ExerciseCatalog.all().stream().map(Exercise::topic).distinct().sorted().forEach(topic::addItem);
        difficulty.addItem("All difficulties");
        ExerciseCatalog.all().stream().map(Exercise::difficulty).distinct().sorted().forEach(difficulty::addItem);
        topic.getAccessibleContext().setAccessibleName("Topic filter");
        difficulty.getAccessibleContext().setAccessibleName("Difficulty filter");
        progress.getAccessibleContext().setAccessibleName("Progress filter");
    }

    /** Adds the search row, then the filter row, to a one-column header. */
    void addTo(JPanel header) {
        header.add(search);
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

    boolean matches(Exercise exercise, boolean passed) {
        String query = search.getText().toLowerCase(Locale.ROOT);
        return (topic.getSelectedIndex() == 0 || exercise.topic().equals(topic.getSelectedItem()))
                && (difficulty.getSelectedIndex() == 0 || exercise.difficulty().equals(difficulty.getSelectedItem()))
                && (progress.getSelectedIndex() == 0 || passed == (progress.getSelectedIndex() == 1))
                && (exercise.title() + " " + exercise.topic()).toLowerCase(Locale.ROOT).contains(query);
    }
}
