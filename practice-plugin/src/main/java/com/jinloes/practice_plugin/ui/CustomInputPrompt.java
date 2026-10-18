package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;

import java.util.HashMap;
import java.util.Map;

/** Asks for custom run input, prefilled with the last input used for that exercise or its sample. */
final class CustomInputPrompt {
    private final Map<String, String> lastInput = new HashMap<>();

    /** The trimmed input, or null when the learner cancels or enters nothing. */
    String ask(Project project, Exercise exercise, boolean debug) {
        String previous = lastInput.getOrDefault(exercise.id(), exercise.sampleInput());
        String input = Messages.showInputDialog(
                project,
                exercise.inputSyntax(),
                (debug ? "Debug " : "Run ") + exercise.title() + " With Input",
                null,
                previous,
                null);
        if (input == null || input.isBlank()) {
            return null;
        }
        String trimmed = input.trim();
        lastInput.put(exercise.id(), trimmed);
        return trimmed;
    }
}
