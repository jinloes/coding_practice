package com.jinloes.practice_plugin.ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/** A request from a React page, such as opening an exercise listed in a study guide. */
record PageEvent(Type type, String id) {
    enum Type { SELECT_EXERCISE, SELECT_GUIDE, SELECT_TRACK }

    /** Parses the page's JSON message; throws {@link IllegalArgumentException} when it is malformed. */
    static PageEvent parse(String json) {
        try {
            JsonObject message = JsonParser.parseString(json).getAsJsonObject();
            String id = message.get("id").getAsString();
            Type type = switch (message.get("type").getAsString()) {
                case "selectExercise" -> Type.SELECT_EXERCISE;
                case "selectGuide" -> Type.SELECT_GUIDE;
                case "selectTrack" -> Type.SELECT_TRACK;
                default -> throw new IllegalArgumentException("Unknown page event: " + json);
            };
            return new PageEvent(type, id);
        } catch (JsonParseException | IllegalStateException | UnsupportedOperationException
                 | NullPointerException exception) {
            throw new IllegalArgumentException("Malformed page event: " + json, exception);
        }
    }
}
