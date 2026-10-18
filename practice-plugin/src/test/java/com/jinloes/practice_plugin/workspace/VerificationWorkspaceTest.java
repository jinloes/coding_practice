package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationWorkspaceTest {
    private static final String MIRROR_REPOSITORY =
            "maven { url = uri('https://maven-central.storage-download.googleapis.com/maven2/') }";

    @TempDir
    Path temporary;

    @Test
    void createsAnOwnedIsolatedProjectWithFreshReportsAndASharedGradleHome() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);
        Path system = temporary.resolve("system");

        VerificationWorkspace workspace = VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint);

        assertThat(workspace.root().resolve(".algorithm-practice-verification")).isRegularFile();
        assertThat(workspace.root().resolve("src/main/java/com/jinloes/practice/Solution.java")).hasContent(
                Files.readString(attempt.solution()));
        assertThat(workspace.root().resolve("src/test/java/com/jinloes/practice/ExamplesTest.java")).isRegularFile();
        assertThat(workspace.root().resolve("src/test/java/com/jinloes/practice/CorrectnessTest.java")).isRegularFile();
        String buildScript = Files.readString(workspace.root().resolve("build.gradle"));
        assertThat(buildScript)
                .as("the packaged script resource is fully substituted before a learner ever runs Gradle")
                .doesNotContainPattern("@[A-Z_]+@");
        assertThat(buildScript)
                .contains("options.release = 17", "reports.junitXml.outputLocation",
                        "org.junit:junit-bom:5.11.4", "org.assertj:assertj-core:3.26.3");
        assertMirrorBeforeCentral(buildScript);
        assertThat(workspace.command(Path.of(System.getProperty("java.home")), 5, 256))
                .contains("--rerun-tasks", "--no-build-cache", "--no-configuration-cache",
                        "--gradle-user-home", workspace.gradleHome().toString());
        assertThat(workspace.gradleHome())
                .as("the Gradle home lives beside, not inside, the per-attempt workspace root")
                .isEqualTo(system.resolve("algorithm-practice/gradle-home"))
                .isNotEqualTo(workspace.root().resolve("gradle-home"));

        Files.createDirectories(workspace.gradleHome());
        Files.writeString(workspace.gradleHome().resolve("caches-.marker"), "downloaded distribution");

        workspace.markFinished(fingerprint);
        workspace.cleanupAfterRun();
        assertThat(workspace.root()).doesNotExist();
        assertThat(workspace.gradleHome().resolve("caches-.marker"))
                .as("a later check must reuse this run's downloaded Gradle distribution and dependency caches")
                .hasContent("downloaded distribution");

        VerificationWorkspace later = VerificationWorkspace.createAt(
                system, attempt, ExerciseCatalog.find("pair-sum"), fingerprint);
        assertThat(later.gradleHome())
                .as("every verification workspace under the same base shares one Gradle home")
                .isEqualTo(workspace.gradleHome());
        later.markFinished(fingerprint);
        later.cleanupAfterRun();
    }

    @Test
    void repositoryOrderCheckRejectsReversedOrMissingEntries() {
        assertThatThrownBy(() -> assertMirrorBeforeCentral("""
                repositories {
                    mavenCentral()
                    %s
                }
                """.formatted(MIRROR_REPOSITORY)))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertMirrorBeforeCentral("""
                repositories {
                    mavenCentral()
                }
                """))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> assertMirrorBeforeCentral("""
                repositories {
                    %s
                }
                """.formatted(MIRROR_REPOSITORY)))
                .isInstanceOf(AssertionError.class);
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
    void packagedBuildScriptLoaderUsesTheDefaultResourcePath() throws Exception {
        assertThat(VerificationWorkspace.buildScript())
                .contains("plugins", "mavenCentral()")
                .doesNotContainPattern("@[A-Z_]+@");
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
    void missingPackagedResourcesAndUnsubstitutedBuildPlaceholdersFailCreation() throws Exception {
        ManagedPracticeWorkspace store = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = store.create(ExerciseCatalog.find("pair-sum"));
        String fingerprint = store.fingerprint(attempt);

        assertThatThrownBy(() -> VerificationWorkspace.createAt(
                temporary.resolve("missing-bootstrap"), attempt, ExerciseCatalog.find("pair-sum"), fingerprint,
                path -> path.equals("/bootstrap/gradlew.bat") ? null : resource(path)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Gradle bootstrap resource");

        assertThatThrownBy(() -> VerificationWorkspace.createAt(
                temporary.resolve("missing-build-script"), attempt, ExerciseCatalog.find("pair-sum"), fingerprint,
                path -> path.equals("/gradle/verification-build.gradle") ? null : resource(path)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("verification script resource");

        assertThatThrownBy(() -> VerificationWorkspace.createAt(
                temporary.resolve("placeholder"), attempt, ExerciseCatalog.find("pair-sum"), fingerprint,
                path -> path.equals("/gradle/verification-build.gradle")
                        ? bytes("plugins { id '@UNREPLACED@' }\n") : resource(path)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("unreplaced placeholders");

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

    private static void assertMirrorBeforeCentral(String buildScript) {
        int mirrorIndex = buildScript.indexOf(MIRROR_REPOSITORY);
        int centralIndex = buildScript.indexOf("mavenCentral()");

        assertThat(mirrorIndex).as("exact Google Maven Central mirror entry").isGreaterThanOrEqualTo(0);
        assertThat(centralIndex).as("mavenCentral() fallback after the Google mirror").isGreaterThan(mirrorIndex);
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

    private static InputStream bytes(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }
}
