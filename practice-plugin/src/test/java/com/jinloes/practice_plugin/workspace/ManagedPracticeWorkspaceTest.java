package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManagedPracticeWorkspaceTest {
    @TempDir Path temporary;

    @Test
    void pinsTheGeneratedSolutionPathEveryStoredAttemptDependsOn() {
        assertThat(ManagedPracticeWorkspace.SOLUTION_PATH)
                .as("existing attempt directories on disk use this exact path")
                .isEqualTo("src/main/java/com/jinloes/practice/Solution.java");
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
    void fingerprintsSolutionAndBundledChecks() throws Exception {
        ManagedPracticeWorkspace workspace = new ManagedPracticeWorkspace(temporary.resolve("config"));
        var attempt = workspace.create(ExerciseCatalog.find("balanced-delimiters"));
        String initial = workspace.fingerprint(attempt);
        Files.writeString(attempt.solution(), Files.readString(attempt.solution()) + "\n// saved edit\n");
        assertThat(workspace.fingerprint(attempt)).isNotEqualTo(initial);
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
}
