package com.jinloes.practice_plugin.integration;

import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeConfigurationType;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.ui.PracticeToolWindowFactory;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import org.jdom.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PluginIntegrationTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUp() throws Exception {
        host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("algorithm-practice", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @Test
    void pluginLoadsAndManagedServicesResolve() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            assertThat(PluginManagerCore.getPlugin(PluginId.getId("com.jinloes.practice"))).isNotNull();
            assertThat(ManagedPracticeProgress.get()).isNotNull();
            assertThat(PracticeModuleWorkspace.get(fixture.getProject())).isNotNull();
            assertThat(fixture.getProject().getService(PracticeRunner.class).isRunning()).isFalse();
        });
    }

    @Test
    void checkConfigurationRoundTripsAndRejectsUnsafeAttemptIds() throws Exception {
        var attempt = new ManagedPracticeWorkspace().create(ExerciseCatalog.find("pair-sum"));
        EdtTestUtil.runInEdtAndWait(() -> {
            var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
            var settings = RunManager.getInstance(fixture.getProject())
                    .createConfiguration("Check attempt", type.getConfigurationFactories()[0]);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = attempt.id();
            Element serialized = new Element("configuration");
            configuration.writeExternal(serialized);
            var restored = (PracticeRunConfiguration) type.getConfigurationFactories()[0]
                    .createTemplateConfiguration(fixture.getProject());
            restored.readExternal(serialized);
            assertThat(restored.attemptId).isEqualTo(configuration.attemptId);
            assertThat(ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, restored)).isNotNull();
            restored.attemptId = "../outside";
            assertThatThrownBy(restored::checkConfiguration).isInstanceOf(RuntimeConfigurationError.class);
        });
    }

    @Test
    void toolWindowCreatesManagedUiWithoutMutatingHostProjectMetadata() throws Exception {
        Set<String> before = Files.list(host).map(path -> path.getFileName().toString())
                .collect(java.util.stream.Collectors.toSet());
        var originalSdk = ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk();
        EdtTestUtil.runInEdtAndWait(() -> {
            var manager = ToolWindowManager.getInstance(fixture.getProject());
            var window = manager.registerToolWindow("Practice integration", true, ToolWindowAnchor.RIGHT);
            try {
                new PracticeToolWindowFactory().createToolWindowContent(fixture.getProject(), window);
                assertThat(window.getContentManager().getContentCount()).isEqualTo(1);
                assertThat(window.getContentManager().getContent(0).getComponent())
                        .isInstanceOf(javax.swing.JPanel.class);
            } finally {
                manager.unregisterToolWindow("Practice integration");
            }
        });
        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isSameAs(originalSdk);
        assertThat(Files.list(host).map(path -> path.getFileName().toString())
                .collect(java.util.stream.Collectors.toSet())).isEqualTo(before);
    }

    @Test
    void openingManagedAttemptUsesCurrentProjectAndLeavesDurableFileOutsideHost() throws Exception {
        var attempt = new ManagedPracticeWorkspace().create(ExerciseCatalog.find("binary-search"));
        var file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(file).isNotNull();
        EdtTestUtil.runInEdtAndWait(() -> {
            PracticeModuleWorkspace.get(fixture.getProject()).open(attempt);
            assertThat(FileEditorManager.getInstance(fixture.getProject()).openFile(file, true)).isNotEmpty();
        });
        assertThat(attempt.solution()).isRegularFile();
        assertThat(attempt.solution()).isNotIn(host);
        assertThat(host.resolve("attempts")).doesNotExist();
        assertThat(host.resolve("build.gradle")).doesNotExist();
        assertThat(host.resolve(".idea/modules.xml")).doesNotExist();
    }

    @Test
    void legacyImportIsIdempotentAndSafelySkipsInvalidEntries() throws Exception {
        Path legacyRoot = temporary.resolve("legacy");
        PracticeWorkspace.create(legacyRoot);
        var legacy = PracticeWorkspace.createAttempt(legacyRoot, ExerciseCatalog.find("pair-sum"));
        Files.writeString(legacy.solution(), "learner-owned legacy solution");
        PracticeProgress legacyProgress = PracticeProgress.get(fixture.getProject());
        var legacyEntry = legacyProgress.entry(legacy.id(), legacy.exerciseId());
        legacyEntry.status = "PASSED";
        legacyEntry.checkedFingerprint = "old-fingerprint";
        legacyEntry.checkedAt = "2025-01-02T03:04:05Z";
        legacyEntry.lastPassedAt = "2025-01-02T03:04:05Z";
        legacyEntry.details = "historical pass";
        legacyEntry.hintsRevealed = 2;
        legacyEntry.tests = 11;
        legacyEntry.failures = 0;
        legacyProgress.getState().selectedAttempts.put(legacy.exerciseId(), legacy.id());
        legacyProgress.getState().testSeconds = 9;
        legacyProgress.getState().suiteSeconds = 90;
        legacyProgress.getState().heapMb = 384;
        addInvalidLegacyEntries(legacyRoot);
        String originalsBefore = treeFingerprint(legacyRoot);

        ManagedPracticeWorkspace managed = new ManagedPracticeWorkspace();
        ManagedPracticeProgress progress = ManagedPracticeProgress.get();
        int managedBefore = managed.attempts("pair-sum").size();

        var first = managed.importLegacy(legacyRoot, legacyProgress, progress);
        var second = managed.importLegacy(legacyRoot, legacyProgress, progress);

        String managedId = progress.importedAttempt(legacyRoot.toRealPath().toString(), legacy.id());
        assertThat(first.imported()).isEqualTo(1);
        assertThat(first.skipped()).isGreaterThanOrEqualTo(5);
        assertThat(first.summary()).contains("Originals were unchanged");
        assertThat(second.imported()).isZero();
        assertThat(second.skipped()).isGreaterThan(first.skipped());
        assertThat(managed.attempts("pair-sum")).hasSize(managedBefore + 1);
        assertThat(managed.read(managedId).solution()).hasContent("learner-owned legacy solution");
        assertThat(progress.importedAttempt(legacyRoot.toRealPath().toString(), legacy.id()))
                .isEqualTo(managedId);
        var migrated = progress.entry(managedId, "pair-sum");
        assertThat(migrated.status).isEqualTo("NOT_RUN");
        assertThat(migrated.checkedFingerprint).isEmpty();
        assertThat(migrated.checkedAt).isEmpty();
        assertThat(migrated.lastPassedAt).isEqualTo(legacyEntry.lastPassedAt);
        assertThat(migrated.hintsRevealed).isEqualTo(2);
        assertThat(migrated.tests).isZero();
        assertThat(migrated.failures).isZero();
        assertThat(progress.getState().selectedAttempts).containsEntry("pair-sum", managedId);
        assertThat(progress.getState().testSeconds).isEqualTo(9);
        assertThat(progress.getState().suiteSeconds).isEqualTo(90);
        assertThat(progress.getState().heapMb).isEqualTo(384);
        assertThat(treeFingerprint(legacyRoot)).isEqualTo(originalsBefore);
    }

    private void addInvalidLegacyEntries(Path legacyRoot) throws Exception {
        Path attempts = legacyRoot.resolve("attempts");
        Files.createDirectories(attempts.resolve("foreign-entry"));
        Files.writeString(attempts.resolve("foreign-entry/keep"), "foreign");

        Path malformed = attempts.resolve("malformed-entry");
        Files.createDirectories(malformed);
        Files.writeString(malformed.resolve(".practice-attempt"), "exercise=not-real\nrevision=1\n");

        Path missing = attempts.resolve("missing-entry");
        Files.createDirectories(missing);
        Files.writeString(missing.resolve(".practice-attempt"), "exercise=pair-sum\nrevision=1\n");

        Path outside = temporary.resolve("outside-entry");
        Files.createDirectories(outside);
        Files.writeString(outside.resolve(".practice-attempt"), "exercise=pair-sum\nrevision=1\n");
        Files.createSymbolicLink(attempts.resolve("redirecting-entry"), outside);

        Path redirectingSolution = attempts.resolve("redirecting-solution");
        Files.createDirectories(redirectingSolution.resolve("src/main/java/com/jinloes/practice"));
        Files.writeString(redirectingSolution.resolve(".practice-attempt"),
                "exercise=pair-sum\nrevision=1\n");
        Path outsideSolution = temporary.resolve("outside-Solution.java");
        Files.writeString(outsideSolution, "outside");
        Files.createSymbolicLink(
                redirectingSolution.resolve("src/main/java/com/jinloes/practice/Solution.java"),
                outsideSolution);
    }

    private static String treeFingerprint(Path root) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted().toList()) {
                digest.update(root.relativize(path).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                if (Files.isSymbolicLink(path)) {
                    digest.update(Files.readSymbolicLink(path).toString()
                            .getBytes(java.nio.charset.StandardCharsets.UTF_8));
                } else if (Files.isRegularFile(path)) {
                    digest.update(Files.readAllBytes(path));
                }
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
