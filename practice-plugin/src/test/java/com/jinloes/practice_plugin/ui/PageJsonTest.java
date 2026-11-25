package com.jinloes.practice_plugin.ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.catalog.TrackCatalog;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PageJsonTest {
    private static final PageJson.Theme THEME = new PageJson.Theme(true, "#2b2d30", "#dfe1e5", "#868a91",
            "#393b40", "#589df6", "\"Inter\", system-ui, sans-serif", "\"JetBrains Mono\", monospace", 13);

    @Test
    void carriesTheThemeForEveryPage() {
        JsonObject theme = render(new PracticePage.Problem(null, Set.of())).getAsJsonObject("theme");

        assertThat(theme.get("dark").getAsBoolean()).isTrue();
        assertThat(theme.get("background").getAsString()).isEqualTo("#2b2d30");
        assertThat(theme.get("accent").getAsString()).isEqualTo("#589df6");
        assertThat(theme.get("codeFontFamily").getAsString()).isEqualTo("\"JetBrains Mono\", monospace");
        assertThat(theme.get("fontSize").getAsInt()).isEqualTo(13);
    }

    @Test
    void rendersAProblemWithItsExamplesProgressAndMemberships() {
        var exercise = ExerciseCatalog.find("merge-sorted-lists");

        JsonObject page = render(new PracticePage.Problem(exercise, Set.of(exercise.id()), true, false))
                .getAsJsonObject("page");
        JsonObject problem = page.getAsJsonObject("problem");

        assertThat(page.get("kind").getAsString()).isEqualTo("problem");
        assertThat(problem.get("id").getAsString()).isEqualTo(exercise.id());
        assertThat(problem.get("title").getAsString()).isEqualTo(exercise.title());
        assertThat(problem.getAsJsonArray("topics").asList().stream().map(e -> e.getAsString()).toList())
                .containsExactlyElementsOf(exercise.topics());
        assertThat(problem.get("difficulty").getAsString()).isEqualTo(exercise.difficulty());
        assertThat(problem.get("passed").getAsBoolean()).isTrue();
        assertThat(problem.get("statement").getAsString()).isEqualTo(exercise.statement());
        assertThat(problem.getAsJsonArray("examples")).hasSize(exercise.examples().size());
        assertThat(problem.getAsJsonArray("examples").get(0).getAsJsonObject().get("input").getAsString())
                .isEqualTo(exercise.examples().get(0).input());
        assertThat(problem.get("inputSyntax").getAsString()).isEqualTo(exercise.inputSyntax());
        assertThat(problem.get("sampleInput").getAsString()).isEqualTo(exercise.sampleInput());
        assertThat(problem.get("hasAttempt").getAsBoolean()).isTrue();
        assertThat(problem.get("canRunExamples").getAsBoolean()).isFalse();
        assertThat(ids(problem, "guides"))
                .containsExactlyElementsOf(GuideCatalog.containing(exercise.id()).stream().map(GuideCatalog.Guide::id).toList());
        assertThat(ids(problem, "tracks"))
                .containsExactlyElementsOf(TrackCatalog.containing(exercise.id()).stream().map(GuideCatalog.Guide::id).toList());
    }

    @Test
    void sendsANullProblemWhenNothingIsSelected() {
        JsonObject page = render(new PracticePage.Problem(null, Set.of())).getAsJsonObject("page");

        assertThat(page.get("problem").isJsonNull()).as("the empty state is an explicit null").isTrue();
    }

    @Test
    void rendersAGuideByLevelAndMarksSelectedAndPassedExercises() {
        var guide = GuideCatalog.find("stacks-queues");
        var selected = guide.exercisesAt("Easy").get(0);

        JsonObject body = render(new PracticePage.Study(guide, selected, Set.of(selected.id())))
                .getAsJsonObject("page").getAsJsonObject("guide");

        assertThat(body.get("kind").getAsString()).isEqualTo("guide");
        assertThat(body.get("summary").getAsString()).isEqualTo(guide.summary());
        assertThat(body.get("lesson").getAsString()).isEqualTo(guide.lesson());
        var levels = body.getAsJsonArray("levels");
        assertThat(levels).hasSize(GuideCatalog.DIFFICULTIES.size());
        for (int index = 0; index < levels.size(); index++) {
            JsonObject level = levels.get(index).getAsJsonObject();
            String difficulty = GuideCatalog.DIFFICULTIES.get(index);
            assertThat(level.get("level").getAsString()).isEqualTo(difficulty);
            assertThat(level.getAsJsonArray("exercises"))
                    .as("%s exercises in guide order", difficulty)
                    .hasSize(guide.exercisesAt(difficulty).size());
        }
        JsonObject first = levels.get(0).getAsJsonObject().getAsJsonArray("exercises").get(0).getAsJsonObject();
        assertThat(first.get("id").getAsString()).isEqualTo(selected.id());
        assertThat(first.get("selected").getAsBoolean()).isTrue();
        assertThat(first.get("passed").getAsBoolean()).isTrue();
    }

    @Test
    void labelsPracticeTracksAsTracks() {
        JsonObject body = render(new PracticePage.Study(TrackCatalog.find("google"), null, Set.of()))
                .getAsJsonObject("page").getAsJsonObject("guide");

        assertThat(body.get("kind").getAsString()).isEqualTo("track");
    }

    @Test
    void withoutAGuideRendersTheChooserWithTheSelectedExercisesMemberships() {
        JsonObject empty = render(new PracticePage.Study(null, null, Set.of())).getAsJsonObject("page");
        assertThat(empty.get("kind").getAsString()).isEqualTo("chooser");
        assertThat(empty.get("selected").isJsonNull()).isTrue();

        JsonObject selected = render(new PracticePage.Study(null, ExerciseCatalog.find("course-schedule"), Set.of()))
                .getAsJsonObject("page").getAsJsonObject("selected");
        assertThat(selected.get("title").getAsString()).isEqualTo("Course Schedule");
        assertThat(ids(selected, "tracks")).containsExactly("blind-75", "neetcode-150", "google");
    }

    @Test
    void escapesMarkupSoTheStateIsSafeToEmbedInAScript() {
        String json = PageJson.toJson(new PracticePage.Problem(ExerciseCatalog.find("merge-sorted-lists"), Set.of()),
                THEME);

        assertThat(json).as("no raw tag can close the injecting script").doesNotContain("</").doesNotContain("<p>");
    }

    @Test
    void rendersCoachPageWithTurnsAndNullExerciseState() {
        var exercise = ExerciseCatalog.find("pair-sum");
        JsonObject coachPage = render(new PracticePage.Coach(
                exercise, "att-1", Set.of(), true, "", false, "Failed: wrong answer", true, "", "gpt-5",
                java.util.List.of(
                        new PracticePage.CoachTurn("user", "Translate my approach", "2026-01-01T00:00:00Z"),
                        new PracticePage.CoachTurn("assistant", "## Step 1\n```java\nint x = 1;\n```",
                                "2026-01-01T00:00:02Z"))))
                .getAsJsonObject("page");

        assertThat(coachPage.get("kind").getAsString()).isEqualTo("coach");
        JsonObject coach = coachPage.getAsJsonObject("coach");
        assertThat(coach.get("exerciseId").getAsString()).isEqualTo("pair-sum");
        assertThat(coach.get("exerciseTitle").getAsString()).isEqualTo(exercise.title());
        assertThat(coach.get("attemptId").getAsString()).isEqualTo("att-1");
        assertThat(coach.get("unlocked").getAsBoolean()).isTrue();
        assertThat(coach.get("stale").getAsBoolean()).isTrue();
        assertThat(coach.get("modelLabel").getAsString()).isEqualTo("gpt-5");
        assertThat(coach.getAsJsonArray("turns")).hasSize(2);
        assertThat(coach.getAsJsonArray("turns").get(1).getAsJsonObject().get("markdown").getAsString())
                .isEqualTo("## Step 1\n```java\nint x = 1;\n```");

        JsonObject emptyCoach = render(new PracticePage.Coach(
                null, null, Set.of(), false, "Run Check first.", false, "", false, "", "CLI default", java.util.List.of()))
                .getAsJsonObject("page").getAsJsonObject("coach");
        assertThat(emptyCoach.get("exerciseId").isJsonNull()).isTrue();
        assertThat(emptyCoach.get("exerciseTitle").isJsonNull()).isTrue();
        assertThat(emptyCoach.get("attemptId").isJsonNull()).isTrue();
        assertThat(emptyCoach.get("unlocked").getAsBoolean()).isFalse();
        assertThat(emptyCoach.get("stale").getAsBoolean()).isFalse();
    }

    @Test
    void rendersHintsPageWithLockedAndRevealedStates() {
        var exercise = ExerciseCatalog.find("pair-sum");
        JsonObject locked = render(new PracticePage.Hints(exercise, Set.of(), false, 2))
                .getAsJsonObject("page").getAsJsonObject("hints");
        assertThat(locked.get("exerciseTitle").getAsString()).isEqualTo(exercise.title());
        assertThat(locked.get("hasAttempt").getAsBoolean()).isFalse();
        assertThat(locked.get("revealedCount").getAsInt()).isZero();
        assertThat(locked.getAsJsonArray("revealedHints")).isEmpty();

        JsonObject revealed = render(new PracticePage.Hints(exercise, Set.of(), true, 1))
                .getAsJsonObject("page").getAsJsonObject("hints");
        assertThat(revealed.get("hasAttempt").getAsBoolean()).isTrue();
        assertThat(revealed.get("revealedCount").getAsInt()).isEqualTo(1);
        assertThat(revealed.get("totalCount").getAsInt()).isEqualTo(exercise.hints().size());
        assertThat(revealed.getAsJsonArray("revealedHints").get(0).getAsString())
                .isEqualTo(exercise.hints().get(0));

        JsonObject empty = render(new PracticePage.Hints(null, Set.of(), false, 0))
                .getAsJsonObject("page").getAsJsonObject("hints");
        assertThat(empty.get("exerciseTitle").isJsonNull()).isTrue();
    }

    @Test
    void rendersResultsPageWithStatusTimestampsAndOutput() {
        var page = new PracticePage.Results(
                "passed", "Passed", "2026-11-24T21:34:55Z", "2026-11-24T21:34:55Z",
                true, "edited", true, "All 12 supplied cases passed.");
        assertThat(page.passed()).isEmpty();

        JsonObject rendered = render(page).getAsJsonObject("page");
        assertThat(rendered.get("kind").getAsString()).isEqualTo("results");
        JsonObject results = rendered.getAsJsonObject("results");
        assertThat(results.get("tone").getAsString()).isEqualTo("passed");
        assertThat(results.get("banner").getAsString()).isEqualTo("Passed");
        assertThat(results.get("checkedAt").getAsString()).isEqualTo("2026-11-24T21:34:55Z");
        assertThat(results.get("lastPassedAt").getAsString()).isEqualTo("2026-11-24T21:34:55Z");
        assertThat(results.get("stale").getAsBoolean()).isTrue();
        assertThat(results.get("staleReason").getAsString()).isEqualTo("edited");
        assertThat(results.get("askCoachEnabled").getAsBoolean()).isTrue();
        assertThat(results.get("output").getAsString()).isEqualTo("All 12 supplied cases passed.");
    }

    private static JsonObject render(PracticePage page) {
        return JsonParser.parseString(PageJson.toJson(page, THEME)).getAsJsonObject();
    }

    private static java.util.List<String> ids(JsonObject owner, String member) {
        return owner.getAsJsonArray(member).asList().stream()
                .map(link -> link.getAsJsonObject().get("id").getAsString())
                .toList();
    }
}
