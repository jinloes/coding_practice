package com.jinloes.system_design_plugin.interview;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewFeedbackTest {
    static JsonObject valid() {
        JsonObject json = new JsonObject();
        json.addProperty("verdict", "Hire");
        json.addProperty("summary", "Solid design.");
        JsonArray dimensions = new JsonArray();
        for (int index = DesignCatalog.DIMENSIONS.size() - 1; index >= 0; index--) {
            JsonObject dimension = new JsonObject();
            dimension.addProperty("name", DesignCatalog.DIMENSIONS.get(index));
            dimension.addProperty("score", 1 + index % 4);
            dimension.add("strengths", strings("good " + index));
            dimension.add("gaps", strings());
            dimensions.add(dimension);
        }
        json.add("dimensions", dimensions);
        json.add("followUpQuestions", strings("How do you shard?"));
        json.add("missedExpectations", strings("Mentions cache eviction"));
        return json;
    }

    static JsonArray strings(String... values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    private static String with(Consumer<JsonObject> change) {
        JsonObject json = valid();
        change.accept(json);
        return json.toString();
    }

    @Test
    void parsesPlainFencedAndEmbeddedJsonInRubricOrder() {
        String json = valid().toString();
        for (String output : new String[]{json, "Here you go:\n```json\n" + json + "\n```\nThanks",
                "```\n" + json + "\n```", "Sure! " + json + " Hope that helps."}) {
            InterviewFeedback feedback = InterviewFeedback.parse(output);
            assertThat(feedback.parsed()).as(output).isTrue();
            assertThat(feedback.verdict()).isEqualTo("Hire");
            assertThat(feedback.summary()).isEqualTo("Solid design.");
            assertThat(feedback.dimensions()).extracting(InterviewFeedback.Dimension::name)
                    .containsExactlyElementsOf(DesignCatalog.DIMENSIONS);
            assertThat(feedback.dimensions().get(0).score()).isEqualTo(1);
            assertThat(feedback.dimensions().get(0).strengths()).containsExactly("good 0");
            assertThat(feedback.followUpQuestions()).containsExactly("How do you shard?");
            assertThat(feedback.missedExpectations()).containsExactly("Mentions cache eviction");
            assertThat(feedback.raw()).isEmpty();
        }
    }

    @Test
    void rejectsInvalidVerdictsDimensionsAndScores() {
        String[] invalid = {
                "{\"verdict\":\"Maybe\"}",
                with(json -> json.addProperty("verdict", "Maybe")),
                with(json -> json.remove("summary")),
                with(json -> json.addProperty("dimensions", "none")),
                with(json -> json.getAsJsonArray("dimensions").remove(0)),
                with(json -> json.getAsJsonArray("dimensions").add("not an object")),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject().addProperty("name", "Vibes")),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject()
                        .addProperty("name", DesignCatalog.DIMENSIONS.get(0))),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject().addProperty("score", 5)),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject().addProperty("score", 0)),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject().addProperty("score", 2.5)),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject().addProperty("score", "3")),
                with(json -> json.getAsJsonArray("dimensions").get(0).getAsJsonObject().remove("gaps")),
                with(json -> json.add("followUpQuestions", strings())),
                with(json -> json.getAsJsonArray("missedExpectations").add(3)),
                with(json -> json.remove("missedExpectations")),
                "[1, 2]", "no json at all", "{broken", ""};
        for (String output : invalid) {
            InterviewFeedback feedback = InterviewFeedback.parse(output);
            if (output.contains("\"followUpQuestions\":[]")) {
                assertThat(feedback.parsed()).as("empty lists are allowed").isTrue();
                continue;
            }
            assertThat(feedback.parsed()).as(output).isFalse();
            assertThat(feedback.raw()).isEqualTo(output.strip());
            assertThat(feedback.dimensions()).isEmpty();
        }
        assertThat(InterviewFeedback.parse(null).raw()).isEmpty();
    }

    @Test
    void boundsRawTextToSixtyFourKibWithoutSplittingCharacters() {
        String ascii = "a".repeat(InterviewFeedback.MAX_RAW_BYTES + 10);
        assertThat(InterviewFeedback.parse(ascii).raw()).hasSize(InterviewFeedback.MAX_RAW_BYTES);

        String multibyte = "a" + "é".repeat(InterviewFeedback.MAX_RAW_BYTES / 2);
        String bounded = InterviewFeedback.unparsed(multibyte).raw();
        assertThat(bounded.getBytes(java.nio.charset.StandardCharsets.UTF_8).length)
                .isLessThanOrEqualTo(InterviewFeedback.MAX_RAW_BYTES);
        assertThat(bounded).startsWith("aé").doesNotContain("\uFFFD");
        assertThat(InterviewFeedback.boundRaw(null)).isEmpty();
    }

    @Test
    void storedRecordsRoundTripParsedAndRawFeedback() {
        InterviewFeedback parsed = InterviewFeedback.parse(valid().toString());
        InterviewFeedback.Stored stored = new InterviewFeedback.Stored(parsed, "fp", "model-x", "2026-01-01T00:00:00Z");
        InterviewFeedback.Stored restored = InterviewFeedback.Stored.fromJson(stored.toJson());
        assertThat(restored).isEqualTo(stored);

        InterviewFeedback.Stored raw = new InterviewFeedback.Stored(InterviewFeedback.unparsed("oops"), null, null, null);
        assertThat(raw.fingerprint()).isEmpty();
        assertThat(InterviewFeedback.Stored.fromJson(raw.toJson())).isEqualTo(raw);
        assertThat(raw.toJson()).doesNotContain("\"feedback\"");
    }

    @Test
    void corruptStoredRecordsAreRejected() {
        String good = new InterviewFeedback.Stored(InterviewFeedback.parse(valid().toString()), "fp", "m", "t").toJson();
        String[] corrupt = {"", "[]", "{", "{\"model\":\"m\",\"createdAt\":\"t\",\"parsed\":false,\"raw\":\"x\"}",
                "{\"fingerprint\":\"fp\",\"model\":\"m\",\"createdAt\":\"t\",\"parsed\":true}",
                "{\"fingerprint\":\"fp\",\"model\":\"m\",\"createdAt\":\"t\",\"parsed\":false}",
                good.replace("\"Hire\"", "\"Maybe\"")};
        for (String text : corrupt) {
            assertThatThrownBy(() -> InterviewFeedback.Stored.fromJson(text)).as(text)
                    .isInstanceOf(IllegalArgumentException.class);
        }
        assertThat(InterviewFeedback.Stored.fromJson(
                "{\"fingerprint\":\"fp\",\"model\":\"m\",\"createdAt\":\"t\",\"parsed\":\"yes\",\"raw\":\"x\"}")
                .feedback().parsed()).isFalse();
    }
}
