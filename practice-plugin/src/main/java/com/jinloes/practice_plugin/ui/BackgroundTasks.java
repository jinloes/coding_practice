package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Runs file-system work off the EDT and delivers the result, or the failure message, back on the
 * EDT only while the owning view is still alive.
 */
final class BackgroundTasks {
    private final Project project;
    private final BooleanSupplier alive;
    private final Consumer<String> onError;

    BackgroundTasks(Project project, BooleanSupplier alive, Consumer<String> onError) {
        this.project = project;
        this.alive = alive;
        this.onError = onError;
    }

    <T> void run(String title, IoTask<T> operation, Consumer<T> completed) {
        new Task.Backgroundable(project, title, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    T result = operation.run();
                    deliver(() -> completed.accept(result));
                } catch (IOException | IllegalArgumentException exception) {
                    deliver(() -> onError.accept(exception.getMessage()));
                }
            }
        }.queue();
    }

    private void deliver(Runnable action) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (alive.getAsBoolean() && !project.isDisposed()) {
                action.run();
            }
        });
    }

    @FunctionalInterface
    interface IoTask<T> {
        T run() throws IOException;
    }
}
