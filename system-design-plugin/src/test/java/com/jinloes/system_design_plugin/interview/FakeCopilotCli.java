package com.jinloes.system_design_plugin.interview;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A stand-in for the Copilot CLI used by {@link CopilotInterviewerTest} and {@link CopilotInterviewerStdinTest}.
 * Its first argument (the "executable") is a properties file scripting the behavior: {@code record} (directory for
 * argv, stdin, and working-directory listings), {@code stdout}, {@code stdoutRepeat}, {@code stderr}, {@code exit},
 * {@code sleepMillis}, {@code hangBeforeStdin} (sleep {@code sleepMillis} before reading stdin), and
 * {@code exitImmediately} (exit with code 3 without reading stdin).
 */
final class FakeCopilotCli {
    private FakeCopilotCli() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties script = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8)) {
            script.load(reader);
        }
        if (Boolean.parseBoolean(script.getProperty("exitImmediately"))) {
            System.exit(3);
        }
        if (Boolean.parseBoolean(script.getProperty("hangBeforeStdin"))) {
            Thread.sleep(Long.parseLong(script.getProperty("sleepMillis", "0")));
        }
        byte[] stdin;
        try (InputStream input = System.in) {
            stdin = input.readAllBytes();
        }
        Path record = Path.of(script.getProperty("record"));
        Files.createDirectories(record);
        Files.write(record.resolve("argv.txt"), String.join("\n", args).getBytes(StandardCharsets.UTF_8));
        Files.write(record.resolve("stdin.txt"), stdin);
        Path cwd = Path.of("").toAbsolutePath();
        try (var entries = Files.list(cwd)) {
            Files.writeString(record.resolve("cwd.txt"), cwd + "\n" + entries
                    .map(path -> path.getFileName().toString()).sorted().collect(Collectors.joining("\n")));
        }
        if (!Boolean.parseBoolean(script.getProperty("hangBeforeStdin"))) {
            Thread.sleep(Long.parseLong(script.getProperty("sleepMillis", "0")));
        }
        String stdout = script.getProperty("stdout", "");
        int repeat = Integer.parseInt(script.getProperty("stdoutRepeat", "1"));
        for (int index = 0; index < repeat; index++) {
            System.out.print(stdout);
        }
        System.out.flush();
        System.err.print(script.getProperty("stderr", ""));
        System.err.flush();
        Thread.sleep(Long.parseLong(script.getProperty("sleepAfterStdoutMillis", "0")));
        System.exit(Integer.parseInt(script.getProperty("exit", "0")));
    }
}
