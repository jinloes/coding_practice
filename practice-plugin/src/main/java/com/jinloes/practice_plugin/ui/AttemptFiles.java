package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

import java.nio.file.Path;

/** Matches editor documents to the solution file of a managed attempt. */
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
}
