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
    }
}
