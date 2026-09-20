package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
}
