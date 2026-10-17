package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.TrackCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StudyGuideViewTest {

    @Test
    void rendersTheLessonAndEachLevelWithPassCounts() {
        var guide = GuideCatalog.find("stacks-queues");

        String html = StudyGuideView.html(guide, null, Set.of("array-stack", "pair-sum"));

        assertThat(html)
                .contains("<h2>Stacks and Queues</h2>")
                .contains("<p><i>" + MarkdownHtml.inline(guide.summary()) + "</i></p>")
                .contains(MarkdownHtml.toHtml(guide.lesson()))
                .contains("<h3>Easy (0/1 passed)</h3><ul><li>Balanced Delimiters</li></ul>")
                .contains("<h3>Medium (1/9 passed)</h3><ul><li>Array Stack (passed)</li>"
                        + "<li>Min-Tracking Stack</li><li>Postfix Evaluation</li><li>Warmer Day Wait</li>"
                        + "<li>Car Fleets</li><li>Max-Tracking Queue</li><li>Rolling Hit Counter</li>"
                        + "<li>Sort Stack With Stack</li><li>Sunset View Buildings</li></ul>")
                .contains("<h3>Hard (0/3 passed)</h3><ul><li>Sliding Window Maximum</li>"
                        + "<li>Largest Histogram Rectangle</li><li>Max Stack With Pop Max</li></ul>");
        assertThat(html.indexOf("<h3>Easy")).isLessThan(html.indexOf("<h3>Medium"));
        assertThat(html.indexOf("<h3>Medium")).isLessThan(html.indexOf("<h3>Hard"));
    }

    @Test
    void marksOnlyPassedExercises() {
        String html = StudyGuideView.html(GuideCatalog.find("stacks-queues"), null, Set.of());

        assertThat(html).as("nothing passed, nothing marked").doesNotContain("(passed)</li>");
    }

    @Test
    void saysWhenALevelHasNoProblemsYet() {
        String html = StudyGuideView.html(GuideCatalog.find("graphs"), null, Set.of("course-schedule"));

        assertThat(html)
                .contains("<h3>Easy (0/0 passed)</h3><p>No Easy problems in this guide yet.</p>")
                .contains("<h3>Hard (1/7 passed)</h3><ul><li>Course Schedule (passed)</li>"
                        + "<li>Alien Alphabet Order</li><li>Word Ladder Length</li>"
                        + "<li>Itinerary Reconstruction</li><li>Rising Water Path</li>"
                        + "<li>Grid Obstacle Shortest Path</li><li>Grouped Dependency Order</li></ul>");
    }

    @Test
    void withoutAGuideListsTheGuidesThatIncludeTheSelectedExercise() {
        assertThat(StudyGuideView.html(null, null, Set.of()))
                .isEqualTo("<p>Choose a guide above to study a topic, or a track to practice its list.</p>");
        assertThat(StudyGuideView.html(null, ExerciseCatalog.find("merge-sorted-lists"), Set.of()))
                .contains("<p>Merge Sorted Lists appears in: Two Pointers, Linked Lists.</p>")
                .contains("<p>Practice tracks: Blind 75, NeetCode 150.</p>");
        assertThat(StudyGuideView.html(null, ExerciseCatalog.find("roman-to-integer"), Set.of()))
                .contains("<p>Roman to Integer is not part of any guide yet.</p>")
                .doesNotContain("Practice tracks");
        assertThat(StudyGuideView.html(null, ExerciseCatalog.find("course-schedule"), Set.of()))
                .contains("<p>Practice tracks: Blind 75, NeetCode 150, Google.</p>");
    }

    @Test
    void rendersAPracticeTrackWithItsNotesAndLevels() {
        var track = TrackCatalog.find("google");

        String html = StudyGuideView.html(track, null, Set.of("course-schedule"));

        assertThat(html)
                .contains("<h2>Google</h2>")
                .contains(MarkdownHtml.toHtml(track.lesson()))
                .contains("<li>Course Schedule (passed)</li>");
    }
}
