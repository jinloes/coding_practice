package com.jinloes.system_design_plugin.interview;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/**
 * A stand-in for {@code copilot --server --stdio} used by {@link CopilotInterviewerTest}: speaks
 * {@code Content-Length}-framed JSON-RPC on stdio. Its first argument (the "executable") is a properties file:
 * {@code record} (directory for argv, pid, the {@code envName} variable, and one line per request method),
 * {@code protocolVersion} (default 3), {@code modelsResult} (raw JSON), {@code modelsError} (error message), and
 * {@code mode}: {@code normal}, {@code exitEarly} (exit 4 before reading anything), or {@code hang} (answer
 * {@code connect}, never {@code models.list}). Notifications are ignored; it exits when stdin closes.
 */
final class FakeCopilotServer {
    private static final ObjectMapper JSON = new ObjectMapper();

    private FakeCopilotServer() {
    }

    public static void main(String[] args) throws IOException {
        Properties script = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8)) {
            script.load(reader);
        }
        Path record = Path.of(script.getProperty("record"));
        Files.createDirectories(record);
        Files.writeString(record.resolve("argv.txt"), String.join("\n", args));
        Files.writeString(record.resolve("pid.txt"), Long.toString(ProcessHandle.current().pid()));
        String envName = script.getProperty("envName", "PATH");
        Files.writeString(record.resolve("env.txt"), String.valueOf(System.getenv(envName)));
        String mode = script.getProperty("mode", "normal");
        if (mode.equals("exitEarly")) {
            System.exit(4);
        }
        InputStream in = System.in;
        OutputStream out = System.out;
        JsonNode message;
        while ((message = read(in)) != null) {
            if (!message.has("id") || !message.has("method")) {
                continue;
            }
            String method = message.get("method").asText();
            Files.writeString(record.resolve("methods.txt"), method + "\n", StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            ObjectNode reply = JSON.createObjectNode().put("jsonrpc", "2.0").set("id", message.get("id"));
            switch (method) {
                case "connect" -> reply.putObject("result").put("protocolVersion",
                        Integer.parseInt(script.getProperty("protocolVersion", "3")));
                case "models.list" -> {
                    if (mode.equals("hang")) {
                        continue;
                    }
                    String error = script.getProperty("modelsError");
                    if (error != null) {
                        reply.putObject("error").put("code", -32000).put("message", error);
                    } else {
                        reply.set("result", JSON.readTree(script.getProperty("modelsResult", "{\"models\":[]}")));
                    }
                }
                case "runtime.shutdown" -> reply.putNull("result");
                default -> reply.putObject("error").put("code", -32601).put("message", "Method not found");
            }
            write(out, reply);
        }
        System.exit(0);
    }

    private static JsonNode read(InputStream in) throws IOException {
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
        return body.length < length ? null : JSON.readTree(body);
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

    private static void write(OutputStream out, JsonNode message) throws IOException {
        byte[] body = JSON.writeValueAsBytes(message);
        out.write(("Content-Length: " + body.length + "\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
        out.write(body);
        out.flush();
    }
}
