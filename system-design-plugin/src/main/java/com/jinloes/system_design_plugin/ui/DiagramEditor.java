package com.jinloes.system_design_plugin.ui;

import com.google.gson.Gson;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.jinloes.system_design_plugin.web.PageEvent;
import com.jinloes.system_design_plugin.web.WebAssets;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Excalidraw editor for an attempt's {@code design.excalidraw}. The page sends whole scenes; each is
 * validated, re-checked against workspace ownership, and written through the VFS. A rejected or failed save is
 * reported in the page, which keeps the scene dirty and retries on the next change or flush.
 */
final class DiagramEditor extends UserDataHolderBase implements FileEditor {
    static final long FLUSH_MILLIS = 2_000;
    private static final Logger LOG = Logger.getInstance(DiagramEditor.class);
    private static final Gson GSON = new Gson();
    private static final Map<DiagramEditor, Boolean> OPEN = new WeakHashMap<>();

    private record PendingSave(long seq, String scene) {
    }

    private record PendingFlush(long seq, CompletableFuture<Void> future) {
    }

    private final VirtualFile file;
    private final BrowserPage page;
    private final AtomicReference<PendingSave> pendingScene = new AtomicReference<>();
    private final AtomicLong nextFlushSeq = new AtomicLong();
    private final AtomicReference<PendingFlush> flushed = new AtomicReference<>();
    private volatile boolean valid = true;
    private volatile boolean editorReady;
    private long lastWrittenSeq;
    private long lastHandledSeq;
    private volatile String lastSaveError = "";

    DiagramEditor(Project project, VirtualFile file) {
        this.file = file;
        page = new BrowserPage(this, WebAssets.EDITOR, this::onMessage, this::pushTheme);
        var connection = project.getMessageBus().connect(this);
        connection.subscribe(FileEditorManagerListener.Before.FILE_EDITOR_MANAGER, new FileEditorManagerListener.Before() {
            @Override
            public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile closing) {
                if (closing.equals(file)) {
                    flush();
                }
            }
        });
        ApplicationManager.getApplication().getMessageBus().connect(this)
                .subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener() {
                    @Override
                    public void appWillBeClosed(boolean isRestart) {
                        flush();
                    }
                });
        synchronized (OPEN) {
            OPEN.put(this, Boolean.TRUE);
        }
    }

    /** Open diagram editors showing {@code diagram}. */
    static List<DiagramEditor> showing(Path diagram) {
        synchronized (OPEN) {
            return OPEN.keySet().stream()
                    .filter(editor -> editor.valid && diagram.equals(editor.file.toNioPath().toAbsolutePath().normalize()))
                    .toList();
        }
    }

    private void onMessage(String json) {
        PageEvent.EditorMessage message;
        try {
            message = PageEvent.parseEditor(json);
        } catch (PageEvent.InvalidSceneException rejected) {
            if (rejected.saveFailure()) {
                recordRejectedSave(rejected.seq(), rejected.getMessage());
                page.execute("window.designSaveResult(" + rejected.seq() + ", "
                        + GSON.toJson(rejected.getMessage()) + ");");
            } else {
                failPendingFlush("Malformed diagram flush acknowledgement.");
            }
            return;
        }
        switch (message.kind()) {
            case READY -> {
                failPendingFlush("Diagram editor reloaded.");
                editorReady = true;
                ApplicationManager.getApplication().invokeLater(this::load, ignored -> !valid);
            }
            case SAVE -> {
                PendingSave next = new PendingSave(message.seq(), message.scene());
                pendingScene.accumulateAndGet(next,
                        (prev, candidate) -> prev != null && prev.seq() > candidate.seq() ? prev : candidate);
                ApplicationManager.getApplication().invokeLater(this::writePending, ignored -> !valid);
            }
            case FLUSHED -> {
                PendingFlush current = flushed.get();
                if (current != null && current.seq() == message.seq() && flushed.compareAndSet(current, null)) {
                    current.future().complete(null);
                }
            }
        }
    }

    private void load() {
        try {
            if (DesignWorkspace.get().ownerOfDiagram(file.toNioPath()).isEmpty()) {
                throw new IOException("This file is not a System Design Practice attempt.");
            }
            file.refresh(false, false);
            String scene = new String(file.contentsToByteArray(false), StandardCharsets.UTF_8);
            clearSaveState();
            page.execute("window.designLoad(" + GSON.toJson(scene) + ", " + IdeTheme.isDark() + ");");
        } catch (IOException | RuntimeException exception) {
            LOG.warn("Could not load " + file.getPath(), exception);
            clearSaveState();
            page.execute("window.designLoad('', " + IdeTheme.isDark() + ");window.designSaveResult(-1, "
                    + GSON.toJson("Could not load the diagram: " + exception.getMessage()) + ");");
        }
    }

    private void pushTheme() {
        page.execute("window.designTheme && window.designTheme(" + IdeTheme.isDark() + ");");
    }

    /** Writes the latest scene the page sent, if any. Must run on the EDT. */
    void writePending() {
        PendingSave pending = pendingScene.getAndSet(null);
        if (pending == null || !valid || !shouldWrite(pending.seq())) {
            return;
        }
        String error = "";
        try {
            if (DesignWorkspace.get().ownerOfDiagram(file.toNioPath()).isEmpty()) {
                throw new IOException("The diagram is no longer inside the practice workspace.");
            }
            WriteAction.run(() -> file.setBinaryContent(pending.scene().getBytes(StandardCharsets.UTF_8), -1, -1, this));
        } catch (IOException | RuntimeException exception) {
            LOG.warn("Could not save " + file.getPath(), exception);
            error = "The diagram was not saved: " + exception.getMessage();
        }
        recordSaveOutcome(pending.seq(), error);
        page.execute("window.designSaveResult(" + pending.seq() + ", " + GSON.toJson(error) + ");");
    }

    String lastSaveError() {
        return lastSaveError;
    }

    private synchronized void clearSaveState() {
        pendingScene.set(null);
        lastWrittenSeq = 0;
        lastHandledSeq = 0;
        lastSaveError = "";
    }

    private synchronized boolean shouldWrite(long seq) {
        if (seq <= lastWrittenSeq) {
            return false;
        }
        lastWrittenSeq = seq;
        return true;
    }

    private synchronized void recordSaveOutcome(long seq, String error) {
        if (seq >= lastHandledSeq) {
            lastHandledSeq = seq;
            lastSaveError = error;
        }
    }

    private synchronized void recordRejectedSave(long seq, String error) {
        if (seq < 0) {
            PendingSave queued = pendingScene.get();
            if (queued != null && queued.seq() > lastHandledSeq) {
                lastHandledSeq = queued.seq();
            }
            lastHandledSeq++;
            lastSaveError = error;
        } else if (seq >= lastHandledSeq) {
            lastHandledSeq = seq;
            lastSaveError = error;
        }
    }

    private void failPendingFlush(String reason) {
        PendingFlush pending = flushed.getAndSet(null);
        if (pending != null) {
            pending.future().completeExceptionally(new CancellationException(reason));
        }
    }

    /** Asks the page to save pending changes; completes when it confirms or fails after {@link #FLUSH_MILLIS}. */
    CompletableFuture<Void> requestFlush() {
        if (!valid || !page.isLoaded() || !editorReady) {
            return CompletableFuture.completedFuture(null);
        }
        long seq = nextFlushSeq.incrementAndGet();
        CompletableFuture<Void> waiting = new CompletableFuture<Void>().orTimeout(FLUSH_MILLIS, TimeUnit.MILLISECONDS);
        PendingFlush current = new PendingFlush(seq, waiting);
        PendingFlush previous = flushed.getAndSet(current);
        if (previous != null) {
            waiting.whenComplete((result, failure) -> {
                if (failure != null) {
                    previous.future().completeExceptionally(failure);
                } else {
                    previous.future().complete(result);
                }
            });
        }
        if (!valid || !editorReady) {
            if (flushed.compareAndSet(current, null)) {
                waiting.completeExceptionally(new CancellationException("Diagram editor closed."));
            }
            return waiting;
        }
        page.execute("window.designFlush && window.designFlush(" + seq + ");");
        return waiting;
    }

    /** Flushes synchronously, bounded to {@link #FLUSH_MILLIS}. Must run on the EDT. */
    void flush() {
        try {
            requestFlush().get(FLUSH_MILLIS + 100, TimeUnit.MILLISECONDS);
        } catch (Exception timeout) {
            LOG.debug("Diagram flush did not complete", timeout);
        }
        writePending();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return page.component();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return page.component();
    }

    @Override
    public @NotNull String getName() {
        return "Diagram";
    }

    @Override
    public @NotNull VirtualFile getFile() {
        return file;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return pendingScene.get() != null;
    }

    @Override
    public boolean isValid() {
        return valid && file.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void dispose() {
        valid = false;
        editorReady = false;
        failPendingFlush("Diagram editor closed.");
        synchronized (OPEN) {
            OPEN.remove(this);
        }
    }
}
