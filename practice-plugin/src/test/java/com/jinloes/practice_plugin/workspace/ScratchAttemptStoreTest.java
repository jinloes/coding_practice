package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScratchAttemptStoreTest {
    @TempDir
    Path temporary;

    @Test
    void createsDistinctRecoverableAttemptsWithoutOverwritingEarlierFiles() throws Exception {
        ScratchAttemptStore store = new ScratchAttemptStore(temporary.resolve("scratches"));
        var first = store.create(ExerciseCatalog.find("pair-sum"));
        var second = store.create(ExerciseCatalog.find("pair-sum"));
        Files.writeString(first.solution(), "custom solution");

        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(first.solution()).isRegularFile();
        assertThat(first.solution().startsWith(temporary.resolve("scratches/Algorithm Practice"))).isTrue();
        assertThat(Files.readString(second.solution())).contains("Implement findPair");
        assertThat(store.attempts("pair-sum")).extracting(ScratchAttemptStore.Attempt::id)
                .containsExactlyInAnyOrder(first.id(), second.id());
        assertThat(store.read(first.id())).isEqualTo(first);
    }

    @Test
    void missingOrForeignFilesAreNeverRecreatedOrAdopted() throws Exception {
        ScratchAttemptStore store = new ScratchAttemptStore(temporary.resolve("scratches"));
        var attempt = store.create(ExerciseCatalog.find("binary-search"));
        Files.delete(attempt.solution());
        Path foreign = temporary.resolve("scratches/Algorithm Practice/binary-search/not-an-attempt");
        Files.createDirectories(foreign);
        Files.writeString(foreign.resolve("Solution.java"), "foreign");

        assertThatThrownBy(() -> store.read(attempt.id())).isInstanceOf(IOException.class);
        assertThat(store.attempts("binary-search")).isEmpty();
        assertThat(attempt.solution()).doesNotExist();
        assertThat(foreign.resolve("Solution.java")).hasContent("foreign");
    }

    @Test
    void fingerprintsSavedScratchAndBundledRevisionInputs() throws Exception {
        ScratchAttemptStore store = new ScratchAttemptStore(temporary.resolve("scratches"));
        var attempt = store.create(ExerciseCatalog.find("balanced-delimiters"));
        String initial = store.fingerprint(attempt);

        Files.writeString(attempt.solution(), Files.readString(attempt.solution()) + "\n// saved edit\n");

        assertThat(store.fingerprint(attempt)).isNotEqualTo(initial);
    }

    @Test
    void copyingALegacyAttemptLeavesTheLegacyFileUntouched() throws Exception {
        Path legacyRoot = temporary.resolve("legacy");
        PracticeWorkspace.create(legacyRoot);
        var legacy = PracticeWorkspace.createAttempt(legacyRoot, ExerciseCatalog.find("pair-sum"));
        Files.writeString(legacy.solution(), "legacy solution");
        ScratchAttemptStore store = new ScratchAttemptStore(temporary.resolve("scratches"));

        var copy = store.copyLegacy(legacyRoot, legacy);

        assertThat(Files.readString(copy.solution())).isEqualTo("legacy solution");
        assertThat(Files.readString(legacy.solution())).isEqualTo("legacy solution");
        assertThat(copy.id()).isNotEqualTo(legacy.id());
    }
}
