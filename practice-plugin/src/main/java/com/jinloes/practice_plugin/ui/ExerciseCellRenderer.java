package com.jinloes.practice_plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.EmptyIcon;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import org.jetbrains.annotations.NotNull;

import javax.swing.JList;
import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** Catalog rows: a passed check mark, the title, a colored difficulty, and the topic. */
final class ExerciseCellRenderer extends ColoredListCellRenderer<Exercise> {
    private static final Map<String, JBColor> DIFFICULTY_COLORS = Map.of(
            "Easy", new JBColor(new Color(0x067D5B), new Color(0x5FB865)),
            "Medium", new JBColor(new Color(0xA35C00), new Color(0xE0A83E)),
            "Hard", new JBColor(new Color(0xC92A2A), new Color(0xF06C6C)));

    private final Supplier<Set<String>> passed;

    ExerciseCellRenderer(Supplier<Set<String>> passed) {
        this.passed = passed;
    }

    static JBColor difficultyColor(String difficulty) {
        return DIFFICULTY_COLORS.getOrDefault(difficulty, JBColor.GRAY);
    }

    @Override
    protected void customizeCellRenderer(
            @NotNull JList<? extends Exercise> list, Exercise exercise, int index, boolean selected, boolean focus
    ) {
        if (exercise == null) {
            return;
        }
        boolean done = passed.get().contains(exercise.id());
        setIcon(done ? AllIcons.RunConfigurations.TestPassed : EmptyIcon.ICON_16);
        append(exercise.title());
        append("  " + exercise.difficulty(),
                new SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, difficultyColor(exercise.difficulty())));
        append("  " + exercise.topic(), SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES);
        setToolTipText(done ? "Passed a full check before" : null);
    }
}
