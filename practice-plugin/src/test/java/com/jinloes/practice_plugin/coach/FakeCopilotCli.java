package com.jinloes.practice_plugin.coach;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Stand-in for the Copilot CLI used by {@link CopilotCoachTest}. Supports both one-shot prompt execution
 * and {@code --server --stdio} JSON-RPC model listing when {@code --server} is present in {@code args}.
 */
final class FakeCopilotCli {
    private FakeCopilotCli() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties script = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8)) {
            script.load(reader);
        }
        if (Arrays.asList(args).contains("--server")) {
            runServer(args, script);
            return;
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

    private static void runServer(String[] args, Properties script) throws IOException {
        Path record = Path.of(script.getProperty("record"));
        Files.createDirectories(record);
        Files.writeString(record.resolve("argv.txt"), String.join("\n", args));
        Files.writeString(record.resolve("pid.txt"), Long.toString(ProcessHandle.current().pid()));
        String mode = script.getProperty("mode", "normal");
        if (mode.equals("exitEarly")) {
            System.exit(4);
        }
        InputStream in = System.in;
        OutputStream out = System.out;
        JsonObject message;
        while ((message = readRpc(in)) != null) {
            if (!message.has("id") || !message.has("method")) {
                continue;
            }
            String method = message.get("method").getAsString();
            Files.writeString(record.resolve("methods.txt"), method + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            JsonObject reply = new JsonObject();
            reply.addProperty("jsonrpc", "2.0");
            reply.add("id", message.get("id"));
            switch (method) {
                case "connect" -> {
                    JsonObject result = new JsonObject();
                    result.addProperty("protocolVersion",
                            Integer.parseInt(script.getProperty("protocolVersion", "4")));
                    reply.add("result", result);
                }
                case "models.list" -> {
                    if (mode.equals("hang")) {
                        continue;
                    }
                    String error = script.getProperty("modelsError");
                    if (error != null) {
                        JsonObject err = new JsonObject();
                        err.addProperty("code", -32000);
                        err.addProperty("message", error);
                        reply.add("error", err);
                    } else {
                        JsonElement result = JsonParser.parseString(
                                script.getProperty("modelsResult", "{\"models\":[]}"));
                        reply.add("result", result);
                    }
                }
                case "runtime.shutdown" -> reply.add("result", null);
                default -> {
                    JsonObject err = new JsonObject();
                    err.addProperty("code", -32601);
                    err.addProperty("message", "Method not found");
                    reply.add("error", err);
                }
            }
            writeRpc(out, reply);
        }
        System.exit(0);
    }

    private static JsonObject readRpc(InputStream in) throws IOException {
        int length = -1;
        String line;
        while ((line = readLine(in)) != null) {
            if (line.isEmpty()) {
                if (length >= 0) {
                    break;
                }
                continue;
            }
            if (line.toLowerCase().startsWith("content-length:")) {
                length = Integer.parseInt(line.substring("content-length:".length()).strip());
            }
        }
        if (line == null) {
            return null;
        }
        byte[] body = in.readNBytes(length);
        return body.length < length
                ? null
                : JsonParser.parseString(new String(body, StandardCharsets.UTF_8)).getAsJsonObject();
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int next;
        while ((next = in.read()) != -1 && next != '\n') {
            if (next != '\r') {
                line.write(next);
            }
        }
        return next == -1 && line.size() == 0 ? null : line.toString(StandardCharsets.US_ASCII);
    }

    private static void writeRpc(OutputStream out, JsonObject message) throws IOException {
        byte[] body = message.toString().getBytes(StandardCharsets.UTF_8);
        out.write(("Content-Length: " + body.length + "\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
        out.write(body);
        out.flush();
    }
}
