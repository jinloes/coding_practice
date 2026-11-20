package com.jinloes.system_design_plugin.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.interview.InterviewPrompt;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Messages from the two bundled pages. Tool-window events are parsed by {@link #parse}; Excalidraw editor
 * messages by {@link #parseEditor}. Anything else is rejected with {@link IllegalArgumentException}. The
 * contract is mirrored in {@code web/src/types.ts}; change both together.
 */
public record PageEvent(Type type, String id, String text) {
    public static final int MAX_SCENE_BYTES = 10 * 1024 * 1024;
    private static final int MAX_EVENT_CHARS = InterviewPrompt.MAX_SECTION_CHARS + 4_096;
    private static final int MAX_LABEL_WIRE_CHARS = 200;
    private static final Pattern ID = Pattern.compile("[a-z0-9][a-z0-9-]{0,199}");
    private static final Pattern SEQ_HEADER = Pattern.compile("\"seq\"\\s*:\\s*([0-9]{1,18})");

    public PageEvent(Type type, String id) {
        this(type, id, "");
    }

    public PageEvent {
        Objects.requireNonNull(type, "type");
        id = Objects.requireNonNullElse(id, "");
        text = Objects.requireNonNullElse(text, "");
    }

    private enum Payload { NONE, LEVEL, LABEL, FOLLOW_UP }

    public enum Type {
        SELECT_PROBLEM("selectProblem", true, Payload.NONE),
        NEW_ATTEMPT("newAttempt", true, Payload.NONE),
        SELECT_ATTEMPT("selectAttempt", true, Payload.NONE),
        OPEN_ATTEMPT("openAttempt", true, Payload.NONE),
        RENAME_ATTEMPT("renameAttempt", true, Payload.LABEL),
        DELETE_ATTEMPT("deleteAttempt", true, Payload.NONE),
        REVEAL_HINT("revealHint", true, Payload.NONE),
        REQUEST_FEEDBACK("requestFeedback", true, Payload.NONE),
        SUBMIT_FOLLOW_UP("submitFollowUp", true, Payload.FOLLOW_UP),
        SELECT_LEVEL("selectLevel", false, Payload.LEVEL),
        CANCEL_FEEDBACK("cancelFeedback", false, Payload.NONE),
        OPEN_SETTINGS("openSettings", false, Payload.NONE);

        private final String wire;
        private final boolean needsId;
        private final Payload payload;

        Type(String wire, boolean needsId, Payload payload) {
            this.wire = wire;
            this.needsId = needsId;
            this.payload = payload;
        }
    }

    /**
     * A message from the diagram editor page. {@code seq} is populated for {@link Kind#SAVE} and
     * {@link Kind#FLUSHED} ({@code 0} for {@link Kind#READY}); {@code scene} is populated for {@link Kind#SAVE}.
     */
    public record EditorMessage(Kind kind, long seq, String scene) {
        public enum Kind { READY, SAVE, FLUSHED }
    }

    /**
     * Rejected editor payload carrying the request's {@code seq} when extractable (or {@code -1L}) and whether
     * the rejection represents a {@code save} failure rather than a {@code flushed} or {@code ready} message.
     */
    public static final class InvalidSceneException extends IllegalArgumentException {
        private final long seq;
        private final boolean saveFailure;

        InvalidSceneException(long seq, boolean saveFailure, String message) {
            super(message);
            this.seq = seq;
            this.saveFailure = saveFailure;
        }

        InvalidSceneException(long seq, boolean saveFailure, String message, Throwable cause) {
            super(message, cause);
            this.seq = seq;
            this.saveFailure = saveFailure;
        }

        public long seq() {
            return seq;
        }

        public boolean saveFailure() {
            return saveFailure;
        }
    }

    public static PageEvent parse(String json) {
        if (json != null && json.length() > MAX_EVENT_CHARS) {
            throw new IllegalArgumentException("Page event exceeds maximum length.");
        }
        JsonObject message = object(json);
        String typeName = string(message, "type", json);
        for (Type type : Type.values()) {
            if (type.wire.equals(typeName)) {
                String id = "";
                if (type.needsId) {
                    id = string(message, "id", json);
                    if (!ID.matcher(id).matches()) {
                        throw new IllegalArgumentException("Invalid ID in page event: " + typeName);
                    }
                }
                String payload = switch (type.payload) {
                    case NONE -> "";
                    case LEVEL -> parseLevel(message, json);
                    case LABEL -> parseLabel(message, json);
                    case FOLLOW_UP -> parseFollowUp(message, json);
                };
                return new PageEvent(type, id, payload);
            }
        }
        throw new IllegalArgumentException("Unknown page event: " + typeName);
    }

    private static String parseLevel(JsonObject message, String context) {
        String level = string(message, "level", context);
        if (!DesignCatalog.LEVELS.contains(level)) {
            throw new IllegalArgumentException("Unsupported target level: " + level);
        }
        return level;
    }

    private static String parseLabel(JsonObject message, String context) {
        String label = string(message, "label", context);
        if (label.length() > MAX_LABEL_WIRE_CHARS) {
            throw new IllegalArgumentException("Attempt label is too long.");
        }
        return label.replaceAll("[\\p{Cntrl}]+", " ").strip();
    }

    private static String parseFollowUp(JsonObject message, String context) {
        String answers = string(message, "answers", context).strip();
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("Write a follow-up answer before submitting.");
        }
        if (answers.length() > InterviewPrompt.MAX_SECTION_CHARS) {
            throw new IllegalArgumentException("Follow-up answer exceeds " + InterviewPrompt.MAX_SECTION_CHARS + " characters.");
        }
        return answers;
    }

    public static EditorMessage parseEditor(String json) {
        long fallbackSeq = extractSeq(json);
        if (json != null && json.length() > MAX_SCENE_BYTES + 1024) {
            throw new InvalidSceneException(fallbackSeq, true, tooLarge());
        }
        JsonObject message = editorObject(json, fallbackSeq);
        return switch (editorString(message, "type", "editor message", fallbackSeq)) {
            case "ready" -> new EditorMessage(EditorMessage.Kind.READY, 0L, null);
            case "flushed" -> new EditorMessage(EditorMessage.Kind.FLUSHED,
                    positiveLong(message, "seq", json, false), null);
            case "save" -> {
                long seq = positiveLong(message, "seq", json, true);
                yield new EditorMessage(EditorMessage.Kind.SAVE, seq,
                        scene(seq, editorString(message, "scene", "save", seq)));
            }
            default -> throw new InvalidSceneException(fallbackSeq, true, "Unknown editor message.");
        };
    }

    static long extractSeq(String json) {
        if (json == null) {
            return -1L;
        }
        String prefix = json.substring(0, Math.min(json.length(), 256));
        var matcher = SEQ_HEADER.matcher(prefix);
        if (!matcher.find()) {
            return -1L;
        }
        long parsed = Long.parseLong(matcher.group(1));
        return parsed > 0 ? parsed : -1L;
    }

    static String tooLarge() {
        return "The diagram is larger than " + MAX_SCENE_BYTES / (1024 * 1024)
                + " MiB and was not saved. Remove large images and try again.";
    }

    private static String scene(long seq, String scene) {
        if (scene.getBytes(StandardCharsets.UTF_8).length > MAX_SCENE_BYTES) {
            throw new InvalidSceneException(seq, true, tooLarge());
        }
        JsonObject parsed = editorObject(scene, seq);
        if (!(parsed.get("type") instanceof JsonPrimitive type) || !"excalidraw".equals(type.getAsString())) {
            throw new InvalidSceneException(seq, true, "The editor sent a scene that is not an Excalidraw file.");
        }
        return scene;
    }

    private static long positiveLong(JsonObject message, String member, String context, boolean saveFailure) {
        if (message.get(member) instanceof JsonPrimitive primitive && primitive.isNumber()) {
            double number = primitive.getAsDouble();
            long value = primitive.getAsLong();
            if (number == (double) value && value > 0) {
                return value;
            }
        }
        throw new InvalidSceneException(-1L, saveFailure,
                "Page message needs a positive integer '" + member + "': " + abbreviate(context));
    }

    private static JsonObject editorObject(String json, long seq) {
        try {
            JsonElement element = JsonParser.parseString(json == null ? "" : json);
            if (element instanceof JsonObject object) {
                return object;
            }
        } catch (JsonParseException malformed) {
            throw new InvalidSceneException(seq, true, "Malformed page message.", malformed);
        }
        throw new InvalidSceneException(seq, true, "Page message must be a JSON object.");
    }

    private static String editorString(JsonObject message, String member, String context, long seq) {
        if (message.get(member) instanceof JsonPrimitive primitive && primitive.isString()) {
            return primitive.getAsString();
        }
        throw new InvalidSceneException(seq, true, "Page message is missing '" + member + "': " + abbreviate(context));
    }

    private static JsonObject object(String json) {
        try {
            JsonElement element = JsonParser.parseString(json == null ? "" : json);
            if (element instanceof JsonObject object) {
                return object;
            }
        } catch (JsonParseException malformed) {
            throw new IllegalArgumentException("Malformed page message.", malformed);
        }
        throw new IllegalArgumentException("Page message must be a JSON object.");
    }

    private static String string(JsonObject message, String member, String context) {
        if (message.get(member) instanceof JsonPrimitive primitive && primitive.isString()) {
            return primitive.getAsString();
        }
        throw new IllegalArgumentException("Page message is missing '" + member + "': " + abbreviate(context));
    }

    private static String abbreviate(String text) {
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }
}
