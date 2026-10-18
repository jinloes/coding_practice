package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManagedPracticeWorkspaceTest {
    @TempDir Path temporary;

    @Test
    void pinsTheGeneratedSolutionPathEveryStoredAttemptDependsOn() {
        assertThat(ManagedPracticeWorkspace.SOLUTION_PATH)
                .as("existing attempt directories on disk use this exact path")
                .isEqualTo("src/main/java/com/jinloes/practice/Solution.java");
        assertThat(new ManagedPracticeWorkspace(temporary.resolve("config")).attemptsDirectory().getFileName())
                .hasToString("attempts");
    }

    @Test
    void createsDistinctDurableAttemptsWithoutOverwritingEarlierFiles() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var first = workspace.create(ExerciseCatalog.find("pair-sum"));
        var second = workspace.create(ExerciseCatalog.find("pair-sum"));
        Files.writeString(first.solution(), "custom solution");

        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(first.solution()).isRegularFile();
        assertThat(first.solution()).isEqualTo(first.directory()
                .resolve("src/main/java/com/jinloes/practice/Solution.java"));
        assertThat(first.solution()).startsWith(temporary.resolve("config/algorithm-practice/attempts"));
        assertThat(Files.readString(second.solution())).contains("Implement findPair")
                .doesNotContain("static void main", "@Test");
        assertThat(workspace.attempts("pair-sum")).extracting(ManagedPracticeWorkspace.Attempt::id)
                .containsExactlyInAnyOrder(first.id(), second.id());
        assertThat(workspace.read(first.id())).isEqualTo(first);
    }

    @Test
    void reportsWhenUuidCollisionsExhaustAttemptCreation() throws Exception {
        UUID fixed = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(
                temporary.resolve("config"), () -> fixed);

        var first = workspace.create(ExerciseCatalog.find("pair-sum"));

        assertThat(first.id()).isEqualTo("pair-sum-0123456789abcdef0123456789abcdef");
        assertThatThrownBy(() -> workspace.create(ExerciseCatalog.find("pair-sum")))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("unique managed attempt");
    }

    @Test
    void missingForeignAndRedirectingFilesAreNeverRecreatedOrAdopted() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = workspace.create(ExerciseCatalog.find("binary-search"));
        Files.delete(attempt.solution());
        Path foreign = workspace.attemptsDirectory().resolve("binary-search/not-an-attempt");
        Files.createDirectories(foreign);
        Files.writeString(foreign.resolve("Solution.java"), "foreign");

        assertThatThrownBy(() -> workspace.read(attempt.id())).isInstanceOf(IOException.class);
        assertThat(workspace.attempts("binary-search")).isEmpty();
        assertThat(foreign.resolve("Solution.java")).hasContent("foreign");

        Path redirected = workspace.attemptsDirectory().resolve(
                "binary-search/binary-search-0123456789abcdef0123456789abcdef");
        Files.createDirectories(redirected);
        Files.createSymbolicLink(redirected.resolve("src"), temporary);
        assertThatThrownBy(() -> workspace.read("binary-search-0123456789abcdef0123456789abcdef"))
                .isInstanceOf(IOException.class);
    }

    @Test
    void missingAttemptsDirectoryHasNoAttemptsAndInvalidIdsAreRejected() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThat(workspace.attempts("pair-sum")).isEmpty();
        assertThatThrownBy(() -> workspace.read("../pair-sum"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid managed attempt ID");
    }

    @Test
    void rejectsLegacyCopiesFromUnmarkedRootsEvenWhenTheSolutionIsInsideThem() throws Exception {
        Path legacyRoot = temporary.resolve("unmarked-legacy");
        Path attemptRoot = legacyRoot.resolve("attempt");
        Path solution = attemptRoot.resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
        Files.createDirectories(solution.getParent());
        Files.writeString(solution, "unmarked");
        var legacy = new PracticeWorkspace.Attempt("unmarked", "pair-sum", "1", attemptRoot);
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThatThrownBy(() -> workspace.copyLegacy(legacyRoot, legacy))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("legacy solution");
    }

    @Test
    void attemptsIgnoreDirectoryEntriesThatResolveToAnotherExercise() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        String binaryId = "binary-search-0123456789abcdef0123456789abcdef";
        Path binarySolution = workspace.attemptsDirectory().resolve("binary-search")
                .resolve(binaryId).resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
        Files.createDirectories(binarySolution.getParent());
        Files.writeString(binarySolution, "binary");
        Files.createDirectories(workspace.attemptsDirectory().resolve("pair-sum").resolve(binaryId));

        assertThat(workspace.attempts("pair-sum")).isEmpty();
    }

    @Test
    void attemptsIgnoreRegularFilesInAnExerciseAttemptDirectory() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        Path exerciseDirectory = workspace.attemptsDirectory().resolve("pair-sum");
        Files.createDirectories(exerciseDirectory);
        Files.writeString(exerciseDirectory.resolve("not-a-directory"), "ignore me");

        assertThat(workspace.attempts("pair-sum")).isEmpty();
    }

    @Test
    void readRejectsNullAttemptIds() {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThatThrownBy(() -> workspace.read(null))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid managed attempt ID: null");
    }

    @Test
    void attemptsIgnoreDirectoriesWhoseIdBelongsToAnotherExercise() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        String binaryId = "binary-search-0123456789abcdef0123456789abcdef";
        Path misplacedSolution = workspace.attemptsDirectory().resolve("pair-sum")
                .resolve(binaryId).resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
        Files.createDirectories(misplacedSolution.getParent());
        Files.writeString(misplacedSolution, "misplaced");

        assertThat(workspace.attempts("pair-sum")).isEmpty();
    }

    @Test
    void readRejectsManagedSolutionsRedirectedOutsideStorageByAParentDirectory() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        String id = "pair-sum-0123456789abcdef0123456789abcdef";
        Path outside = temporary.resolve("outside-root").resolve(id);
        Path outsideSolution = outside.resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
        Files.createDirectories(outsideSolution.getParent());
        Files.writeString(outsideSolution, "redirected");
        Path exerciseDirectory = workspace.attemptsDirectory().resolve("pair-sum");
        Files.createDirectories(exerciseDirectory);
        Files.createSymbolicLink(exerciseDirectory.resolve(id), outside);

        assertThatThrownBy(() -> workspace.read(id))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("inside the practice storage root");
    }

    @Test
    void rejectsLegacyCopiesWhoseSolutionPathIsASymbolicLinkInsideTheLegacyWorkspace() throws Exception {
        Path legacyRoot = temporary.resolve("legacy-symlink");
        LegacyWorkspaceFixture.create(legacyRoot);
        var legacy = LegacyWorkspaceFixture.createAttempt(legacyRoot, ExerciseCatalog.find("pair-sum"));
        Path target = legacy.solution().resolveSibling("RealSolution.java");
        Files.writeString(target, "real solution");
        Files.delete(legacy.solution());
        Files.createSymbolicLink(legacy.solution(), target.getFileName());
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThatThrownBy(() -> workspace.copyLegacy(legacyRoot, legacy))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("legacy solution");
    }

    @Test
    void rejectsLegacyCopiesWhoseSolutionRealPathIsADirectoryInsideTheLegacyWorkspace() throws Exception {
        Path legacyRoot = temporary.resolve("legacy-directory");
        LegacyWorkspaceFixture.create(legacyRoot);
        var legacy = LegacyWorkspaceFixture.createAttempt(legacyRoot, ExerciseCatalog.find("pair-sum"));
        Files.delete(legacy.solution());
        Files.createDirectory(legacy.solution());
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThatThrownBy(() -> workspace.copyLegacy(legacyRoot, legacy))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("legacy solution");
    }

    @Test
    void fingerprintsSolutionAndBundledChecks() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = workspace.create(ExerciseCatalog.find("balanced-delimiters"));
        String initial = workspace.fingerprint(attempt);
        Files.writeString(attempt.solution(), Files.readString(attempt.solution()) + "\n// saved edit\n");
        assertThat(workspace.fingerprint(attempt)).isNotEqualTo(initial);
    }

    @Test
    void fingerprintRejectsAttemptRecordsThatDoNotMatchManagedStorage() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = workspace.create(ExerciseCatalog.find("balanced-delimiters"));
        var wrongPath = new ManagedPracticeWorkspace.Attempt(
                attempt.id(), attempt.exerciseId(), attempt.revision(), temporary.resolve("Solution.java"));
        var wrongExercise = new ManagedPracticeWorkspace.Attempt(
                attempt.id(), "pair-sum", attempt.revision(), attempt.solution());

        assertThatThrownBy(() -> workspace.fingerprint(wrongPath)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> workspace.fingerprint(wrongExercise)).isInstanceOf(IOException.class);
    }

    @Test
    void copiesAValidatedLegacyAttemptWithoutChangingOriginals() throws Exception {
        Path legacyRoot = temporary.resolve("legacy");
        LegacyWorkspaceFixture.create(legacyRoot);
        var legacy = LegacyWorkspaceFixture.createAttempt(legacyRoot, ExerciseCatalog.find("pair-sum"));
        Files.writeString(legacy.solution(), "legacy solution");
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        var copy = workspace.copyLegacy(legacyRoot, legacy);

        assertThat(copy.solution()).hasContent("legacy solution");
        assertThat(legacy.solution()).hasContent("legacy solution");
        assertThat(copy.id()).isNotEqualTo(legacy.id());
    }

    @Test
    void rejectsLegacyCopiesWhenTheLegacySolutionDoesNotValidate() throws Exception {
        Path legacyRoot = temporary.resolve("legacy");
        LegacyWorkspaceFixture.create(legacyRoot);
        var legacy = LegacyWorkspaceFixture.createAttempt(legacyRoot, ExerciseCatalog.find("pair-sum"));
        Files.delete(legacy.solution());
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThatThrownBy(() -> workspace.copyLegacy(legacyRoot, legacy))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsLegacyCopiesWhoseSolutionIsOutsideTheMarkedLegacyWorkspace() throws Exception {
        Path legacyRoot = temporary.resolve("legacy");
        LegacyWorkspaceFixture.create(legacyRoot);
        Path outsideAttempt = temporary.resolve("outside-attempt");
        Files.createDirectories(outsideAttempt.resolve("src/main/java/com/jinloes/practice"));
        Files.writeString(outsideAttempt.resolve(ManagedPracticeWorkspace.SOLUTION_PATH), "outside");
        var legacy = new PracticeWorkspace.Attempt("outside", "pair-sum", "1", outsideAttempt);
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));

        assertThatThrownBy(() -> workspace.copyLegacy(legacyRoot, legacy))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("legacy solution");
    }
}
