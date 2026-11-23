package com.jinloes.practice_plugin.platform;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OsTest {
    @Test
    void detectsWindowsAndFormatsPlatformExecutables() {
        assertThat(Os.isWindows("Windows 11")).isTrue();
        assertThat(Os.isWindows("Linux")).isFalse();
        assertThat(Os.executable("java", true)).isEqualTo("java.exe");
        assertThat(Os.executable("java", false)).isEqualTo("java");
        assertThat(Os.executable("javac")).isEqualTo(Os.IS_WINDOWS ? "javac.exe" : "javac");
    }
}
