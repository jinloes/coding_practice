package com.jinloes.system_design_plugin.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;
import com.jinloes.system_design_plugin.web.PageState.AttemptInfo;
import com.jinloes.system_design_plugin.web.PageState.Review;
import com.jinloes.system_design_plugin.web.PageState.Theme;
import com.jinloes.system_design_plugin.web.PageState.View;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PageStateTest {
    private static final Theme THEME = new Theme(true, "#000000", "#ffffff", "#888888", "#333333", "#3366ff",
            "\"Inter\", system-ui, sans-serif", 13);
    private static final Problem PROBLEM = DesignCatalog.all().get(0);

    private static JsonObject render(View view) {
        return JsonParser.parseString(PageState.toJson(view, THEME)).getAsJsonObject();
    }

    private static View view(Problem selected, int hints, InterviewFeedback.Stored feedback, boolean stale) {
        List<Review> reviews = feedback == null ? List.of() : List.of(new Review(feedback, stale));
        return new View(Set.of(PROBLEM.id()), selected, List.of(new AttemptInfo("a1", "Attempt 1")), "a1", hints,
                false, reviews, null);
    }

    private static InterviewFeedback feedbackMissing(String expectation) {
        JsonObject json = new JsonObject();
        json.addProperty("verdict", DesignCatalog.VERDICTS.get(0));
        json.addProperty("summary", "Solid <b>start</b>");
        JsonArray dimensions = new JsonArray();
        for (String name : DesignCatalog.DIMENSIONS) {
            JsonObject dimension = new JsonObject();
            dimension.addProperty("name", name);
            dimension.addProperty("score", 3);
            dimension.add("strengths", new JsonArray());
            dimension.add("gaps", new JsonArray());
            dimensions.add(dimension);
        }
        json.add("dimensions", dimensions);
        JsonArray followUps = new JsonArray();
        followUps.add("How do you shard?");
        json.add("followUpQuestions", followUps);
        JsonArray missed = new JsonArray();
        missed.add(expectation);
        json.add("missedExpectations", missed);
        return InterviewFeedback.parse(json.toString());
    }

    @Test
    void serializesThemeAndProblemListDerivedFromCatalog() {
        JsonObject state = render(new View(Set.of(PROBLEM.id()), null, List.of(), null, 0, true, List.of(), "Oops"));

        assertThat(state.getAsJsonObject("theme").get("background").getAsString()).isEqualTo("#000000");
        assertThat(state.getAsJsonObject("theme").get("dark").getAsBoolean()).isTrue();
        assertThat(state.get("targetLevel").getAsString()).isEqualTo("Senior");
        assertThat(state.getAsJsonArray("levels")).hasSize(DesignCatalog.LEVELS.size());
        assertThat(state.getAsJsonArray("tracks")).isNotEmpty();
        assertThat(state.getAsJsonArray("guides")).isNotEmpty();
        JsonArray dimensionSummary = state.getAsJsonArray("dimensionSummary");
        assertThat(dimensionSummary).hasSize(DesignCatalog.DIMENSIONS.size());
        assertThat(dimensionSummary.get(0).getAsJsonObject().get("name").getAsString())
                .isEqualTo(DesignCatalog.DIMENSIONS.get(0));
        assertThat(dimensionSummary.get(0).getAsJsonObject().get("reviewedCount").getAsInt()).isZero();
        assertThat(dimensionSummary.get(0).getAsJsonObject().get("guideId").getAsString()).isNotBlank();
        JsonArray problems = state.getAsJsonArray("problems");
        assertThat(problems).hasSameSizeAs(DesignCatalog.all());
        for (int i = 0; i < problems.size(); i++) {
            JsonObject item = problems.get(i).getAsJsonObject();
            Problem problem = DesignCatalog.all().get(i);
            assertThat(item.get("id").getAsString()).isEqualTo(problem.id());
            assertThat(item.get("title").getAsString()).isEqualTo(problem.title());
            assertThat(item.getAsJsonArray("tracks")).isNotEmpty();
            assertThat(item.get("reviewed").getAsBoolean()).isEqualTo(problem.equals(PROBLEM));
            assertThat(item.get("selected").getAsBoolean()).isFalse();
        }
        assertThat(state.get("selected").isJsonNull()).isTrue();
        assertThat(state.get("feedback").isJsonNull()).isTrue();
        assertThat(state.getAsJsonArray("reviews")).isEmpty();
        assertThat(state.get("stale").getAsBoolean()).isFalse();
        assertThat(state.get("running").getAsBoolean()).isTrue();
        assertThat(state.get("error").getAsString()).isEqualTo("Oops");
    }

    @Test
    void serializesSelectedProblemWithRevealedHintsOnly() {
        JsonObject state = render(view(PROBLEM, 1, null, false));

        JsonObject selected = state.getAsJsonObject("selected");
        assertThat(selected.get("id").getAsString()).isEqualTo(PROBLEM.id());
        assertThat(selected.get("statementHtml").getAsString()).isEqualTo(MarkdownHtml.toHtml(PROBLEM.statement()));
        assertThat(selected.getAsJsonArray("hintsHtml")).hasSize(1);
        assertThat(selected.getAsJsonArray("hintsHtml").get(0).getAsString())
                .isEqualTo(MarkdownHtml.inline(PROBLEM.hints().get(0)));
        assertThat(selected.get("hintCount").getAsInt()).isEqualTo(PROBLEM.hints().size());
        assertThat(selected.getAsJsonArray("attempts").get(0).getAsJsonObject().get("label").getAsString())
                .isEqualTo("Attempt 1");
        assertThat(selected.get("selectedAttempt").getAsString()).isEqualTo("a1");
        assertThat(state.getAsJsonArray("problems").get(0).getAsJsonObject().get("selected").getAsBoolean()).isTrue();
        assertThat(state.get("error").getAsString()).isEmpty();
    }

    @Test
    void clampsHintCount() {
        assertThat(render(view(PROBLEM, -3, null, false)).getAsJsonObject("selected").getAsJsonArray("hintsHtml"))
                .isEmpty();
        assertThat(render(view(PROBLEM, 99, null, false)).getAsJsonObject("selected").getAsJsonArray("hintsHtml"))
                .hasSize(PROBLEM.hints().size());
    }

    @Test
    void neverSerializesExpectations() {
        for (Problem problem : DesignCatalog.all()) {
            String json = PageState.toJson(view(problem, DesignCatalog.HINT_COUNT, null, false), THEME);
            assertThat(json).doesNotContain("expectations");
            for (String expectation : problem.expectations()) {
                assertThat(json).doesNotContain(expectation);
            }
        }
    }

    @Test
    void feedbackExposesOnlyTheMissedExpectationItNames() {
        String missed = PROBLEM.expectations().get(0);
        InterviewFeedback.Stored stored = new InterviewFeedback.Stored(feedbackMissing(missed), "fp", "gpt-x",
                "2024-01-01T00:00:00Z");
        String json = PageState.toJson(view(PROBLEM, 0, stored, true), THEME);
        JsonObject feedback = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("feedback");

        assertThat(feedback.getAsJsonArray("missedExpectations")).hasSize(1);
        assertThat(feedback.getAsJsonArray("missedExpectations").get(0).getAsString()).isEqualTo(missed);
        for (String other : PROBLEM.expectations().subList(1, PROBLEM.expectations().size())) {
            assertThat(json).doesNotContain(other);
        }
        assertThat(feedback.get("parsed").getAsBoolean()).isTrue();
        assertThat(feedback.get("verdict").getAsString()).isEqualTo(DesignCatalog.VERDICTS.get(0));
        assertThat(feedback.get("summary").getAsString()).isEqualTo("Solid <b>start</b>");
        assertThat(feedback.getAsJsonArray("dimensions")).hasSize(DesignCatalog.DIMENSIONS.size());
        assertThat(feedback.getAsJsonArray("followUpQuestions").get(0).getAsString()).isEqualTo("How do you shard?");
        assertThat(feedback.get("model").getAsString()).isEqualTo("gpt-x");
        assertThat(feedback.get("createdAt").getAsString()).isEqualTo("2024-01-01T00:00:00Z");
        assertThat(JsonParser.parseString(json).getAsJsonObject().get("stale").getAsBoolean()).isTrue();
    }

    @Test
    void serializesRawFallback() {
        InterviewFeedback.Stored stored = new InterviewFeedback.Stored(InterviewFeedback.parse("not json"), "", "", "");
        JsonObject feedback = render(view(PROBLEM, 0, stored, false)).getAsJsonObject("feedback");

        assertThat(feedback.get("parsed").getAsBoolean()).isFalse();
        assertThat(feedback.get("raw").getAsString()).isEqualTo("not json");
        assertThat(feedback.getAsJsonArray("dimensions")).isEmpty();
        assertThat(feedback.get("stale").getAsBoolean()).isFalse();
    }

    @Test
    void serializesOrderedReviewHistoryWithPerReviewStaleness() {
        InterviewFeedback.Stored newest = new InterviewFeedback.Stored(
                InterviewFeedback.parse("latest raw"), "fp2", "m2", "2026-02-01T00:00:00Z");
        InterviewFeedback.Stored older = new InterviewFeedback.Stored(
                InterviewFeedback.parse("older raw"), "fp1", "m1", "2026-01-01T00:00:00Z");
        View view = new View(Set.of(PROBLEM.id()), PROBLEM, List.of(new AttemptInfo("a1", "Attempt 1")), "a1", 0,
                false, List.of(new Review(newest, false), new Review(older, true)), null);

        JsonObject state = render(view);
        assertThat(view.feedback()).isEqualTo(newest);
        assertThat(view.stale()).isFalse();
        JsonArray reviews = state.getAsJsonArray("reviews");
        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getAsJsonObject().get("raw").getAsString()).isEqualTo("latest raw");
        assertThat(reviews.get(0).getAsJsonObject().get("stale").getAsBoolean()).isFalse();
        assertThat(reviews.get(1).getAsJsonObject().get("raw").getAsString()).isEqualTo("older raw");
        assertThat(reviews.get(1).getAsJsonObject().get("stale").getAsBoolean()).isTrue();
        assertThat(state.get("stale").getAsBoolean()).isFalse();
    }

    @Test
    void serializesNonZeroDimensionReadinessSummaries() {
        List<PageState.DimensionSummary> summaries = List.of(
                new PageState.DimensionSummary(
                        "Capacity estimates", 2.7, 3, "numbers-that-matter", "Back-of-the-Envelope Numbers"));
        View view = new View(
                Map.of(PROBLEM.id(), "Hire"), summaries, null, List.of(), "", 0, "Senior", false, List.of(), "");

        JsonArray serialized = render(view).getAsJsonArray("dimensionSummary");
        assertThat(serialized).hasSize(1);
        JsonObject item = serialized.get(0).getAsJsonObject();
        assertThat(item.get("name").getAsString()).isEqualTo("Capacity estimates");
        assertThat(item.get("averageScore").getAsDouble()).isEqualTo(2.7);
        assertThat(item.get("reviewedCount").getAsInt()).isEqualTo(3);
        assertThat(item.get("guideId").getAsString()).isEqualTo("numbers-that-matter");
        assertThat(item.get("guideTitle").getAsString()).isEqualTo("Back-of-the-Envelope Numbers");
    }

    @Test
    void sampleStateUsesCatalogDimensionsAndVerdicts() throws IOException {
        String sampleFile = System.getProperty("design.web.sample");
        assertThat(sampleFile).as("design.web.sample is set by the Gradle test task").isNotBlank();
        String sample = Files.readString(Path.of(sampleFile), StandardCharsets.UTF_8);

        for (String dimension : DesignCatalog.DIMENSIONS) {
            assertThat(sample).contains("'" + dimension + "'");
        }
        assertThat(DesignCatalog.VERDICTS).anyMatch(verdict -> sample.contains("verdict: '" + verdict + "'"));
    }
}
