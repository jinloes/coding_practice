package com.jinloes.design.file_system;

import com.jinloes.design.file_system.InMemoryFileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryFileSystemTest {

    private InMemoryFileSystem fs;

    @BeforeEach
    void setUp() {
        fs = new InMemoryFileSystem();
    }

    @Nested
    class ListingAndMkdirTests {

        @Test
        void rootInitiallyEmpty() {
            List<String> root = fs.ls("/");
            assertThat(root).isEmpty();
        }

        @Test
        void createsIntermediateDirectoriesAndShowsChildren() {
            fs.mkdir("/a/b/c");
            assertThat(fs.ls("/")).containsExactly("a");
            assertThat(fs.ls("/a")).containsExactly("b");
            assertThat(fs.ls("/a/b")).containsExactly("c");
        }

        @Test
        void consecutiveSlashesAreIgnored() {
            fs.mkdir("/a//b");
            assertThat(fs.ls("/a")).containsExactly("b");
        }

        @Test
        void lsNonExistentPathReturnsEmpty() {
            assertThat(fs.ls("/ghost")).isEmpty();
        }

        @Test
        void lsDeepNonExistentPathReturnsEmpty() {
            fs.mkdir("/a");
            assertThat(fs.ls("/a/b/c")).isEmpty();
        }

        @Test
        void mkdirNullEmptyOrRootIsNoOp() {
            fs.mkdir(null);
            fs.mkdir("");
            fs.mkdir("/");
            assertThat(fs.ls("/")).isEmpty();
        }

        @Test
        void lsNullOrEmptyResolvesToRoot() {
            fs.mkdir("/a");
            assertThat(fs.ls(null)).containsExactly("a");
            assertThat(fs.ls("")).containsExactly("a");
        }
    }

    @Nested
    class FileOperationsTests {

        @Test
        void addContentAndReadContent() {
            fs.createFile("/a/b/file.txt");
            fs.addContentToFile("/a/b/file.txt", "hello");
            fs.addContentToFile("/a/b/file.txt", " world");
            assertThat(fs.ls("/a/b/file.txt")).containsExactly("file.txt");
            assertThat(fs.readContentFromFile("/a/b/file.txt")).isEqualTo("hello world");
        }

        @Test
        void nonExistentFileThrows() {
            assertThatThrownBy(() -> fs.readContentFromFile("/no/such/file.txt"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createFileNullOrEmptyThrows() {
            assertThatThrownBy(() -> fs.createFile(null)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> fs.createFile("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createFileAtRoot() {
            fs.createFile("/top.txt");
            assertThat(fs.ls("/")).containsExactly("top.txt");
        }

        @Test
        void createFileWithRelativePath() {
            fs.createFile("rel.txt");
            assertThat(fs.ls("/")).containsExactly("rel.txt");
        }

        @Test
        void createFileUnderIntermediateFileThrows() {
            fs.addContentToFile("/a", "data");
            assertThatThrownBy(() -> fs.createFile("/a/b/c.txt"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createFileWhereDirectoryExistsThrows() {
            fs.mkdir("/x");
            assertThatThrownBy(() -> fs.createFile("/x")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createFileTwiceIsIdempotent() {
            fs.createFile("/a.txt");
            fs.createFile("/a.txt");
            assertThat(fs.ls("/")).containsExactly("a.txt");
        }

        @Test
        void createFileUnderAFilePathThrows() {
            fs.addContentToFile("/a", "data");
            assertThatThrownBy(() -> fs.createFile("/a/b.txt")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void addContentToDirectoryThrows() {
            fs.mkdir("/d");
            assertThatThrownBy(() -> fs.addContentToFile("/d", "x"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void readContentFromDirectoryThrows() {
            fs.mkdir("/d");
            assertThatThrownBy(() -> fs.readContentFromFile("/d"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void readThroughAFilePathThrows() {
            fs.addContentToFile("/a", "data");
            assertThatThrownBy(() -> fs.readContentFromFile("/a/b"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void removesFileAndDirectories() {
            fs.addContentToFile("/x/y/z.txt", "data");
            assertThat(fs.ls("/x/y")).containsExactly("z.txt");
            fs.delete("/x/y/z.txt");
            assertThat(fs.ls("/x/y")).isEmpty();
            // remove directory
            fs.delete("/x/y");
            assertThat(fs.ls("/x")).isEmpty();
        }

        @Test
        void rootDeletionThrows() {
            assertThatThrownBy(() -> fs.delete("/")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void nullOrEmptyDeletionThrows() {
            assertThatThrownBy(() -> fs.delete(null)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> fs.delete("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void deleteNonExistentThrows() {
            assertThatThrownBy(() -> fs.delete("/nope")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class MoveTests {

        @Test
        void fileIntoDirectoryPreserveContent() {
            fs.addContentToFile("/src/one.txt", "one");
            fs.mkdir("/dest");
            fs.move("/src/one.txt", "/dest");
            assertThat(fs.ls("/dest")).containsExactly("one.txt");
            assertThat(fs.readContentFromFile("/dest/one.txt")).isEqualTo("one");
            assertThat(fs.ls("/src")).isEmpty();
        }

        @Test
        void renameWithinParentPreserveContent() {
            fs.addContentToFile("/dir/a.txt", "a");
            fs.move("/dir/a.txt", "/dir/b.txt");
            assertThat(fs.ls("/dir")).containsExactly("b.txt");
            assertThat(fs.readContentFromFile("/dir/b.txt")).isEqualTo("a");
        }

        @Test
        void replaceExistingFile() {
            fs.addContentToFile("/d/f1.txt", "first");
            fs.addContentToFile("/d/f2.txt", "second");
            fs.move("/d/f1.txt", "/d/f2.txt");
            assertThat(fs.ls("/d")).containsExactly("f2.txt");
            assertThat(fs.readContentFromFile("/d/f2.txt")).isEqualTo("first");
        }

        @Test
        void movingDirectoryIntoOwnDescendantThrows() {
            fs.mkdir("/a/b");
            assertThatThrownBy(() -> fs.move("/a", "/a/b/c"))
                    .isInstanceOf(IllegalArgumentException.class);
            // Source must remain intact after a rejected move.
            assertThat(fs.ls("/")).containsExactly("a");
            assertThat(fs.ls("/a")).containsExactly("b");
        }

        @Test
        void movingDirectoryOntoItselfThrows() {
            fs.mkdir("/a/b");
            assertThatThrownBy(() -> fs.move("/a", "/a"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void movingRootThrows() {
            assertThatThrownBy(() -> fs.move("/", "/x"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void movingNonExistentSourceThrows() {
            assertThatThrownBy(() -> fs.move("/missing.txt", "/dest.txt"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void nullArgumentsThrow() {
            assertThatThrownBy(() -> fs.move(null, "/x")).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> fs.move("/x", null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        void moveFileToNewNameInAnotherDirectory() {
            fs.addContentToFile("/src/one.txt", "one");
            fs.mkdir("/dest");
            fs.move("/src/one.txt", "/dest/renamed.txt");
            assertThat(fs.ls("/dest")).containsExactly("renamed.txt");
            assertThat(fs.readContentFromFile("/dest/renamed.txt")).isEqualTo("one");
        }

        @Test
        void moveFileIntoDirectoryThatAlreadyHasSameName() {
            fs.addContentToFile("/dest/item.txt", "old");
            fs.addContentToFile("/src/item.txt", "new");
            fs.move("/src/item.txt", "/dest");
            assertThat(fs.ls("/dest")).containsExactly("item.txt");
            assertThat(fs.readContentFromFile("/dest/item.txt")).isEqualTo("new");
            assertThat(fs.ls("/src")).isEmpty();
        }
    }
}
