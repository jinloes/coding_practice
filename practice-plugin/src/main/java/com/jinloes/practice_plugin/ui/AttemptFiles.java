package com.jinloes.practice_plugin.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Matches editor documents to the solution file of a managed attempt and prepares it for editing. */
final class AttemptFiles {
    private AttemptFiles() {
    }

    static boolean isSolution(VirtualFile file, ManagedPracticeWorkspace.Attempt attempt) {
        return attempt != null && file != null
                && Path.of(file.getPath()).toAbsolutePath().normalize().equals(attempt.solution());
    }

    static boolean hasUnsavedChanges(ManagedPracticeWorkspace.Attempt attempt) {
        if (attempt == null) {
            return false;
        }
        FileDocumentManager documents = FileDocumentManager.getInstance();
        for (var document : documents.getUnsavedDocuments()) {
            if (isSolution(documents.getFile(document), attempt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads the learner's current solution text, preferring the live IntelliJ editor document (including unsaved
     * edits) when open, and falling back to the solution file on disk.
     */
    static String readCurrentSource(ManagedPracticeWorkspace.Attempt attempt) throws IOException {
        if (attempt == null) {
            return "";
        }
        FileDocumentManager documents = FileDocumentManager.getInstance();
        if (documents != null) {
            for (Document document : documents.getUnsavedDocuments()) {
                if (isSolution(documents.getFile(document), attempt)) {
                    return document.getText();
                }
            }
            VirtualFile file = LocalFileSystem.getInstance().findFileByNioFile(attempt.solution());
            if (file != null) {
                Document cached = documents.getCachedDocument(file);
                if (cached != null) {
                    return cached.getText();
                }
            }
        }
        return Files.isRegularFile(attempt.solution())
                ? Files.readString(attempt.solution(), StandardCharsets.UTF_8)
                : "";
    }

    /**
     * Opens the attempt's practice module and returns its solution file. Runs in the background; the caller
     * opens the returned file in an editor on the EDT.
     */
    static VirtualFile prepare(Project project, ManagedPracticeWorkspace.Attempt attempt) throws IOException {
        try {
            PracticeModuleWorkspace.get(project).open(attempt);
        } catch (ExecutionException exception) {
            throw new IOException(exception.getMessage(), exception);
        }
        var file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        if (file == null) {
            throw new IOException("Solution file is missing: " + attempt.solution());
        }
        return file;
    }
}
