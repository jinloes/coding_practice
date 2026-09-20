package com.jinloes.practice_plugin.platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Guards the five ownership scenarios the superseded per-workspace implementations agreed on. Each
 * assertion states the decision the stricter original made, because widening what this class
 * deletes is irreversible for a learner.
 */
class OwnedDirectoryTest {
    private static final String MARKER = ".algorithm-practice-owned";

    @TempDir
    Path tempDir;

    @Test
    void createsAndReadsBackAnOwnedDirectory() throws Exception {
        OwnedDirectory owned = base("owned");
        Path directory = owned.resolve("child");

        owned.create(directory, "schema=1\npid=42\n");

        assertThat(owned.readMarker(directory))
                .as("marker content is returned verbatim to the caller")
                .containsEntry("schema", "1")
                .containsEntry("pid", "42");
        assertThat(directory.resolve(MARKER)).isRegularFile();
    }

    @Test
    void refusesToCreateTwiceOverAnExistingOwnedDirectory() throws Exception {
        OwnedDirectory owned = base("owned");
        Path directory = owned.resolve("child");
        owned.create(directory, "schema=1\npid=42\n");

        assertThatThrownBy(() -> owned.create(directory, "schema=1\npid=43\n"))
                .as("creation never silently replaces an existing tree")
                .isInstanceOf(IOException.class);
        assertThat(owned.readMarker(directory)).containsEntry("pid", "42");
    }

    @Test
    void preservesASymlinkedRoot() throws Exception {
        OwnedDirectory owned = base("owned");
        Path outside = tempDir.resolve("outside");
        Files.createDirectories(outside);
        Files.writeString(outside.resolve(MARKER), "schema=1\npid=1\n", StandardCharsets.UTF_8);
        Path link = owned.resolve("linked");
        Files.createDirectories(owned.base());
        Files.createSymbolicLink(link, outside);

        assertThatThrownBy(() -> owned.readMarker(link))
                .as("a symlinked owned directory is never treated as owned")
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> owned.deleteTree(link)).isInstanceOf(IOException.class);
        owned.cleanupAbandoned((candidate, marker) -> true);

        assertThat(link).as("the symlink is preserved").exists();
        assertThat(outside.resolve(MARKER)).as("the symlink target is preserved").isRegularFile();
    }

    @Test
    void preservesAPathThatEscapesTheOwnedBase() throws Exception {
        OwnedDirectory owned = base("owned");
        Path escape = owned.base().resolve("../sibling/child");
        Path sibling = tempDir.resolve("sibling/child");
        Files.createDirectories(sibling);
        Files.writeString(sibling.resolve(MARKER), "schema=1\npid=1\n", StandardCharsets.UTF_8);

        assertThatThrownBy(() -> owned.requireOwned(escape))
                .as("`..` never resolves back into the owned base")
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> owned.readMarker(escape)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> owned.deleteTree(escape)).isInstanceOf(IOException.class);

        assertThat(sibling.resolve(MARKER)).as("the escaped directory is preserved").isRegularFile();
    }

    @Test
    void preservesADeeperDescendantOfTheOwnedBase() throws Exception {
        OwnedDirectory owned = base("owned");
        Path nested = owned.resolve("child").resolve("grandchild");
        Files.createDirectories(nested);
        Files.writeString(nested.resolve(MARKER), "schema=1\npid=1\n", StandardCharsets.UTF_8);

        assertThatThrownBy(() -> owned.deleteTree(nested))
                .as("only direct children of the owned base are owned")
                .isInstanceOf(IOException.class);

        assertThat(nested.resolve(MARKER)).isRegularFile();
    }

    @Test
    void preservesADirectoryClaimedByALiveProcess() throws Exception {
        OwnedDirectory owned = base("owned");
        Path directory = owned.resolve("live");
        long livePid = ProcessHandle.current().pid();
        owned.create(directory, "schema=1\npid=" + livePid + "\n");
        Files.writeString(directory.resolve("payload"), "learner data", StandardCharsets.UTF_8);

        owned.cleanupAbandoned((candidate, marker) -> isDead(marker.getProperty("pid")));

        assertThat(directory).as("a live owner's directory is preserved").isDirectory();
        assertThat(directory.resolve("payload")).hasContent("learner data");
    }

    @Test
    void deletesADirectoryClaimedByADeadProcess() throws Exception {
        OwnedDirectory owned = base("owned");
        Path directory = owned.resolve("dead");
        owned.create(directory, "schema=1\npid=" + deadPid() + "\n");
        Files.createDirectories(directory.resolve("nested/deeper"));
        Files.writeString(directory.resolve("nested/deeper/file"), "generated", StandardCharsets.UTF_8);

        owned.cleanupAbandoned((candidate, marker) -> isDead(marker.getProperty("pid")));

        assertThat(directory).as("an abandoned directory is removed, deepest entry first").doesNotExist();
        assertThat(owned.base()).as("the owned base itself survives cleanup").isDirectory();
    }

    @Test
    void preservesADirectoryWhoseMarkerCannotBeRead() throws Exception {
        OwnedDirectory owned = base("owned");
        Path unmarked = owned.resolve("unmarked");
        Files.createDirectories(unmarked);
        Files.writeString(unmarked.resolve("payload"), "learner data", StandardCharsets.UTF_8);
        Path markerIsDirectory = owned.resolve("marker-is-directory");
        Files.createDirectories(markerIsDirectory.resolve(MARKER));
        Path markerIsSymlink = owned.resolve("marker-is-symlink");
        Files.createDirectories(markerIsSymlink);
        Path foreign = tempDir.resolve("foreign-marker");
        Files.writeString(foreign, "schema=1\npid=" + deadPid() + "\n", StandardCharsets.UTF_8);
        Files.createSymbolicLink(markerIsSymlink.resolve(MARKER), foreign);

        assertThatThrownBy(() -> owned.readMarker(unmarked)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> owned.readMarker(markerIsDirectory)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> owned.readMarker(markerIsSymlink)).isInstanceOf(IOException.class);
        owned.cleanupAbandoned((candidate, marker) -> true);

        assertThat(unmarked.resolve("payload")).as("an unmarked directory is preserved").isRegularFile();
        assertThat(markerIsDirectory).as("a directory-shaped marker is preserved").isDirectory();
        assertThat(markerIsSymlink).as("a symlinked marker is preserved").isDirectory();
        assertThat(foreign).as("a symlinked marker's target is preserved").isRegularFile();
    }

    @Test
    void keepsCleaningAfterOnePreservedCandidate() throws Exception {
        OwnedDirectory owned = base("owned");
        Path unmarked = owned.resolve("unmarked");
        Files.createDirectories(unmarked);
        Path abandoned = owned.resolve("abandoned");
        owned.create(abandoned, "schema=1\npid=" + deadPid() + "\n");

        owned.cleanupAbandoned((candidate, marker) -> isDead(marker.getProperty("pid")));

        assertThat(unmarked).as("the preserved candidate is untouched").isDirectory();
        assertThat(abandoned).as("later candidates are still cleaned").doesNotExist();
    }

    @Test
    void preservesEverythingWhenTheOwnedBaseIsMissingOrSymlinked() throws Exception {
        OwnedDirectory missing = base("never-created");
        missing.cleanupAbandoned((candidate, marker) -> true);
        assertThat(missing.base()).doesNotExist();

        Path realBase = tempDir.resolve("real-base");
        Path child = realBase.resolve("child");
        Files.createDirectories(child);
        Files.writeString(child.resolve(MARKER), "schema=1\npid=" + deadPid() + "\n", StandardCharsets.UTF_8);
        OwnedDirectory symlinked = base("symlinked-base");
        Files.createSymbolicLink(symlinked.base(), realBase);

        symlinked.cleanupAbandoned((candidate, marker) -> true);

        assertThat(child).as("a symlinked owned base is never walked").isDirectory();
    }

    private OwnedDirectory base(String name) {
        return OwnedDirectory.under(tempDir.resolve(name), MARKER);
    }

    private static boolean isDead(String pidText) throws IOException {
        if (pidText == null || !pidText.matches("[1-9][0-9]*")) {
            throw new IOException("Invalid owner process ID.");
        }
        return ProcessHandle.of(Long.parseLong(pidText)).map(handle -> !handle.isAlive()).orElse(true);
    }

    private static long deadPid() {
        for (long pid = 4_000_000L; pid < 4_000_100L; pid++) {
            if (ProcessHandle.of(pid).isEmpty()) {
                return pid;
            }
        }
        throw new IllegalStateException("No unused process ID is available for this test");
    }
}
