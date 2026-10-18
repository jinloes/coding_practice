package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PracticeModuleWorkspaceTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private ManagedPracticeWorkspace attempts;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("managed-modules", host, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null));
        });
        attempts = new ManagedPracticeWorkspace(temporary.resolve("config"));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @Test
    void createsDistinctNonPersistentJava17ModulesWithoutChangingHostSdkOrFiles() throws Exception {
        var first = attempts.create(ExerciseCatalog.find("pair-sum"));
        var second = attempts.create(ExerciseCatalog.find("binary-search"));
        Path host = Path.of(fixture.getProject().getBasePath());
        var before = Files.list(host).map(Path::getFileName).toList();

        var firstModule = EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(first));
        var secondModule = EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(second));
        var reopenedFirstModule = EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(first));

        assertThat(firstModule).isNotSameAs(secondModule);
        assertThat(reopenedFirstModule).isSameAs(firstModule);
        assertThat(ModuleManager.getInstance(fixture.getProject()).getModules())
                .contains(firstModule, secondModule);
        assertThat(ModuleRootManager.getInstance(firstModule).getSdk()).isNotNull();
        assertThat(EdtTestUtil.runInEdtAndGet(() -> {
            var model = ModuleRootManager.getInstance(firstModule).getModifiableModel();
            try {
                return model.getModuleExtension(LanguageLevelModuleExtension.class).getLanguageLevel();
            } finally {
                model.dispose();
            }
        })).isEqualTo(LanguageLevel.JDK_17);
        assertThat(ModuleRootManager.getInstance(firstModule).getContentRoots())
                .extracting(file -> Path.of(file.getPath()))
                .containsExactly(first.directory());
        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isNull();
        assertThat(Files.list(host).map(Path::getFileName).toList()).containsExactlyElementsOf(before);
    }

    @Test
    void generatedRunnerIsAttachedThenRemovedAndUnmarkedSiblingSurvivesCleanup() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path runner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));
        Path harness = runner.getParent().getParent().getParent().getParent().getParent();
        Path foreign = harness.getParent().resolve("unmarked-sibling-" + System.nanoTime());
        Files.createDirectories(foreign);
        Files.writeString(foreign.resolve("keep"), "learner-owned");
        try {
            assertThat(runner).isRegularFile();
            assertThat(runner).content().contains("class ExampleRunner", "static void main");
            assertThat(ModuleRootManager.getInstance(support.open(attempt)).getContentRoots())
                    .extracting(file -> Path.of(file.getPath())).contains(harness);

            EdtTestUtil.runInEdtAndWait(() -> support.clearExampleHarness(attempt));
            assertThat(harness).doesNotExist();
            assertThat(foreign.resolve("keep")).hasContent("learner-owned");
        } finally {
            Files.deleteIfExists(foreign.resolve("keep"));
            Files.deleteIfExists(foreign);
        }
    }

    @Test
    void preparingAHarnessAgainReplacesThePreviousMarkedHarness() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path firstRunner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));
        Path harness = firstRunner.getParent().getParent().getParent().getParent().getParent();
        Files.writeString(harness.resolve("stale"), "old generated file");

        Path secondRunner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));

        assertThat(secondRunner).isEqualTo(firstRunner);
        assertThat(harness.resolve("stale")).doesNotExist();
        assertThat(secondRunner).content().contains("class ExampleRunner");
    }

    @Test
    void clearExampleHarnessPreservesAHarnessWhoseMarkerNoLongerValidates() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path runner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));
        Path harness = runner.getParent().getParent().getParent().getParent().getParent();
        Files.writeString(harness.resolve(".algorithm-practice-generated"),
                "schema=2\nkind=harness\nattempt=" + attempt.id() + "\npid=1\n");

        EdtTestUtil.runInEdtAndWait(() -> support.clearExampleHarness(attempt));

        assertThat(harness).isDirectory();
    }

    @Test
    void clearingAnUnpreparedHarnessIsANoOp() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));

        EdtTestUtil.runInEdtAndWait(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).clearExampleHarness(attempt));

        assertThat(generatedBase("harnesses").resolve(attempt.id())).doesNotExist();
    }

    @Test
    void prepareExampleHarnessReportsExistingHarnessWhoseOwnershipCannotBeRevalidated() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path runner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));
        Path harness = runner.getParent().getParent().getParent().getParent().getParent();
        Files.writeString(harness.resolve(".algorithm-practice-generated"),
                "schema=2\nkind=harness\nattempt=" + attempt.id() + "\npid=1\n");

        assertThatThrownBy(() -> EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId()))))
                .hasMessageContaining("Cannot prepare the generated example runner");
    }

    @Test
    void prefersAUsableHostSdkButNeverSetsOneWhenMissing() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var hostSdk = JavaSdk.getInstance().createJdk(
                "Managed practice host JDK", System.getProperty("java.home"), false);
        EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() -> {
            ProjectJdkTable.getInstance().addJdk(hostSdk, fixture.getTestRootDisposable());
            ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(hostSdk);
        }));

        var module = EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(attempt));

        assertThat(ModuleRootManager.getInstance(module).getSdk()).isSameAs(hostSdk);
        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isSameAs(hostSdk);
    }

    @Test
    void usesARegisteredJdkWhenTheProjectHasNoSdk() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var tableSdk = JavaSdk.getInstance().createJdk(
                "Managed practice table JDK", System.getProperty("java.home"), false);
        EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() ->
                ProjectJdkTable.getInstance().addJdk(tableSdk, fixture.getTestRootDisposable())));

        var module = EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(attempt));

        assertThat(ModuleRootManager.getInstance(module).getSdk()).isSameAs(tableSdk);
        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isNull();
    }

    @Test
    void javaHomeSelectsAJdkOnDemandAndTemporaryConfigurationTrackingIsIdempotent() throws Exception {
        PracticeModuleWorkspace support = PracticeModuleWorkspace.get(fixture.getProject());

        assertThat(support.javaHome()).isDirectory();
        support.track(null);
        support.untrack(null);
        assertThat(support.cleanupGeneratedArtifacts()).isTrue();
    }

    @Test
    void openFailsWithoutRecreatingMissingManagedRoots() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        deleteTree(attempt.directory());

        assertThatThrownBy(() -> EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(attempt)))
                .hasMessageContaining("Cannot prepare the managed Java module");
        assertThat(attempt.directory()).doesNotExist();
    }

    @Test
    void recoversDeadSameAttemptOutputBeforeWritingFreshMarker() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        Path output = generatedBase("module-output").resolve(attempt.id());
        long deadPid = deadPid();
        writeMarker(output, "output", attempt.id(), Long.toString(deadPid));
        Files.writeString(output.resolve("stale.class"), "stale");

        EdtTestUtil.runInEdtAndGet(() ->
                PracticeModuleWorkspace.get(fixture.getProject()).open(attempt));

        assertThat(output.resolve("stale.class")).doesNotExist();
        assertThat(output.resolve(".algorithm-practice-generated")).content()
                .contains("kind=output", "attempt=" + attempt.id(),
                        "pid=" + ProcessHandle.current().pid());
    }

    @Test
    void cleanupPreservesWrongMalformedActiveUncertainUnmarkedAndRedirectingArtifacts() throws Exception {
        Path harnesses = generatedBase("harnesses");
        Files.createDirectories(harnesses);
        String wrongKindId = attemptId(1);
        String malformedAttemptId = attemptId(2);
        String malformedPidId = attemptId(3);
        String activeId = attemptId(4);
        String uncertainId = attemptId(5);
        Path wrongKind = writeMarker(harnesses.resolve(wrongKindId), "output", wrongKindId,
                Long.toString(deadPid()));
        Path malformedAttempt = writeMarker(harnesses.resolve(malformedAttemptId), "harness", "../outside",
                Long.toString(deadPid()));
        Path malformedPid = writeMarker(harnesses.resolve(malformedPidId), "harness", malformedPidId, "12x");
        Path uncertain = writeMarker(harnesses.resolve(uncertainId), "harness", attemptId(6),
                Long.toString(deadPid()));
        Path unmarked = harnesses.resolve("unmarked");
        Files.createDirectories(unmarked);
        Files.writeString(unmarked.resolve("keep"), "keep");
        Path redirectTarget = temporary.resolve("redirect-target");
        Files.createDirectories(redirectTarget);
        Files.writeString(redirectTarget.resolve("keep"), "keep");
        Path redirect = harnesses.resolve("redirect");
        Files.createSymbolicLink(redirect, redirectTarget);
        Process activeProcess = sleepingProcess();
        Path active = writeMarker(harnesses.resolve(activeId), "harness", activeId,
                Long.toString(activeProcess.pid()));
        try {
            assertThat(PracticeModuleWorkspace.get(fixture.getProject()).cleanupGeneratedArtifacts()).isTrue();

            assertThat(wrongKind).isDirectory();
            assertThat(malformedAttempt).isDirectory();
            assertThat(malformedPid).isDirectory();
            assertThat(active).isDirectory();
            assertThat(uncertain).isDirectory();
            assertThat(unmarked.resolve("keep")).hasContent("keep");
            assertThat(redirect).isSymbolicLink();
            assertThat(redirectTarget.resolve("keep")).hasContent("keep");
        } finally {
            activeProcess.destroyForcibly();
            activeProcess.waitFor();
        }
    }

    @Test
    void cleanupRemovesOnlyDeadOwnedFallbackSdksWithParseableNames() throws Exception {
        String prefix = "Algorithm Practice IDE JDK ";
        String malformedName = prefix + "not-a-pid";
        String noSeparatorName = prefix + deadPid();
        String staleName = prefix + deadPid() + "-module-workspace";
        Sdk malformed = JavaSdk.getInstance().createJdk(malformedName, System.getProperty("java.home"), false);
        Sdk noSeparator = JavaSdk.getInstance().createJdk(noSeparatorName, System.getProperty("java.home"), false);
        Sdk stale = JavaSdk.getInstance().createJdk(staleName, System.getProperty("java.home"), false);
        try {
            EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() -> {
                ProjectJdkTable.getInstance().addJdk(malformed);
                ProjectJdkTable.getInstance().addJdk(noSeparator);
                ProjectJdkTable.getInstance().addJdk(stale);
            }));

            assertThat(PracticeModuleWorkspace.get(fixture.getProject()).cleanupGeneratedArtifacts()).isTrue();

            assertThat(ProjectJdkTable.getInstance().findJdk(malformedName)).isSameAs(malformed);
            assertThat(ProjectJdkTable.getInstance().findJdk(noSeparatorName)).isSameAs(noSeparator);
            assertThat(ProjectJdkTable.getInstance().findJdk(staleName)).isNull();
        } finally {
            removeJdk(malformedName);
            removeJdk(noSeparatorName);
            removeJdk(staleName);
        }
    }

    @Test
    void cleanupDisposesOpenModules() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        var module = EdtTestUtil.runInEdtAndGet(() -> support.open(attempt));

        assertThat(support.cleanupGeneratedArtifacts()).isTrue();

        assertThat(module.isDisposed()).isTrue();
        assertThat(ModuleManager.getInstance(fixture.getProject()).getModules()).doesNotContain(module);
    }

    private Path generatedBase(String kind) {
        return Path.of(PathManager.getSystemPath(), "algorithm-practice", kind,
                Integer.toUnsignedString(fixture.getProject().getLocationHash().hashCode()))
                .toAbsolutePath().normalize();
    }

    private static Path writeMarker(Path directory, String kind, String attemptId, String pid) throws Exception {
        Files.createDirectories(directory);
        Files.writeString(directory.resolve(".algorithm-practice-generated"), """
                schema=1
                kind=%s
                attempt=%s
                pid=%s
                """.formatted(kind, attemptId, pid));
        return directory;
    }

    private static String attemptId(int suffix) {
        return "pair-sum-" + "%032x".formatted(suffix);
    }

    private static long deadPid() {
        long candidate = 999_999_999L;
        while (ProcessHandle.of(candidate).isPresent()) {
            candidate--;
        }
        return candidate;
    }

    private static void deleteTree(Path root) throws Exception {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private static void removeJdk(String name) throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            Sdk remaining = ProjectJdkTable.getInstance().findJdk(name);
            if (remaining != null) {
                WriteAction.run(() -> ProjectJdkTable.getInstance().removeJdk(remaining));
            }
        });
    }

    private static Process sleepingProcess() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive",
                    "-Command", "Start-Sleep -Seconds 30").start();
        }
        return new ProcessBuilder("sleep", "30").start();
    }
}
