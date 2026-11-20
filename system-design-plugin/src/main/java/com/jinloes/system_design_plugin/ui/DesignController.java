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
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.Alarm;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;
import com.jinloes.system_design_plugin.interview.DiagramSummary;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;
import com.jinloes.system_design_plugin.interview.InterviewPrompt;
import com.jinloes.system_design_plugin.state.DesignProgress;
import com.jinloes.system_design_plugin.web.PageEvent;
import com.jinloes.system_design_plugin.web.PageState;
import com.jinloes.system_design_plugin.web.ToolWindowView;
import com.jinloes.system_design_plugin.web.WebAssets;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace.Attempt;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
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
    private final Alarm refresh = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
    private final AtomicLong renderSequence = new AtomicLong();
    private volatile String selectedProblem;
    private volatile boolean running;
    private volatile String error = "";
    private volatile AtomicBoolean canceled = new AtomicBoolean();
    private volatile ProgressIndicator indicator;

    DesignController(Project project) {
        this.project = project;
        page = new BrowserPage(this, WebAssets.INDEX, this::onMessage, this::push);
        DesignWorkspace workspace = workspace();
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
        error = "";
        DesignProgress progress = progress();
        try {
            switch (event.type()) {
                case SELECT_PROBLEM -> {
                    if (DesignCatalog.exists(event.id())) {
                        selectedProblem = event.id();
                    }
                }
                case NEW_ATTEMPT -> {
                    Problem problem = DesignCatalog.find(event.id());
                    Attempt attempt = workspace().create(problem);
                    progress.entry(attempt.id(), problem.id());
                    progress.select(problem.id(), attempt.id());
                    selectedProblem = problem.id();
                    open(attempt);
                }
                case SELECT_ATTEMPT -> {
                    Attempt attempt = workspace().read(event.id());
                    progress.select(attempt.problemId(), attempt.id());
                }
                case OPEN_ATTEMPT -> open(workspace().read(event.id()));
                case RENAME_ATTEMPT -> {
                    Attempt attempt = workspace().read(event.id());
                    progress.renameAttempt(attempt.id(), attempt.problemId(), event.text());
                }
                case DELETE_ATTEMPT -> deleteAttempt(event.id(), progress);
                case REVEAL_HINT -> {
                    Attempt attempt = workspace().read(event.id());
                    progress.revealHint(attempt.id(), attempt.problemId());
                }
                case REQUEST_FEEDBACK -> requestFeedback(workspace().read(event.id()), "");
                case SUBMIT_FOLLOW_UP -> requestFeedback(workspace().read(event.id()), event.text());
                case SELECT_LEVEL -> progress.setTargetLevel(event.text());
                case CANCEL_FEEDBACK -> cancel();
                case OPEN_SETTINGS -> InterviewerSettingsDialog.edit(project, progress);
            }
        } catch (IOException | IllegalArgumentException exception) {
            error = exception.getMessage();
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
                    error = failure;
                    push();
                    return;
                }
                FileEditorManager editors = FileEditorManager.getInstance(project);
                files.forEach(file -> editors.openFile(file, true));
            }, project.getDisposed());
        });
    }

    private void deleteAttempt(String attemptId, DesignProgress progress) throws IOException {
        if (running || CopilotInterviewer.isRunning()) {
            error = "Finish or cancel the running review before deleting an attempt.";
            return;
        }
        DesignWorkspace workspace = workspace();
        Attempt attempt = workspace.read(attemptId);
        for (Path path : List.of(attempt.notes(), attempt.diagram())) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByNioFile(path);
            if (file != null) {
                for (Project openProject : ProjectManager.getInstance().getOpenProjects()) {
                    FileEditorManager.getInstance(openProject).closeFile(file);
                }
            }
        }
        workspace.delete(attempt.id());
        progress.removeAttempt(attempt.id());
    }

    private void requestFeedback(Attempt attempt, String followUpAnswers) {
        if (running || CopilotInterviewer.isRunning()) {
            error = "A feedback request is already running.";
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
            private String verdict;

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
                    DesignWorkspace workspace = workspace();
                    String fingerprint = workspace.fingerprint(attempt);
                    String notes = workspace.readNotes(attempt);
                    String summary = DiagramSummary.summarize(workspace.readDiagram(attempt));
                    List<String> priorFollowUps = followUpAnswers.isBlank()
                            ? List.of()
                            : workspace.latestFeedback(attempt, InterviewFeedback.Stored::fromJson)
                                    .map(stored -> stored.feedback().followUpQuestions())
                                    .orElse(List.of());
                    InterviewFeedback feedback = new CopilotInterviewer().run(
                            InterviewPrompt.build(problem, targetLevel, notes, summary, priorFollowUps, followUpAnswers),
                            run, () -> cancel.get() || progressIndicator.isCanceled());
                    String round = followUpAnswers.isBlank()
                            ? InterviewFeedback.ROUND_INITIAL
                            : InterviewFeedback.ROUND_FOLLOW_UP;
                    workspace.saveFeedback(attempt, new InterviewFeedback.Stored(feedback, fingerprint,
                            run.model(), java.time.Instant.now().toString(), targetLevel, round, followUpAnswers).toJson());
                    verdict = feedback.parsed() ? feedback.verdict() : "";
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
                if (verdict != null) {
                    progress().recordReview(attempt.id(), attempt.problemId(), verdict);
                    announce("Interviewer feedback is ready for " + problem.title() + ".", NotificationType.INFORMATION);
                } else if (!failure.isEmpty()) {
                    error = failure;
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
        long sequence = renderSequence.incrementAndGet();
        PageState.Theme theme = IdeTheme.current();
        String problemId = selectedProblem;
        boolean isRunning = running;
        String currentError = error;
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            String json = PageState.toJson(view(problemId, isRunning, currentError), theme);
            ApplicationManager.getApplication().invokeLater(() -> {
                if (sequence == renderSequence.get()) {
                    page.execute("window.designRender(" + json + ");");
                }
            }, ignored -> project.isDisposed());
        });
    }

    private PageState.View view(String problemId, boolean isRunning, String currentError) {
        DesignProgress progress = progress();
        DesignWorkspace workspace = workspace();
        ToolWindowView.AttemptStore store = new ToolWindowView.AttemptStore() {
            @Override
            public List<ToolWindowView.AttemptEntry> attempts(String problem) throws IOException {
                return workspace.attempts(problem).stream()
                        .map(attempt -> new ToolWindowView.AttemptEntry(attempt.id(), created(attempt)))
                        .toList();
            }

            @Override
            public List<InterviewFeedback.Stored> feedbackHistory(String attemptId) throws IOException {
                try {
                    return workspace.feedbackHistory(attemptId, InterviewFeedback.Stored::fromJson);
                } catch (IOException exception) {
                    LOG.debug("Could not read feedback", exception);
                    throw exception;
                }
            }

            @Override
            public String fingerprint(String attemptId) throws IOException {
                try {
                    return workspace.fingerprint(attemptId);
                } catch (IOException exception) {
                    LOG.debug("Could not fingerprint attempt", exception);
                    throw exception;
                }
            }
        };
        return ToolWindowView.build(problemId, isRunning, currentError, progress.bestVerdicts(),
                progress.targetLevel(), progress::selectedAttempt, progress::attemptLabel,
                progress::hintsRevealed, store, ZoneId.systemDefault());
    }

    private static FileTime created(Attempt attempt) {
        try {
            return Files.readAttributes(attempt.directory(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
                    .creationTime();
        } catch (IOException exception) {
            return FileTime.fromMillis(0);
        }
    }

    @Override
    public void dispose() {
        cancel();
    }
}
