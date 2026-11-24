package com.jinloes.practice_plugin.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/** A request from a React page, such as opening an exercise, resetting filters, or running examples. */
record PageEvent(Type type, String id) {
    enum Type {
        SELECT_EXERCISE,
        SELECT_GUIDE,
        SELECT_TRACK,
        CLEAR_FILTERS,
        START_OR_OPEN,
        RUN_EXAMPLES,
        RUN_SAMPLE,
        COACH_DIAGNOSE,
        COACH_TRANSLATE,
        COACH_COMPARE,
        COACH_FOLLOW_UP,
        COACH_CANCEL,
        COACH_CLEAR,
        COACH_SETTINGS
    }

    /** Parses the page's JSON message; throws {@link IllegalArgumentException} when it is malformed. */
    static PageEvent parse(String json) {
        try {
            JsonObject message = JsonParser.parseString(json).getAsJsonObject();
            return switch (requiredString(message, "type")) {
                case "selectExercise" -> new PageEvent(Type.SELECT_EXERCISE, requiredString(message, "id"));
                case "selectGuide" -> new PageEvent(Type.SELECT_GUIDE, requiredString(message, "id"));
                case "selectTrack" -> new PageEvent(Type.SELECT_TRACK, requiredString(message, "id"));
                case "clearFilters" -> new PageEvent(Type.CLEAR_FILTERS, "");
                case "startOrOpen" -> new PageEvent(Type.START_OR_OPEN, "");
                case "runExamples" -> new PageEvent(Type.RUN_EXAMPLES, "");
                case "runSample" -> new PageEvent(Type.RUN_SAMPLE, "");
                case "coachDiagnose" -> new PageEvent(Type.COACH_DIAGNOSE, "");
                case "coachTranslate" -> new PageEvent(Type.COACH_TRANSLATE, "");
                case "coachCompare" -> new PageEvent(Type.COACH_COMPARE, "");
                case "coachFollowUp" -> new PageEvent(Type.COACH_FOLLOW_UP, requiredString(message, "text"));
                case "coachCancel" -> new PageEvent(Type.COACH_CANCEL, "");
                case "coachClear" -> new PageEvent(Type.COACH_CLEAR, "");
                case "coachSettings" -> new PageEvent(Type.COACH_SETTINGS, "");
                default -> throw new IllegalArgumentException("Unknown page event: " + json);
            };
        } catch (JsonParseException | IllegalStateException | UnsupportedOperationException
                 | NullPointerException exception) {
            throw new IllegalArgumentException("Malformed page event: " + json, exception);
        }
    }

    private static String requiredString(JsonObject object, String member) {
        JsonElement element = object.get(member);
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw new IllegalStateException("Missing string field: " + member);
        }
        String value = element.getAsString();
        if (value.isBlank()) {
            throw new IllegalStateException("Blank string field: " + member);
        }
        return value;
    }
}
