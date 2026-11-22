package com.jinloes.practice_plugin.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.run.RunEvent;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

/**
 * Coordinates result banners, attempt freshness checks, edit-staleness feedback, status-bar text,
 * error notifications, and {@link RunEvent} transitions for {@link PracticePanel}.
 */
final class RunStatusPresenter {
    private final Project project;
    private final ManagedPracticeProgress progress;
    private final ManagedPracticeWorkspace workspace;
    private final AttemptBar attemptBar;
    private final ResultsView results;
    private final JBLabel status;
    private final BackgroundTasks tasks;

    RunStatusPresenter(Project project,
                       ManagedPracticeProgress progress,
                       ManagedPracticeWorkspace workspace,
                       AttemptBar attemptBar,
                       ResultsView results,
                       JBLabel status,
                       BackgroundTasks tasks) {
        this.project = project;
        this.progress = progress;
        this.workspace = workspace;
        this.attemptBar = attemptBar;
        this.results = results;
        this.status = status;
        this.tasks = tasks;
    }

    void showNoAttempt() {
        status.setText("Choose Start Attempt to create your first managed attempt.");
    }

    void showAttempt(ManagedPracticeWorkspace.Attempt attempt,
                     ManagedPracticeProgress.Entry entry) {
        status.setText("Attempt " + attempt.id() + ": " + ResultsView.label(entry.status));
        results.showEntry(entry);
        if (!entry.checkedFingerprint.isEmpty()) {
            tasks.run("Checking result freshness", () -> workspace.fingerprint(attempt), hash -> {
                if (attempt.equals(attemptBar.selected())
                        && (!hash.equals(entry.checkedFingerprint) || AttemptFiles.hasUnsavedChanges(attempt))) {
                    status.setText("Edited since last check; previous result is stale.");
                    results.markStale("check the current solution again.");
                }
            });
        }
    }

    void onAttemptEdited(ManagedPracticeWorkspace.Attempt attempt, Exercise exercise) {
        if (attempt == null || exercise == null) {
            return;
        }
        var entry = progress.entry(attempt.id(), exercise.id());
        if (!entry.checkedFingerprint.isEmpty() || results.hasDisplayedResult()) {
            status.setText("Edited since displayed result; check again.");
            results.markStale("solution was edited; run or check again.");
        }
    }

    void error(String text) {
        attemptBar.resetBusy();
        status.setText("Action failed; see Results.");
        results.showError(text);
        NotificationGroupManager.getInstance().getNotificationGroup("Algorithm Practice")
                .createNotification(text, NotificationType.ERROR).notify(project);
    }

    void onRunEvent(RunEvent event, Runnable onCheckStateChanged, Runnable updateButtons) {
        switch (event) {
            case RunEvent.Started started -> {
                if (started.attemptId().isEmpty() || attemptBar.matches(started.attemptId())) {
                    results.showRunning();
                    results.showOutput(started.text());
                    if (AttemptFiles.hasUnsavedChanges(attemptBar.selected())) {
                        results.markStale("there are unsaved changes. Check again after saving.");
                    }
                }
                status.setText("Running...");
                updateButtons.run();
            }
            case RunEvent.FailedToStart failed -> {
                error(failed.text());
                updateButtons.run();
            }
            case RunEvent.CheckFinished finished -> {
                onCheckStateChanged.run();
                updateButtons.run();
                var entry = progress.entry(finished.attemptId(), finished.exerciseId());
                if (attemptBar.matches(finished.attemptId())) {
                    results.showStatus(entry);
                    showSelectedRunFinished(finished.text());
                } else {
                    status.setText("Check finished for " + finished.attemptId() + ": "
                            + ResultsView.label(entry.status));
                }
            }
            case RunEvent.ExampleFinished finished -> {
                updateButtons.run();
                if (finished.attemptId().isEmpty() || attemptBar.matches(finished.attemptId())) {
                    results.showRunFinished(finished.text());
                    showSelectedRunFinished(finished.text());
                } else {
                    status.setText("Example run finished for " + finished.attemptId() + ".");
                }
            }
        }
    }

    private void showSelectedRunFinished(String outputText) {
        results.showOutput(outputText);
        if (AttemptFiles.hasUnsavedChanges(attemptBar.selected())) {
            results.markStale("there are unsaved changes. Check again after saving.");
        }
        status.setText("Run finished; see Results.");
    }
}
