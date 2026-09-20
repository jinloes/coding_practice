package com.jinloes.practice_plugin.platform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Properties;

/**
 * The plugin-owned marker/delete protocol for generated directory trees.
 *
 * <p>Every plugin-generated tree lives as a direct child of one owned base directory and carries a
 * marker file naming the owning process. Deleting a tree is irreversible for a learner, so this
 * class refuses to act whenever ownership cannot be positively re-established: an unmarked,
 * malformed, unreadable, symlinked, or out-of-base path is always preserved rather than removed.
 *
 * <p>The checks below are the strictest union of the per-workspace implementations this class
 * replaced:
 * <ul>
 *   <li>the directory must be a <em>direct</em> child of the owned base, never a deeper descendant;
 *   <li>the directory's parent must resolve through symlinks to the owned base itself;
 *   <li>neither the directory nor its marker may be a symbolic link, and both are inspected with
 *       {@link LinkOption#NOFOLLOW_LINKS};
 *   <li>any {@code IOException} while establishing ownership preserves the directory.
 * </ul>
 *
 * <p>Marker <em>content</em> stays with each caller: this class writes the bytes it is given and
 * returns the parsed properties, so marker field names and schema values are unchanged.
 */
public final class OwnedDirectory {
    private final Path ownedBase;
    private final String markerName;

    private OwnedDirectory(Path ownedBase, String markerName) {
        this.ownedBase = ownedBase;
        this.markerName = markerName;
    }

    /**
     * @param ownedBase directory whose direct children this instance owns
     * @param markerName marker file name written into each owned child
     */
    public static OwnedDirectory under(Path ownedBase, String markerName) {
        return new OwnedDirectory(ownedBase.toAbsolutePath().normalize(), markerName);
    }

    /** Decides whether an owned directory with this marker may be deleted. */
    @FunctionalInterface
    public interface MarkerFilter {
        /**
         * @param directory the owned directory being considered, so callers can check invariants
         *     carried by its name
         * @throws IOException when ownership cannot be confirmed, which preserves the directory
         */
        boolean deletable(Path directory, Properties marker) throws IOException;
    }

    public Path base() {
        return ownedBase;
    }

    public Path resolve(String name) {
        return ownedBase.resolve(name).normalize();
    }

    /**
     * Creates an owned directory and writes its marker. Nothing is deleted here: an existing
     * directory fails because its marker cannot be created anew.
     */
    public void create(Path directory, String markerText) throws IOException {
        requireOwned(directory);
        Files.createDirectories(directory);
        Files.writeString(directory.resolve(markerName), markerText, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW);
    }

    /**
     * Checks only that {@code directory} is a non-symlinked direct child of the owned base. This is
     * a lexical check so it works before the directory or the base exists.
     */
    public void requireOwned(Path directory) throws IOException {
        Path normalized = directory.toAbsolutePath().normalize();
        if (!ownedBase.equals(normalized.getParent()) || Files.isSymbolicLink(normalized)) {
            throw new IOException("Generated path escapes the plugin-owned root.");
        }
    }

    /**
     * Re-establishes ownership of an existing directory and returns its marker properties.
     *
     * @throws IOException when the directory is not a marked, plugin-owned, non-symlinked direct
     *     child of the owned base, or when the marker cannot be read
     */
    public Properties readMarker(Path directory) throws IOException {
        requireOwned(directory);
        Path normalized = directory.toAbsolutePath().normalize();
        Path marker = normalized.resolve(markerName);
        if (!Files.isDirectory(normalized, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(normalized)
                || !Files.isRegularFile(marker, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(marker)
                || !normalized.getParent().toRealPath().equals(ownedBase.toRealPath())) {
            throw new IOException("Refusing to modify an unmarked plugin-owned directory.");
        }
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(marker, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    /**
     * Deletes an owned tree after re-establishing ownership, deepest entries first.
     *
     * @throws IOException when ownership cannot be re-established, leaving the tree untouched
     */
    public void deleteTree(Path directory) throws IOException {
        readMarker(directory);
        Path normalized = directory.toAbsolutePath().normalize();
        try (var paths = Files.walk(normalized)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    /**
     * Deletes every owned child the filter accepts. Candidates whose ownership or marker cannot be
     * established are preserved for later recovery, and a failure on one candidate never stops the
     * others.
     */
    public void cleanupAbandoned(MarkerFilter filter) {
        if (!Files.isDirectory(ownedBase, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(ownedBase)) {
            return;
        }
        try (var entries = Files.list(ownedBase)) {
            for (Path candidate : entries.toList()) {
                try {
                    if (filter.deletable(candidate, readMarker(candidate))) {
                        deleteTree(candidate);
                    }
                } catch (IOException | SecurityException ignored) {
                    // Unmarked, malformed, active, and uncertain paths remain untouched.
                }
            }
        } catch (IOException | SecurityException ignored) {
            // A later explicit cleanup can retry without risking unverified paths.
        }
    }
}
