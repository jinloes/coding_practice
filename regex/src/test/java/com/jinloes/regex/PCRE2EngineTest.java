package com.jinloes.regex;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class PCRE2EngineTest {

    private final PCRE2Engine engine = new PCRE2Engine();

    @Test
    void findsAllWords() {
        assertThat(engine.findMatches("foo bar baz", "\\w+"))
            .isEqualTo(List.of("foo", "bar", "baz"));
    }

    @Test
    void findsAllIntegers() {
        assertThat(engine.findMatches("order 3 items at 10 dollars each", "\\d+"))
            .isEqualTo(List.of("3", "10"));
    }

    @Test
    void findsDecimalPrices() {
        assertThat(engine.findMatches("pay $19.99 and $5.00", "\\d+\\.\\d{2}"))
            .isEqualTo(List.of("19.99", "5.00"));
    }

    @Test
    void findsDates() {
        assertThat(engine.findMatches("from 2024-01-15 to 2024-03-31", "\\d{4}-\\d{2}-\\d{2}"))
            .isEqualTo(List.of("2024-01-15", "2024-03-31"));
    }

    @Test
    void findsEmailAddresses() {
        assertThat(engine.findMatches("contact alice@example.com or bob@test.org for info",
            "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}"))
            .isEqualTo(List.of("alice@example.com", "bob@test.org"));
    }

    @Test
    void findsCaseInsensitiveWithInlineFlag() {
        assertThat(engine.findMatches("Hello WORLD hello world", "(?i)hello"))
            .isEqualTo(List.of("Hello", "hello"));
    }

    @Test
    void findsUnicodeCharacters() {
        assertThat(engine.findMatches("CJK: 漢 字", "\\p{han}"))
            .isEqualTo(List.of("漢", "字"));
    }

    @Test
    void returnsEmptyListWhenNoMatches() {
        assertThat(engine.findMatches("hello world", "\\d+"))
            .isEmpty();
    }

    @Test
    void returnsEmptyListForEmptyInput() {
        assertThat(engine.findMatches("", "\\w+"))
            .isEmpty();
    }

    @Test
    void findsMatchesWithJit() {
        assertThat(engine.findMatches("foo bar baz", "\\w+", true))
            .isEqualTo(List.of("foo", "bar", "baz"));
    }

    @Test
    void findsUnicodeCharactersWithJit() {
        assertThat(engine.findMatches("CJK: 漢 字", "\\p{han}", true))
            .isEqualTo(List.of("漢", "字"));
    }

    @Test
    void testsAnchoredPatternsWithAndWithoutJitAndEmptyMatchesInProcess() {
        assertThat(engine.test("abc", null, "a.c", true)).isPositive();
        assertThat(engine.test("abcd", null, "a.c", true)).isNegative();

        String previous = System.getProperty("pcre2.regex.jit");
        try {
            System.setProperty("pcre2.regex.jit", "false");
            assertThat(engine.test("abc", java.util.EnumSet.of(org.pcre4j.option.Pcre2CompileOption.UTF), "a.c", true))
                .isPositive();
            assertThat(engine.findMatches("a", "a*", true)).containsExactly("a", "");
        } finally {
            if (previous == null) {
                System.clearProperty("pcre2.regex.jit");
            } else {
                System.setProperty("pcre2.regex.jit", previous);
            }
        }

        assertThat(engine.findMatches("", "")).containsExactly("");
        assertThat(engine.findMatches("\uD83D\uDE00", "")).containsExactly("", "");
        assertThat(engine.findMatches("ab", "(?=a)|b")).containsExactly("", "b");
    }

    @Test
    void emptyMatchesTerminateAndAdvanceAcrossUnicodeWithAndWithoutJit() throws Exception {
        String classpath = String.join(File.pathSeparator, List.of(
                EmptyMatchProbe.class, PCRE2Engine.class, org.pcre4j.Pcre2Code.class,
                org.pcre4j.api.IPcre2.class, org.pcre4j.jna.Pcre2.class, Class.forName("com.sun.jna.Native"))
                .stream().map(type -> {
                    try {
                        return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
                    } catch (java.net.URISyntaxException exception) {
                        throw new IllegalStateException(exception);
                    }
                }).distinct().toList());
        String pcre2Lib = System.getProperty("pcre2.library.name");
        for (boolean jitAllowed : List.of(false, true)) {
            List<String> command = new java.util.ArrayList<>();
            command.add(Path.of(System.getProperty("java.home"), "bin",
                    System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString());
            command.add("-Xmx64m");
            if (pcre2Lib != null) {
                command.add("-Dpcre2.library.name=" + pcre2Lib);
            }
            command.add("-Dpcre2.regex.jit=" + jitAllowed);
            command.add("-classpath");
            command.add(classpath);
            command.add(EmptyMatchProbe.class.getName());
            Process process = new ProcessBuilder(command).start();
            try {
                assertThat(process.waitFor(10, TimeUnit.SECONDS))
                        .as("zero-width matching must terminate (JIT allowed: %s)", jitAllowed).isTrue();
                assertThat(process.exitValue())
                        .as(new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8)).isZero();
                assertThat(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                        .lines().toList()).containsExactly(
                                "1|", "2||", "3|||", "2||", "2||", "2|a|", "2||b", "1|", "1|", "1|", "2||",
                                "1|", "2||", "3|||", "2||", "2||", "2|a|", "2||b", "1|", "1|", "1|", "2||");
            } finally {
                process.destroyForcibly();
                assertThat(process.waitFor(5, TimeUnit.SECONDS)).isTrue();
            }
        }
    }

    public static final class EmptyMatchProbe {
        public static void main(String[] args) {
            PCRE2Engine engine = new PCRE2Engine();
            for (boolean useJit : List.of(false, true)) {
                for (String[] example : List.of(
                        new String[]{"", ""},
                        new String[]{"a", ""},
                        new String[]{"ab", ""},
                        new String[]{"\u6f22", ""},
                        new String[]{"\uD83D\uDE00", ""},
                        new String[]{"a", "a*"},
                        new String[]{"ab", "(?=a)|b"},
                        new String[]{"abc", "^"},
                        new String[]{"abc", "$"},
                        new String[]{"ab", "(?=b)"},
                        new String[]{"b", "a*"})) {
                    List<String> matches = engine.findMatches(example[0], example[1], useJit);
                    System.out.println(matches.size() + "|" + String.join("|", matches));
                }
            }
        }
    }
}