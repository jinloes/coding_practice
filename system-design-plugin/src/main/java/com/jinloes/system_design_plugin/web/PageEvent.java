package com.jinloes.system_design_plugin.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Messages from the two bundled pages. Tool-window events are parsed by {@link #parse}; Excalidraw editor
 * messages by {@link #parseEditor}. Anything else is rejected with {@link IllegalArgumentException}. The
 * contract is mirrored in {@code web/src/types.ts}; change both together.
 */
public record PageEvent(Type type, String id) {
    public static final int MAX_SCENE_BYTES = 10 * 1024 * 1024;
    private static final Pattern ID = Pattern.compile("[a-z0-9][a-z0-9-]{0,199}");

    public enum Type {
        SELECT_PROBLEM("selectProblem", true),
        NEW_ATTEMPT("newAttempt", true),
        SELECT_ATTEMPT("selectAttempt", true),
        OPEN_ATTEMPT("openAttempt", true),
        REVEAL_HINT("revealHint", true),
        REQUEST_FEEDBACK("requestFeedback", true),
        CANCEL_FEEDBACK("cancelFeedback", false),
        OPEN_SETTINGS("openSettings", false);

        private final String wire;
        private final boolean needsId;

        Type(String wire, boolean needsId) {
            this.wire = wire;
            this.needsId = needsId;
        }
    }

    /** A message from the diagram editor page. {@code scene} is set only for {@link Kind#SAVE}. */
    public record EditorMessage(Kind kind, String scene) {
        public enum Kind { READY, SAVE, FLUSHED }
    }

    public static PageEvent parse(String json) {
        JsonObject message = object(json);
        String typeName = string(message, "type", json);
        for (Type type : Type.values()) {
            if (type.wire.equals(typeName)) {
                if (!type.needsId) {
                    return new PageEvent(type, "");
                }
                String id = string(message, "id", json);
                if (!ID.matcher(id).matches()) {
                    throw new IllegalArgumentException("Invalid ID in page event: " + typeName);
                }
                return new PageEvent(type, id);
            }
        }
        throw new IllegalArgumentException("Unknown page event: " + typeName);
    }

    public static EditorMessage parseEditor(String json) {
        if (json != null && json.length() > MAX_SCENE_BYTES + 1024) {
            throw new IllegalArgumentException(tooLarge());
        }
        JsonObject message = object(json);
        return switch (string(message, "type", "editor message")) {
            case "ready" -> new EditorMessage(EditorMessage.Kind.READY, null);
            case "flushed" -> new EditorMessage(EditorMessage.Kind.FLUSHED, null);
            case "save" -> new EditorMessage(EditorMessage.Kind.SAVE, scene(string(message, "scene", "save")));
            default -> throw new IllegalArgumentException("Unknown editor message.");
        };
    }

    static String tooLarge() {
        return "The diagram is larger than " + MAX_SCENE_BYTES / (1024 * 1024)
                + " MiB and was not saved. Remove large images and try again.";
    }

    private static String scene(String scene) {
        if (scene.getBytes(StandardCharsets.UTF_8).length > MAX_SCENE_BYTES) {
            throw new IllegalArgumentException(tooLarge());
        }
        JsonObject parsed = object(scene);
        if (!(parsed.get("type") instanceof JsonPrimitive type) || !"excalidraw".equals(type.getAsString())) {
            throw new IllegalArgumentException("The editor sent a scene that is not an Excalidraw file.");
        }
        return scene;
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
