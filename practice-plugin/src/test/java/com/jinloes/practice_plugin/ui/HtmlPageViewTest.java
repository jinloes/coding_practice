package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.run.CheckResult;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlPageViewTest {

    @Test
    void rendersTheProblemOrItsEmptyState() {
        var exercise = ExerciseCatalog.find("pair-sum");

        assertThat(HtmlPageView.html(new PracticePage.Problem(exercise, Set.of())))
                .isEqualTo(PracticeViews.problemText(exercise));
        assertThat(HtmlPageView.html(new PracticePage.Problem(null, Set.of())))
                .isEqualTo("<p>No exercises match these filters.</p>");
    }

    @Test
    void rendersTheStudyGuideWithTheSharedRenderer() {
        var guide = GuideCatalog.find("graphs");

        assertThat(HtmlPageView.html(new PracticePage.Study(guide, null, Set.of("course-schedule"))))
                .isEqualTo(StudyGuideView.html(guide, null, Set.of("course-schedule")));
    }

    @Test
    void resultsBannerHasAReadableLabelForEveryCheckStatus() {
        for (CheckResult.Status status : CheckResult.Status.values()) {
            assertThat(ResultsView.label(status.name()))
                    .as(status.name())
                    .isNotBlank()
                    .doesNotContain("_");
        }
        assertThat(ResultsView.label(null)).isEqualTo("Not checked yet");
        assertThat(ResultsView.label("PASSED")).isEqualTo("Passed");
    }
}
