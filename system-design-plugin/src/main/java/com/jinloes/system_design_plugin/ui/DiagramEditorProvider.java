package com.jinloes.system_design_plugin.ui;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefApp;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace;
import org.jetbrains.annotations.NotNull;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/** Opens an attempt's {@code design.excalidraw} in the Excalidraw editor; every other file is ignored. */
public final class DiagramEditorProvider implements FileEditorProvider, DumbAware {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if (!file.isInLocalFileSystem() || !DesignWorkspace.DIAGRAM.equals(file.getName())) {
            return false;
        }
        try {
            Path path = file.toNioPath();
            return DesignWorkspace.get().ownerOfDiagram(path).isPresent() && JBCefApp.isSupported();
        } catch (UnsupportedOperationException | InvalidPathException notLocal) {
            return false;
        }
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new DiagramEditor(project, file);
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "system-design-diagram";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
