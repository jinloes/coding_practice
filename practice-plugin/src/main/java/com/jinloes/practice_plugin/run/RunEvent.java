package com.jinloes.practice_plugin.run;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Structured lifecycle updates published by {@link PracticeRunner} on the event dispatch thread so
 * UI listeners can match completions to the attempt that ran without tracking a separate run-kind
 * flag.
 */
public sealed interface RunEvent {
    String text();

    record Started(String attemptId, String text) implements RunEvent {
        public Started {
            attemptId = Objects.requireNonNullElse(attemptId, "");
            text = Objects.requireNonNullElse(text, "");
        }
    }

    record ExampleFinished(String attemptId, String text) implements RunEvent {
        public ExampleFinished {
            attemptId = Objects.requireNonNullElse(attemptId, "");
            text = Objects.requireNonNullElse(text, "");
        }
    }

    record CheckFinished(String attemptId, String exerciseId, String text) implements RunEvent {
        public CheckFinished {
            attemptId = Objects.requireNonNullElse(attemptId, "");
            exerciseId = Objects.requireNonNullElse(exerciseId, "");
            text = Objects.requireNonNullElse(text, "");
        }
    }

    record FailedToStart(String text) implements RunEvent {
        public FailedToStart {
            text = Objects.requireNonNullElse(text, "");
        }
    }

    static String attemptId(ManagedPracticeWorkspace.Attempt attempt) {
        return Optional.ofNullable(attempt).map(ManagedPracticeWorkspace.Attempt::id).orElse("");
    }

    /** Thread-safe listener registry shared by one {@link PracticeRunner}. */
    final class Listeners {
        private final List<Consumer<RunEvent>> entries = new CopyOnWriteArrayList<>();

        void addEvent(Consumer<RunEvent> listener) {
            entries.add(listener);
        }

        void remove(Consumer<RunEvent> listener) {
            entries.remove(listener);
        }

        int size() {
            return entries.size();
        }

        void publish(Project project, RunEvent event) {
            ApplicationManager.getApplication().invokeLater(() -> {
                if (!project.isDisposed()) {
                    entries.forEach(each -> each.accept(event));
                }
            });
        }
    }
}
