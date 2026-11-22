package com.jinloes.system_design_plugin.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.Alarm;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;
import com.jinloes.system_design_plugin.state.DesignProgress;
import com.jinloes.system_design_plugin.web.DesignSession;
import com.jinloes.system_design_plugin.web.PageEvent;
import com.jinloes.system_design_plugin.web.PageState;
import com.jinloes.system_design_plugin.web.WebAssets;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace.Attempt;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** Drives the tool window: turns page events into workspace actions and renders state back into the page. */
final class DesignController implements Disposable {
    private static final Logger LOG = Logger.getInstance(DesignController.class);

    private final Project project;
    private final BrowserPage page;
    private final DesignSession session;
    private final Alarm refresh = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
    private final AtomicLong renderSequence = new AtomicLong();
    private volatile boolean running;
    private volatile AtomicBoolean canceled = new AtomicBoolean();
    private volatile ProgressIndicator indicator;

    DesignController(Project project) {
        this.project = project;
        DesignWorkspace workspace = workspace();
        DesignProgress progress = progress();
        this.session = new DesignSession(workspace, progress);
        page = new BrowserPage(this, WebAssets.INDEX, this::onMessage, this::push);
        DesignProgress.Subscription subscription = progress.subscribe(() -> {
            refresh.cancelAllRequests();
            refresh.addRequest(this::push, 300);
        });
        Disposer.register(this, subscription::close);
        ApplicationManager.getApplication().getMessageBus().connect(this)
                .subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
                    @Override
                    public void after(@NotNull List<? extends VFileEvent> events) {
                        for (VFileEvent event : events) {
                            if (workspace.isAttemptsPath(event.getPath())) {
                                refresh.cancelAllRequests();
                                refresh.addRequest(DesignController.this::push, 300);
                                return;
                            }
                        }
                    }
                });
    }

    JComponent component() {
        return page.component();
    }

    private void onMessage(String json) {
        PageEvent event;
        try {
            event = PageEvent.parse(json);
        } catch (IllegalArgumentException rejected) {
            LOG.debug("Ignored page event: ", rejected.getMessage());
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> handle(event), project.getDisposed());
    }

    private static DesignWorkspace workspace() {
        return ApplicationManager.getApplication().getService(DesignWorkspace.class);
    }

    private static DesignProgress progress() {
        return ApplicationManager.getApplication().getService(DesignProgress.class);
    }

    private void handle(PageEvent event) {
        session.clearError();
        try {
            switch (event.type()) {
                case SELECT_PROBLEM -> session.selectProblem(event.id());
                case NEW_ATTEMPT -> open(session.createAttempt(event.id()));
                case SELECT_ATTEMPT -> session.selectAttempt(event.id());
                case OPEN_ATTEMPT -> open(session.readAttempt(event.id()));
                case RENAME_ATTEMPT -> session.renameAttempt(event.id(), event.text());
                case DELETE_ATTEMPT -> session.deleteAttempt(
                        event.id(), running || CopilotInterviewer.isRunning(), this::closeAttemptEditors);
                case REVEAL_HINT -> session.revealHint(event.id());
                case REQUEST_FEEDBACK -> requestFeedback(session.readAttempt(event.id()), "");
                case SUBMIT_FOLLOW_UP -> requestFeedback(session.readAttempt(event.id()), event.text());
                case SELECT_LEVEL -> session.selectLevel(event.text());
                case CANCEL_FEEDBACK -> cancel();
                case OPEN_SETTINGS -> InterviewerSettingsDialog.edit(project, progress());
            }
        } catch (IOException | IllegalArgumentException exception) {
            session.setError(exception.getMessage());
        }
        push();
    }

    // VFS refresh is a slow operation that is prohibited on the EDT, so resolve files on a pooled thread.
    private void open(Attempt attempt) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<VirtualFile> files = new java.util.ArrayList<>();
            String missing = "";
            for (Path path : List.of(attempt.notes(), attempt.diagram())) {
                VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(path);
                if (file == null) {
                    missing = "Could not open " + path.getFileName() + ".";
                    break;
                }
                files.add(file);
            }
            String failure = missing;
            ApplicationManager.getApplication().invokeLater(() -> {
                if (!failure.isEmpty()) {
                    session.setError(failure);
                    push();
                    return;
                }
                FileEditorManager editors = FileEditorManager.getInstance(project);
                files.forEach(file -> editors.openFile(file, true));
            }, project.getDisposed());
        });
    }

    private void closeAttemptEditors(Attempt attempt) {
        for (Path path : List.of(attempt.notes(), attempt.diagram())) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByNioFile(path);
            if (file != null) {
                for (Project openProject : ProjectManager.getInstance().getOpenProjects()) {
                    FileEditorManager.getInstance(openProject).closeFile(file);
                }
            }
        }
    }

    private void requestFeedback(Attempt attempt, String followUpAnswers) {
        if (running || CopilotInterviewer.isRunning()) {
            session.setError("A feedback request is already running.");
            return;
        }
        FileDocumentManager.getInstance().saveAllDocuments();
        List<DiagramEditor> editors = DiagramEditor.showing(attempt.diagram().toAbsolutePath().normalize());
        CompletableFuture<?>[] flushes = editors.stream().map(DiagramEditor::requestFlush)
                .toArray(CompletableFuture[]::new);
        AtomicBoolean cancel = new AtomicBoolean();
        canceled = cancel;
        running = true;
        DesignProgress progress = progress();
        CopilotInterviewer.Settings run = progress.settings();
        String targetLevel = progress.targetLevel();
        Problem problem = DesignCatalog.find(attempt.problemId());
        String taskTitle = followUpAnswers.isBlank() ? "Getting interviewer feedback" : "Reviewing follow-up answers";
        ProgressManager.getInstance().run(new Task.Backgroundable(project, taskTitle, true) {
            private String failure = "";
            private boolean completed;

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                indicator = progressIndicator;
                try {
                    CompletableFuture.allOf(flushes).get(DiagramEditor.FLUSH_MILLIS + 500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    failure = cancel.get() || progressIndicator.isCanceled()
                            ? "" : "Could not finish saving the open diagram before requesting feedback. Try again.";
                    return;
                } catch (Exception timeout) {
                    LOG.debug("Diagram flush before feedback did not complete", timeout);
                    failure = cancel.get() || progressIndicator.isCanceled()
                            ? "" : "Could not finish saving the open diagram before requesting feedback. Try again.";
                    return;
                }
                if (!editors.isEmpty()) {
                    ApplicationManager.getApplication().invokeAndWait(() -> editors.forEach(DiagramEditor::writePending));
                    Optional<String> saveError = editors.stream()
                            .map(DiagramEditor::lastSaveError)
                            .filter(message -> !message.isEmpty())
                            .findFirst();
                    if (saveError.isPresent()) {
                        failure = saveError.get();
                        return;
                    }
                }
                try {
                    DesignSession.PreparedReview prepared =
                            session.preparePrompt(attempt, targetLevel, followUpAnswers);
                    InterviewFeedback feedback = new CopilotInterviewer().run(
                            prepared.prompt(), run, () -> cancel.get() || progressIndicator.isCanceled());
                    session.recordCompletedReview(prepared, feedback, run.model());
                    completed = true;
                } catch (CopilotInterviewer.InterviewException | IOException | RuntimeException exception) {
                    if (!(exception instanceof CopilotInterviewer.InterviewException)) {
                        LOG.warn("Feedback request failed", exception);
                    }
                    failure = cancel.get() || progressIndicator.isCanceled()
                            ? "" : String.valueOf(exception.getMessage());
                }
            }

            @Override
            public void onFinished() {
                running = false;
                indicator = null;
                if (completed) {
                    announce("Interviewer feedback is ready for " + problem.title() + ".", NotificationType.INFORMATION);
                } else if (!failure.isEmpty()) {
                    session.setError(failure);
                }
                push();
            }

            private void announce(String message, NotificationType type) {
                NotificationGroupManager.getInstance().getNotificationGroup("System Design Practice")
                        .createNotification(message, type).notify(project);
            }
        });
    }

    private void cancel() {
        canceled.set(true);
        ProgressIndicator current = indicator;
        if (current != null) {
            current.cancel();
        }
    }

    /** Recomputes the view off the EDT and renders it; stale renders are dropped. */
    private void push() {
        refresh.cancelAllRequests();
        long sequence = renderSequence.incrementAndGet();
        PageState.Theme theme = IdeTheme.current();
        String problemId = session.selectedProblem();
        boolean isRunning = running;
        String currentError = session.error();
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            String json = PageState.toJson(
                    session.view(problemId, isRunning, currentError, ZoneId.systemDefault()), theme);
            ApplicationManager.getApplication().invokeLater(() -> {
                if (sequence == renderSequence.get()) {
                    page.execute("window.designRender(" + json + ");");
                }
            }, ignored -> project.isDisposed());
        });
    }

    @Override
    public void dispose() {
        cancel();
    }
}
