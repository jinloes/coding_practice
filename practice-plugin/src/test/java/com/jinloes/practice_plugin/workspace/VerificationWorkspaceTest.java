package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExampleLibraries;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationWorkspaceTest {
    @TempDir
    Path temporary;

    @Test
    void createsAnOwnedOfflineWorkspaceWithBundledLibrariesAndDirectJavaCommand() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);
        Path system = temporary.resolve("system");
        Path javaHome = Path.of(System.getProperty("java.home"));

        VerificationWorkspace workspace = VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint);

        assertThat(workspace.root().resolve(".algorithm-practice-verification")).isRegularFile();
        assertThat(workspace.root().resolve("src/main/java/com/jinloes/practice/Solution.java"))
                .hasContent(Files.readString(attempt.solution()));
        assertThat(workspace.root().resolve("src/main/java/com/jinloes/practice/Workload.java")).isRegularFile();
        assertThat(workspace.root().resolve("src/main/java/com/jinloes/practice/ComplexityProbe.java")).isRegularFile();
        assertThat(workspace.root().resolve("src/test/java/com/jinloes/practice/ExamplesTest.java")).isRegularFile();
        assertThat(workspace.root().resolve("src/test/java/com/jinloes/practice/CorrectnessTest.java")).isRegularFile();
        assertThat(workspace.root().resolve("libs")).isDirectory();
        assertThat(workspace.root().resolve("libs/verification-runner.jar")).isRegularFile();
        for (String name : ExampleLibraries.names()) {
            assertThat(workspace.root().resolve("libs").resolve(name)).isRegularFile();
        }
        for (String name : ExampleLibraries.verificationNames()) {
            assertThat(workspace.root().resolve("libs").resolve(name)).isRegularFile();
        }

        List<String> command = workspace.command(javaHome, 5, 256);
        assertThat(command).contains(
                "-Xmx128m",
                "com.jinloes.practice_plugin.runner.VerificationMain",
                javaHome.toString(),
                "5",
                "256");
        assertThat(command).doesNotContain("org.gradle.wrapper.GradleWrapperMain");

        workspace.markFinished(fingerprint);
        workspace.cleanupAfterRun();
        assertThat(workspace.root()).doesNotExist();
    }

    @Test
    void recoveryDeletesOnlyMarkedInactiveWorkspaces() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);
        Path system = temporary.resolve("system");
        VerificationWorkspace dead = VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint);
        dead.markProcess(Long.MAX_VALUE, fingerprint);
        VerificationWorkspace active = VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint);
        active.markProcess(ProcessHandle.current().pid(), fingerprint);
        VerificationWorkspace created = VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint);
        Path foreign = system.resolve("algorithm-practice/verification/foreign");
        Files.createDirectories(foreign);
        Files.writeString(foreign.resolve("keep"), "not plugin owned");
        Path learnerScratch = temporary.resolve("learner-scratch/Solution.java");
        Files.createDirectories(learnerScratch.getParent());
        Files.writeString(learnerScratch, "learner work");

        VerificationWorkspace.cleanupAbandoned(system);

        assertThat(dead.root()).doesNotExist();
        assertThat(active.root()).exists();
        assertThat(created.root()).exists();
        assertThat(foreign.resolve("keep")).hasContent("not plugin owned");
        assertThat(learnerScratch).hasContent("learner work");

        active.markFinished(fingerprint);
        active.cleanupAfterRun();
        created.markFinished(fingerprint);
        created.cleanupAfterRun();
    }

    @Test
    void creationFailureCleansUpThePartiallyWrittenWorkspace() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);
        Files.delete(attempt.solution());
        Path system = temporary.resolve("system");

        assertThatThrownBy(() -> VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint))
                .isInstanceOf(Exception.class);

        Path verification = system.resolve("algorithm-practice/verification");
        if (Files.isDirectory(verification)) {
            try (var entries = Files.list(verification)) {
                assertThat(entries.toList()).isEmpty();
            }
        }
    }

    @Test
    void refusesToCleanUpWorkspacesThatHaveNotFinished() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);
        VerificationWorkspace workspace = VerificationWorkspace.createAt(
                temporary.resolve("system"), attempt, ExerciseCatalog.find("pair-sum"), fingerprint);

        assertThat(workspace.resultDirectory()).isEqualTo(workspace.root().resolve(".practice-results"));
        assertThatThrownBy(workspace::cleanupAfterRun)
                .isInstanceOf(Exception.class)
                .hasMessageContaining("still active");

        workspace.markFinished(fingerprint);
        workspace.cleanupAfterRun();
    }

    @Test
    void defaultFactoryCreatesAWorkspaceUnderTheIdeSystemPath() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);

        VerificationWorkspace workspace = VerificationWorkspace.create(
                attempt, ExerciseCatalog.find("pair-sum"), fingerprint);

        assertThat(workspace.root()).isDirectory();
        workspace.markFinished(fingerprint);
        workspace.cleanupAfterRun();
    }

    @Test
    void defaultCleanupAbandonedRemovesFinishedWorkspacesUnderTheIdeSystemPath() throws Exception {
        Path verification = Path.of(PathManager.getSystemPath(), "algorithm-practice", "verification");
        Path finished = verification.resolve("finished-" + System.nanoTime());
        writeVerificationMarker(finished, "schema=1\nstate=finished\npid=0\nfingerprint=f\n");

        VerificationWorkspace.cleanupAbandoned();

        assertThat(finished).doesNotExist();
    }

    @Test
    void malformedRecoveryMarkersArePreservedForManualInspection() throws Exception {
        Path system = temporary.resolve("system");
        Path verification = system.resolve("algorithm-practice/verification");
        Path badSchema = verification.resolve("bad-schema");
        Path badPid = verification.resolve("bad-pid");
        Path badState = verification.resolve("bad-state");
        Path blankFingerprint = verification.resolve("blank-fingerprint");
        writeVerificationMarker(badSchema, "schema=2\nstate=finished\npid=-1\nfingerprint=f\n");
        writeVerificationMarker(badPid, "schema=1\nstate=active\npid=abc\nfingerprint=f\n");
        writeVerificationMarker(badState, "schema=1\nstate=paused\npid=-1\nfingerprint=f\n");
        writeVerificationMarker(blankFingerprint, "schema=1\nstate=finished\npid=0\nfingerprint=   \n");

        VerificationWorkspace.cleanupAbandoned(system);

        assertThat(badSchema).isDirectory();
        assertThat(badPid).isDirectory();
        assertThat(badState).isDirectory();
        assertThat(blankFingerprint).isDirectory();
    }

    @Test
    void recoveryTreatsNonPositiveAndAbsentProcessHandlesAsInactive() throws Exception {
        Path system = temporary.resolve("system");
        Path verification = system.resolve("algorithm-practice/verification");
        Path nonPositive = verification.resolve("non-positive");
        Path absentHandle = verification.resolve("absent-handle");
        writeVerificationMarker(nonPositive, "schema=1\nstate=active\npid=0\nfingerprint=f\n");
        writeVerificationMarker(absentHandle, "schema=1\nstate=active\npid=" + deadPid() + "\nfingerprint=f\n");

        VerificationWorkspace.cleanupAbandoned(system);

        assertThat(nonPositive).doesNotExist();
        assertThat(absentHandle).doesNotExist();
    }

    @Test
    void missingPackagedVerificationResourcesAndLibrariesFailCreation() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);

        assertThatThrownBy(() -> VerificationWorkspace.createAt(
                temporary.resolve("missing-verification-libs"), attempt, ExerciseCatalog.find("pair-sum"), fingerprint,
                path -> path.equals("/verification-libraries/verification-runner.jar") ? null : resource(path)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("verification library");

        assertThatThrownBy(() -> VerificationWorkspace.createAt(
                temporary.resolve("missing-exercise-resource"), attempt, ExerciseCatalog.find("pair-sum"), fingerprint,
                path -> path.endsWith("/Workload.java") ? null : resource(path)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("verification resource");
    }

    @Test
    void userFacingCleanupRemovesOnlyDeadPluginOwnedFallbackSdks() throws Exception {
        IdeaProjectTestFixture fixture = EdtTestUtil.runInEdtAndGet(() -> {
            IdeaProjectTestFixture created = IdeaTestFixtureFactory.getFixtureFactory()
                    .createLightFixtureBuilder("workspace-cleanup").getFixture();
            created.setUp();
            return created;
        });
        String sdkName = "Algorithm Practice IDE JDK " + Long.MAX_VALUE + "-workspace-cleanup";
        Sdk staleSdk = JavaSdk.getInstance().createJdk(sdkName, System.getProperty("java.home"), false);
        try {
            EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() ->
                    ProjectJdkTable.getInstance().addJdk(staleSdk)));
            assertThat(ProjectJdkTable.getInstance().findJdk(sdkName)).isSameAs(staleSdk);

            assertThat(PracticeModuleWorkspace.get(fixture.getProject()).cleanupGeneratedArtifacts()).isTrue();

            assertThat(ProjectJdkTable.getInstance().findJdk(sdkName)).isNull();
        } finally {
            EdtTestUtil.runInEdtAndWait(() -> {
                Sdk remaining = ProjectJdkTable.getInstance().findJdk(sdkName);
                if (remaining != null) {
                    WriteAction.run(() -> ProjectJdkTable.getInstance().removeJdk(remaining));
                }
                fixture.tearDown();
            });
        }
    }

    private static void writeVerificationMarker(Path directory, String marker) throws Exception {
        Files.createDirectories(directory);
        Files.writeString(directory.resolve(".algorithm-practice-verification"), marker);
    }

    private static InputStream resource(String path) {
        return VerificationWorkspace.class.getResourceAsStream(path);
    }

    private static long deadPid() {
        long candidate = 999_999_999L;
        while (ProcessHandle.of(candidate).isPresent()) {
            candidate--;
        }
        return candidate;
    }
}
