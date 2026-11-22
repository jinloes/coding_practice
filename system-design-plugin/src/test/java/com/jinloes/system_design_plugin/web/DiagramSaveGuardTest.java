package com.jinloes.system_design_plugin.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiagramSaveGuardTest {
    @TempDir Path temporary;

    @Test
    void competingEditorsPreserveTheSuccessfulSceneAndRejectTheStaleWriter() throws IOException {
        Path diagram = temporary.resolve("design.excalidraw");
        byte[] original = "{}".getBytes(StandardCharsets.UTF_8);
        Files.write(diagram, original);
        DiagramSaveGuard first = new DiagramSaveGuard(original);
        DiagramSaveGuard second = new DiagramSaveGuard(original);
        byte[] successful = "{\"elements\":[]}".getBytes(StandardCharsets.UTF_8);
        save(first, diagram, successful);

        assertThatThrownBy(() -> save(second, diagram, "{} ".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(IOException.class).hasMessageContaining("Copy your unsaved scene before reopening");
        assertThat(Files.readAllBytes(diagram)).isEqualTo(successful);

        byte[] next = "{\"elements\":[],\"files\":{}}".getBytes(StandardCharsets.UTF_8);
        save(first, diagram, next);
        assertThat(Files.readAllBytes(diagram)).isEqualTo(next);
    }

    @Test
    void baselinesOwnTheirBytesAndOnlySuccessfulWritesAdvanceThem() throws IOException {
        byte[] loaded = {1, 2};
        DiagramSaveGuard guard = new DiagramSaveGuard(loaded);
        loaded[0] = 9;
        guard.requireUnchanged(new byte[]{1, 2});
        byte[] replacement = {3, 4};
        assertThatThrownBy(() -> {
            guard.requireUnchanged(new byte[]{1, 2});
            Files.write(temporary, replacement);
            guard.recordSaved(replacement);
        }).isInstanceOf(IOException.class);
        guard.requireUnchanged(new byte[]{1, 2});
        assertThatThrownBy(() -> guard.requireUnchanged(replacement)).isInstanceOf(IOException.class);

        guard.recordSaved(replacement);
        replacement[0] = 9;
        guard.requireUnchanged(new byte[]{3, 4});
        assertThatThrownBy(() -> guard.requireUnchanged(new byte[]{1, 2})).isInstanceOf(IOException.class);
    }

    private static void save(DiagramSaveGuard guard, Path diagram, byte[] scene) throws IOException {
        guard.requireUnchanged(Files.readAllBytes(diagram));
        Files.write(diagram, scene);
        guard.recordSaved(scene);
    }
}
