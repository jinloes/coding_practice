package com.jinloes.practice_plugin.run;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

final class TestChildProcess {
    static List<String> command(String mode) {
        return command(TestChildProcess.class, mode);
    }

    static List<String> command(Class<?> main, String... arguments) {
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "-classpath", System.getProperty("practice.test.classpath", System.getProperty("java.class.path")),
                main.getName()));
        command.addAll(List.of(arguments));
        return command;
    }

    static Process start(Class<?> main, String... arguments) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command(main, arguments));
        builder.environment().remove("JAVA_TOOL_OPTIONS");
        builder.environment().remove("JDK_JAVA_OPTIONS");
        return builder.start();
    }

    public static void main(String[] args) throws InterruptedException {
        switch (args[0]) {
            case "silent" -> { }
            case "waiting" -> new CountDownLatch(1).await();
            default -> throw new IllegalArgumentException("Unknown child fixture mode: " + args[0]);
        }
    }
}
