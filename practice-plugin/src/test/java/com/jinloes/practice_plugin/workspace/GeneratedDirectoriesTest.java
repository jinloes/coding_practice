package com.jinloes.practice_plugin.workspace;

import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratedDirectoriesTest {
    private static final String MARKER = ".algorithm-practice-generated";
    private static final String ATTEMPT = "pair-sum-0123456789abcdef0123456789abcdef";

    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private GeneratedDirectories generated;

    @BeforeEach
    void setUp() throws Exception {
        fixture = EdtTestUtil.runInEdtAndGet(() -> {
            IdeaProjectTestFixture created = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("generated-directories", temporary.resolve("host"), false).getFixture();
            created.setUp();
            return created;
        });
        generated = new GeneratedDirectories(fixture.getProject());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (generated != null) {
            generated.deleteAllOwned();
        }
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }

    @Test
    void rejectsGeneratedDirectoriesWhoseNameDoesNotMatchTheAttempt() {
        Path wrongName = generated.base(GeneratedDirectories.HARNESSES)
                .resolve("pair-sum-fedcba9876543210fedcba9876543210");

        assertThatThrownBy(() -> generated.createMarked(wrongName, "harness", ATTEMPT))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("does not match");
    }

    @Test
    void refusesToReplaceExistingArtifactsThatDoNotMatchTheExpectedInactiveAttempt() throws Exception {
        Path directory = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        writeMarker(directory, "harness", ATTEMPT, Long.toString(ProcessHandle.current().pid()));

        assertThatThrownBy(() -> generated.createMarked(directory, "harness", ATTEMPT))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("not a recoverable");
        assertThat(directory).isDirectory();
    }


    @Test
    void rejectsAttemptIdsThatAreNotManagedAttemptIdsEvenWhenTheDirectoryNameMatches() throws Exception {
        Path invalid = generated.base(GeneratedDirectories.HARNESSES).resolve("not-an-attempt");

        assertThatThrownBy(() -> generated.createMarked(invalid, "harness", "not-an-attempt"))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("does not match");
    }

    @Test
    void refusesToReplaceExistingArtifactsWithTheWrongKindEvenWhenInactive() throws Exception {
        Path directory = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        writeMarker(directory, "output", ATTEMPT, Long.toString(deadPid()));

        assertThatThrownBy(() -> generated.createMarked(directory, "harness", ATTEMPT))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid generated-directory marker");
        assertThat(directory).isDirectory();
    }

    @Test
    void replacesArtifactsWhoseCreatorProcessHandleNoLongerExists() throws Exception {
        Path directory = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        writeMarker(directory, "harness", ATTEMPT, Long.toString(deadPid()));
        Files.writeString(directory.resolve("stale"), "old");

        generated.createMarked(directory, "harness", ATTEMPT);

        assertThat(directory.resolve("stale")).doesNotExist();
        assertThat(directory.resolve(MARKER)).content().contains("pid=" + ProcessHandle.current().pid());
    }

    @Test
    void cleanupDropsDeletedCurrentProcessArtifactsFromTheOwnedSet() throws Exception {
        Path current = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        generated.createMarked(current, "harness", ATTEMPT);

        generated.cleanupAbandoned(GeneratedDirectories.HARNESSES, true);

        assertThat(current).doesNotExist();
        generated.deleteAllOwned();
        assertThat(current).doesNotExist();
    }

    @Test
    void deleteAllOwnedPreservesDirectoriesWhoseMarkerNoLongerValidates() throws Exception {
        Path directory = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        generated.createMarked(directory, "harness", ATTEMPT);
        Files.writeString(directory.resolve(MARKER), "schema=2\nkind=harness\nattempt=" + ATTEMPT + "\npid=1\n");

        generated.deleteAllOwned();

        assertThat(directory).isDirectory();
    }

    @Test
    void rejectsTreesOutsideKnownGeneratedBasesAndUnknownKinds() throws Exception {
        Path outside = temporary.resolve(ATTEMPT);
        Files.createDirectories(outside);

        assertThatThrownBy(() -> generated.deleteTree(outside)).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> generated.createMarked(outside, "scratch", ATTEMPT))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unknown generated-directory kind");
    }

    @Test
    void rejectsInvalidMarkersAndOversizedProcessIds() throws Exception {
        Path invalid = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        writeMarker(invalid, "harness", ATTEMPT, "1");
        Files.writeString(invalid.resolve(MARKER), "schema=2\nkind=harness\nattempt=" + ATTEMPT + "\npid=1\n");
        assertThatThrownBy(() -> generated.deleteTree(invalid)).isInstanceOf(Exception.class);

        Path oversized = generated.base(GeneratedDirectories.MODULE_OUTPUT).resolve(ATTEMPT);
        writeMarker(oversized, "output", ATTEMPT, "999999999999999999999999999999");
        assertThatThrownBy(() -> generated.deleteTree(oversized)).isInstanceOf(Exception.class);
    }

    @Test
    void cleanupCanEitherPreserveOrRemoveCurrentProcessArtifacts() throws Exception {
        Path preserve = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        writeMarker(preserve, "harness", ATTEMPT, Long.toString(ProcessHandle.current().pid()));

        generated.cleanupAbandoned(GeneratedDirectories.HARNESSES, false);

        assertThat(preserve).isDirectory();

        generated.cleanupAbandoned(GeneratedDirectories.HARNESSES, true);

        assertThat(preserve).doesNotExist();
    }


    @Test
    void cleanupPreservesArtifactsWhileTheirCreatorProcessIsAlive() throws Exception {
        Process active = sleepingProcess();
        Path activeDirectory = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        try {
            writeMarker(activeDirectory, "harness", ATTEMPT, Long.toString(active.pid()));

            generated.cleanupAbandoned(GeneratedDirectories.HARNESSES, false);

            assertThat(activeDirectory).isDirectory();
        } finally {
            active.destroyForcibly();
            active.waitFor();
        }
    }

    @Test
    void cleanupWithCurrentProcessIncludedStillRemovesOtherDeadProcesses() throws Exception {
        Path dead = generated.base(GeneratedDirectories.HARNESSES).resolve(ATTEMPT);
        writeMarker(dead, "harness", ATTEMPT, Long.toString(deadPid()));

        generated.cleanupAbandoned(GeneratedDirectories.HARNESSES, true);

        assertThat(dead).doesNotExist();
    }

    private static void writeMarker(Path directory, String kind, String attemptId, String pid) throws Exception {
        Files.createDirectories(directory);
        Files.writeString(directory.resolve(MARKER), """
                schema=1
                kind=%s
                attempt=%s
                pid=%s
                """.formatted(kind, attemptId, pid));
    }

    private static Process sleepingProcess() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive",
                    "-Command", "Start-Sleep -Seconds 30").start();
        }
        return new ProcessBuilder("sleep", "30").start();
    }

    private static long deadPid() {
        long candidate = 999_999_999L;
        while (ProcessHandle.of(candidate).isPresent()) {
            candidate--;
        }
        return candidate;
    }
}
