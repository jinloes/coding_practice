package com.jinloes.practice_plugin.app;

import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class LegacyImportServiceTest {
    @TempDir Path temporary;

    @Test
    void rejectsRedirectingLegacyRootWhenSymlinksAreAvailable() throws IOException {
        Path realRoot = temporary.resolve("real");
        Files.createDirectories(realRoot);
        Path link = temporary.resolve("link");
        try {
            Files.createSymbolicLink(link, realRoot);
        } catch (UnsupportedOperationException | IOException exception) {
            assumeTrue(false, "Symlink creation is unavailable: " + exception.getMessage());
        }

        assertThatThrownBy(() -> service().importLegacy(link, new PracticeProgress(), new ManagedPracticeProgress()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("must not redirect");
    }

    @Test
    void rejectsUnmarkedLegacyRoot() throws IOException {
        Path root = temporary.resolve("not-legacy");
        Files.createDirectories(root);

        assertThatThrownBy(() -> service().importLegacy(root, new PracticeProgress(), new ManagedPracticeProgress()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("not a marked legacy practice project");
    }

    @Test
    void rejectsMissingOrNonDirectoryAttemptsRoot() throws IOException {
        Path missingAttempts = legacyRoot("missing-attempts");
        assertThatThrownBy(() -> service().importLegacy(
                missingAttempts, new PracticeProgress(), new ManagedPracticeProgress()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("attempts directory must not redirect");

        Path fileAttempts = legacyRoot("file-attempts");
        Files.writeString(fileAttempts.resolve("attempts"), "not a directory");
        assertThatThrownBy(() -> service().importLegacy(
                fileAttempts, new PracticeProgress(), new ManagedPracticeProgress()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("attempts directory must not redirect");
    }

    @Test
    void rejectsAttemptsRootThatResolvesOutsideTheLegacyWorkspace() throws Exception {
        assumeTrue(System.getProperty("os.name").startsWith("Windows"), "junction test is Windows-specific");
        Path root = legacyRoot("junction-attempts");
        Path outside = temporary.resolve("outside-attempts");
        Files.createDirectories(outside);
        Process process = new ProcessBuilder(
                System.getenv("ComSpec"), "/c", "mklink", "/J",
                root.resolve("attempts").toString(), outside.toString())
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assumeTrue(process.waitFor() == 0, "Could not create junction: " + output);

        assertThatThrownBy(() -> service().importLegacy(root, new PracticeProgress(), new ManagedPracticeProgress()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("attempts directory must not redirect");
    }

    @Test
    void preservesCustomManagedLimitsAndSkipsInvalidLegacyLimits() throws IOException {
        Path root = legacyRoot("limits");
        Files.createDirectories(root.resolve("attempts"));

        PracticeProgress invalidLegacy = new PracticeProgress();
        invalidLegacy.getState().testSeconds = 0;
        invalidLegacy.getState().suiteSeconds = 90;
        invalidLegacy.getState().heapMb = 512;
        ManagedPracticeProgress defaults = new ManagedPracticeProgress();

        service().importLegacy(root, invalidLegacy, defaults);

        assertThat(defaults.hasDefaultLimits())
                .as("invalid legacy limits must not overwrite valid defaults")
                .isTrue();

        PracticeProgress validLegacy = new PracticeProgress();
        validLegacy.getState().testSeconds = 9;
        validLegacy.getState().suiteSeconds = 90;
        validLegacy.getState().heapMb = 384;
        ManagedPracticeProgress customized = new ManagedPracticeProgress();
        customized.getState().testSeconds = 12;
        customized.getState().suiteSeconds = 120;
        customized.getState().heapMb = 512;

        service().importLegacy(root, validLegacy, customized);

        assertThat(customized.getState().testSeconds).isEqualTo(12);
        assertThat(customized.getState().suiteSeconds).isEqualTo(120);
        assertThat(customized.getState().heapMb).isEqualTo(512);
    }

    private Path legacyRoot(String name) throws IOException {
        Path root = temporary.resolve(name);
        Files.createDirectories(root);
        Files.writeString(root.resolve(PracticeWorkspace.MARKER), "schema=1\n");
        return root;
    }

    private LegacyImportService service() {
        return new LegacyImportService(new ManagedPracticeWorkspace());
    }
}
