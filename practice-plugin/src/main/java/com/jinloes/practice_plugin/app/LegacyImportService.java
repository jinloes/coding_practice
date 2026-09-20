package com.jinloes.practice_plugin.app;

import com.jinloes.practice_plugin.run.CheckResult;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

/**
 * Migrates a learner from the legacy on-disk practice project into managed attempt storage.
 *
 * <p>This is a use case, not storage: it reads through {@link PracticeWorkspace}, writes through
 * {@link ManagedPracticeWorkspace}, and transfers progress entries. Keeping it here is what lets
 * the {@code workspace} package stay free of {@code run} and {@code state} dependencies.
 */
public final class LegacyImportService {
    private final ManagedPracticeWorkspace attempts;

    public record LegacyImportResult(int imported, int skipped, String summary) {}

    public LegacyImportService() {
        this(ManagedPracticeWorkspace.get());
    }

    public LegacyImportService(ManagedPracticeWorkspace attempts) {
        this.attempts = Objects.requireNonNull(attempts, "attempts");
    }

    public LegacyImportResult importLegacy(
            Path legacyRoot,
            PracticeProgress legacyProgress,
            ManagedPracticeProgress progress
    ) throws IOException {
        if (Files.isSymbolicLink(legacyRoot)) {
            throw new IOException("The legacy practice root must not redirect.");
        }
        Path root = legacyRoot.toRealPath();
        if (!PracticeWorkspace.isWorkspace(root)) {
            throw new IOException("The current project is not a marked legacy practice project.");
        }
        Path attemptsRoot = root.resolve("attempts");
        if (!Files.isDirectory(attemptsRoot, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(attemptsRoot)
                || !attemptsRoot.toRealPath().equals(root.resolve("attempts"))) {
            throw new IOException("The legacy attempts directory must not redirect.");
        }

        int imported = 0;
        int skipped = 0;
        try (var entries = Files.list(attemptsRoot)) {
            for (Path entry : entries.sorted(Comparator.comparing(Path::toString)).toList()) {
                if (!Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)
                        || Files.isSymbolicLink(entry)
                        || !Files.isRegularFile(entry.resolve(".practice-attempt"), LinkOption.NOFOLLOW_LINKS)
                        || Files.isSymbolicLink(entry.resolve(".practice-attempt"))) {
                    skipped++;
                    continue;
                }
                try {
                    PracticeWorkspace.Attempt legacy =
                            PracticeWorkspace.readAttempt(root, entry.getFileName().toString());
                    String mapped = progress.importedAttempt(root.toString(), legacy.id());
                    if (mapped != null) {
                        skipped++;
                        continue;
                    }
                    ManagedPracticeWorkspace.Attempt copy = attempts.copyLegacy(root, legacy);
                    progress.recordImport(root.toString(), legacy.id(), copy.id());
                    PracticeProgress.Entry old = legacyProgress.entry(legacy.id(), legacy.exerciseId());
                    ManagedPracticeProgress.Entry migrated = progress.entry(copy.id(), legacy.exerciseId());
                    migrated.status = CheckResult.Status.NOT_RUN.name();
                    migrated.checkedFingerprint = "";
                    migrated.checkedAt = "";
                    migrated.lastPassedAt = old.lastPassedAt;
                    migrated.details = "Imported from legacy attempt " + legacy.id()
                            + "; run a new full check for a current result.";
                    migrated.hintsRevealed = old.hintsRevealed;
                    migrated.tests = 0;
                    migrated.failures = 0;
                    if (legacy.id().equals(
                            legacyProgress.getState().selectedAttempts.get(legacy.exerciseId()))) {
                        progress.getState().selectedAttempts.put(legacy.exerciseId(), copy.id());
                    }
                    imported++;
                } catch (IOException | IllegalArgumentException exception) {
                    skipped++;
                }
            }
        }
        progress.getState().testSeconds = legacyProgress.getState().testSeconds;
        progress.getState().suiteSeconds = legacyProgress.getState().suiteSeconds;
        progress.getState().heapMb = legacyProgress.getState().heapMb;
        String summary = "Imported " + imported + " legacy attempt(s); skipped " + skipped
                + " already imported or invalid entry/entries. Originals were unchanged.";
        return new LegacyImportResult(imported, skipped, summary);
    }
}
