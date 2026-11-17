package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.run.CheckResult;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlPageViewTest {

    @Test
    void rendersTheProblemWithMetadataMembershipsOrItsEmptyState() {
        var exercise = ExerciseCatalog.find("merge-sorted-lists");
        String unpassed = HtmlPageView.html(new PracticePage.Problem(exercise, Set.of()));
        String passed = HtmlPageView.html(new PracticePage.Problem(exercise, Set.of(exercise.id())));

        assertThat(unpassed)
                .isEqualTo(PracticeViews.problemText(exercise))
                .contains(exercise.title(), exercise.difficulty(), exercise.topic(),
                        exercise.intendedTime(), exercise.intendedSpace(), exercise.inputSyntax(),
                        "Two Pointers", "Linked Lists", "Blind 75")
                .doesNotContain("(passed)");
        assertThat(passed).contains(exercise.title() + " (passed)");
        assertThat(HtmlPageView.html(new PracticePage.Problem(ExerciseCatalog.find("roman-to-integer"), Set.of())))
                .contains("Not part of any guide yet.")
                .doesNotContain("Practice tracks:");
        assertThat(HtmlPageView.html(new PracticePage.Problem(null, Set.of())))
                .isEqualTo("<p>No exercises match these filters.</p>");
    }

    @Test
    void lazyPageViewDefersCreationAndCoalescesHiddenUpdatesUntilActivated() {
        AtomicInteger created = new AtomicInteger();
        List<PracticePage> shown = new ArrayList<>();
        PageView fake = new PageView() {
            private final JLabel label = new JLabel();

            @Override
            public JComponent component() {
                return label;
            }

            @Override
            public void show(PracticePage page) {
                shown.add(page);
            }
        };

        PageView lazy = PageView.lazy(() -> {
            created.incrementAndGet();
            return fake;
        }, false);
        PracticePage first = new PracticePage.Problem(null, Set.of());
        PracticePage second = new PracticePage.Problem(ExerciseCatalog.find("pair-sum"), Set.of("pair-sum"));

        lazy.show(first);
        lazy.show(second);
        assertThat(created).hasValue(0);
        assertThat(shown).isEmpty();

        lazy.setActive(true);
        assertThat(created).hasValue(1);
        assertThat(shown).containsExactly(second);

        lazy.setActive(false);
        lazy.setActive(true);
        assertThat(created).hasValue(1);
        assertThat(shown).as("unchanged state is not re-pushed on tab revisit").containsExactly(second);
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
