package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.platform.OwnedDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * The per-project example-harness and module-output directories below the IDE system path.
 *
 * <p>Every directory carries a schema-1 marker naming its kind, attempt, and creating process, and
 * is only replaced or deleted when that marker proves it is an inactive artifact of the expected
 * kind and attempt. Not thread-safe: {@link PracticeModuleWorkspace} guards every call with its own
 * monitor.
 */
final class GeneratedDirectories {
    static final String HARNESSES = "harnesses";
    static final String MODULE_OUTPUT = "module-output";
    private static final String MARKER = ".algorithm-practice-generated";
    private static final String ATTEMPT_ID_PATTERN = "[a-z][a-z0-9-]*-[a-f0-9]{32}";
    private final Project project;
    private final Set<Path> ownedDirectories = new LinkedHashSet<>();

    GeneratedDirectories(Project project) {
        this.project = project;
    }

    Path base(String baseName) {
        return Path.of(PathManager.getSystemPath(), "algorithm-practice", baseName,
                Integer.toUnsignedString(project.getLocationHash().hashCode())).toAbsolutePath().normalize();
    }

    /** Creates a marked directory, first replacing a same-attempt artifact whose creator has exited. */
    void createMarked(Path directory, String kind, String attemptId) throws IOException {
        OwnedDirectory owned = owned(kind);
        owned.requireOwned(directory);
        if (!attemptId.matches(ATTEMPT_ID_PATTERN)
                || !attemptId.equals(directory.getFileName().toString())) {
            throw new IOException("Generated path does not match its managed attempt.");
        }
        if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            Marker existing = readMarker(directory);
            if (!kind.equals(existing.kind()) || !attemptId.equals(existing.attemptId())
                    || !isInactive(existing.pid())) {
                throw new IOException("Existing generated directory is not a recoverable inactive attempt artifact.");
            }
            deleteTree(directory);
        }
        owned.create(directory, markerText(kind, attemptId));
        ownedDirectories.add(directory);
    }

    /**
     * Removes marked directories under one base whose creating process has exited, and optionally
     * those this process created.
     */
    void cleanupAbandoned(String baseName, boolean includeCurrentProcess) {
        String kind = kindOf(baseName);
        OwnedDirectory.under(base(baseName), MARKER).cleanupAbandoned((candidate, properties) -> {
            Marker marker = parseMarker(candidate, properties, kind);
            return (includeCurrentProcess && marker.pid() == ProcessHandle.current().pid())
                    || isInactive(marker.pid());
        });
        ownedDirectories.removeIf(path -> !Files.exists(path, LinkOption.NOFOLLOW_LINKS));
    }

    void deleteTree(Path root) throws IOException {
        Marker marker = readMarker(root);
        owned(marker.kind()).deleteTree(root);
        ownedDirectories.remove(root);
    }

    /** Deletes every directory this instance created, preserving any whose marker no longer validates. */
    void deleteAllOwned() {
        for (Path path : Set.copyOf(ownedDirectories)) {
            try {
                deleteTree(path);
            } catch (IOException ignored) {
                // Preserve anything whose ownership cannot be revalidated.
            }
        }
        ownedDirectories.clear();
    }

    private static String markerText(String kind, String attemptId) {
        return """
                schema=1
                kind=%s
                attempt=%s
                pid=%s
                """.formatted(kind, attemptId, ProcessHandle.current().pid());
    }

    private Marker readMarker(Path root) throws IOException {
        Path parent = root.toAbsolutePath().normalize().getParent();
        String baseName;
        if (base(HARNESSES).equals(parent)) {
            baseName = HARNESSES;
        } else if (base(MODULE_OUTPUT).equals(parent)) {
            baseName = MODULE_OUTPUT;
        } else {
            throw new IOException("Refusing to modify an unmarked generated directory.");
        }
        OwnedDirectory owned = OwnedDirectory.under(base(baseName), MARKER);
        return parseMarker(root, owned.readMarker(root), kindOf(baseName));
    }

    private static Marker parseMarker(Path root, Properties properties, String expectedKind) throws IOException {
        String kind = properties.getProperty("kind", "");
        String attemptId = properties.getProperty("attempt", "");
        String pidText = properties.getProperty("pid", "");
        if (!"1".equals(properties.getProperty("schema"))
                || !expectedKind.equals(kind)
                || !attemptId.matches(ATTEMPT_ID_PATTERN)
                || !attemptId.equals(root.getFileName().toString())
                || !pidText.matches("[1-9][0-9]*")) {
            throw new IOException("Invalid generated-directory marker.");
        }
        try {
            return new Marker(kind, attemptId, Long.parseLong(pidText));
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid generated-directory marker.", exception);
        }
    }

    private OwnedDirectory owned(String kind) throws IOException {
        return OwnedDirectory.under(base(switch (kind) {
            case "harness" -> HARNESSES;
            case "output" -> MODULE_OUTPUT;
            default -> throw new IOException("Unknown generated-directory kind.");
        }), MARKER);
    }

    private static String kindOf(String baseName) {
        return HARNESSES.equals(baseName) ? "harness" : "output";
    }

    private static boolean isInactive(long pid) {
        return ProcessHandle.of(pid).map(handle -> !handle.isAlive()).orElse(true);
    }

    private record Marker(String kind, String attemptId, long pid) {}
}
