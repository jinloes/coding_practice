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
    @FunctionalInterface
    interface PathResolver {
        Path resolve(Path path) throws IOException;
    }

    private final ManagedPracticeWorkspace attempts;
    private final PathResolver realPath;

    public record LegacyImportResult(int imported, int skipped, String summary) {}

    public LegacyImportService() {
        this(ManagedPracticeWorkspace.get());
    }

    public LegacyImportService(ManagedPracticeWorkspace attempts) {
        this(attempts, Path::toRealPath);
    }

    LegacyImportService(ManagedPracticeWorkspace attempts, PathResolver realPath) {
        this.attempts = Objects.requireNonNull(attempts, "attempts");
        this.realPath = Objects.requireNonNull(realPath, "realPath");
    }

    public LegacyImportResult importLegacy(
            Path legacyRoot,
            PracticeProgress legacyProgress,
            ManagedPracticeProgress progress
    ) throws IOException {
        if (Files.isSymbolicLink(legacyRoot)) {
            throw new IOException("The legacy practice root must not redirect.");
        }
        Path root = realPath.resolve(legacyRoot);
        if (!PracticeWorkspace.isWorkspace(root)) {
            throw new IOException("The current project is not a marked legacy practice project.");
        }
        Path attemptsRoot = root.resolve("attempts");
        if (!Files.isDirectory(attemptsRoot, LinkOption.NOFOLLOW_LINKS)
                || !realPath.resolve(attemptsRoot).equals(attemptsRoot)) {
            throw new IOException("The legacy attempts directory must not redirect.");
        }

        int imported = 0;
        int skipped = 0;
        try (var entries = Files.list(attemptsRoot)) {
            for (Path entry : entries.sorted(Comparator.comparing(Path::toString)).toList()) {
                if (!Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)
                        || !Files.isRegularFile(entry.resolve(".practice-attempt"), LinkOption.NOFOLLOW_LINKS)) {
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
        var legacyState = legacyProgress.getState();
        if (progress.hasDefaultLimits() && ManagedPracticeProgress.validLimits(
                legacyState.testSeconds, legacyState.suiteSeconds, legacyState.heapMb)) {
            var state = progress.getState();
            state.testSeconds = legacyState.testSeconds;
            state.suiteSeconds = legacyState.suiteSeconds;
            state.heapMb = legacyState.heapMb;
        }
        String summary = "Imported " + imported + " legacy attempt(s); skipped " + skipped
                + " already imported or invalid entry/entries. Originals were unchanged.";
        return new LegacyImportResult(imported, skipped, summary);
    }
}
