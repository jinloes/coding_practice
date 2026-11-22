package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ControlFlowException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Runs file-system work off the EDT and delivers the result, cancellation, or failure message back
 * on the EDT only while the owning view is still alive.
 */
final class BackgroundTasks {
    private final Project project;
    private final BooleanSupplier alive;
    private final Consumer<String> onError;
    private final Runnable onCancel;

    BackgroundTasks(Project project, BooleanSupplier alive, Consumer<String> onError) {
        this(project, alive, onError, () -> {});
    }

    BackgroundTasks(Project project, BooleanSupplier alive, Consumer<String> onError, Runnable onCancel) {
        this.project = project;
        this.alive = alive;
        this.onError = onError;
        this.onCancel = Objects.requireNonNull(onCancel, "onCancel");
    }

    <T> void run(String title, IoTask<T> operation, Consumer<T> completed) {
        AtomicBoolean cancelled = new AtomicBoolean();
        new Task.Backgroundable(project, title, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                execute(operation, completed, cancelled);
            }

            @Override
            public void onCancel() {
                if (cancelled.compareAndSet(false, true)) {
                    deliver(onCancel);
                }
            }
        }.queue();
    }

    <T> void execute(IoTask<T> operation, Consumer<T> completed, AtomicBoolean cancelled) {
        try {
            T result = operation.run();
            deliver(() -> completed.accept(result));
        } catch (IOException exception) {
            deliver(() -> onError.accept(nonBlankMessage(exception)));
        } catch (RuntimeException exception) {
            if (exception instanceof ControlFlowException) {
                if (cancelled.compareAndSet(false, true)) {
                    deliver(onCancel);
                }
                throw exception;
            }
            deliver(() -> onError.accept(nonBlankMessage(exception)));
        }
    }

    private static String nonBlankMessage(Throwable exception) {
        String message = exception.getMessage();
        return message != null && !message.isBlank() ? message : exception.getClass().getSimpleName();
    }

    private void deliver(Runnable action) {
        var application = ApplicationManager.getApplication();
        if (application == null) {
            if (alive.getAsBoolean()) {
                action.run();
            }
            return;
        }
        application.invokeLater(() -> {
            if (alive.getAsBoolean() && (project == null || !project.isDisposed())) {
                action.run();
            }
        });
    }

    @FunctionalInterface
    interface IoTask<T> {
        T run() throws IOException;
    }
}
