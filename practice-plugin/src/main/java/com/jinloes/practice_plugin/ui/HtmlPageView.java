package com.jinloes.practice_plugin.ui;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

/** The Swing HTML fallback for {@link PageView}, used when JCEF is unavailable. Its pages are not interactive. */
final class HtmlPageView implements PageView {
    private PracticePage current;
    private final JEditorPane pane = PracticeViews.htmlPane(() -> current == null ? null : html(current));
    private final JBScrollPane scroll = new JBScrollPane(pane);

    @Override
    public JComponent component() {
        return scroll;
    }

    @Override
    public void show(PracticePage page) {
        current = page;
        PracticeViews.setHtmlBody(pane, html(page));
    }

    static String html(PracticePage page) {
        return switch (page) {
            case PracticePage.Problem problem -> problem.exercise() == null
                    ? "<p>No exercises match these filters. Use Reset filters above the exercise list to show all exercises.</p>"
                    : PracticeViews.problemText(problem.exercise(), problem.passed());
            case PracticePage.Study study -> StudyGuideView.html(study.guide(), study.selected(), study.passed());
            case PracticePage.Hints hints -> hintsHtml(hints);
            case PracticePage.Results results -> resultsHtml(results);
            case PracticePage.Coach coach -> coachHtml(coach);
        };
    }

    private static String resultsHtml(PracticePage.Results results) {
        StringBuilder out = new StringBuilder();
        if (!results.banner().isBlank()) {
            out.append("<h2>").append(MarkdownHtml.inline(results.banner())).append("</h2>");
        }
        if (!results.checkedAt().isBlank() || !results.lastPassedAt().isBlank()) {
            out.append("<p>Last full check: ")
                    .append(MarkdownHtml.inline(results.checkedAt().isBlank() ? "never" : results.checkedAt()))
                    .append(" &middot; Most recent pass: ")
                    .append(MarkdownHtml.inline(results.lastPassedAt().isBlank() ? "never" : results.lastPassedAt()))
                    .append("</p>");
        }
        if (!results.output().isBlank()) {
            out.append("<pre>").append(MarkdownHtml.inline(results.output())).append("</pre>");
        }
        return out.toString();
    }

    private static String hintsHtml(PracticePage.Hints hints) {
        if (hints.exercise() == null) {
            return "<p>Select an exercise to view hints.</p>";
        }
        if (!hints.hasAttempt()) {
            return "<p>Start or resume an attempt to reveal hints.</p>";
        }
        return HintButton.revealedHtml(hints.exercise().hints(), hints.revealedCount());
    }

    private static String coachHtml(PracticePage.Coach coach) {
        StringBuilder out = new StringBuilder();
        String title = coach.exercise() == null ? "AI Coach" : "AI Coach — " + coach.exercise().title();
        out.append("<h2>").append(MarkdownHtml.inline(title)).append("</h2>");
        if (!coach.checkSummary().isBlank()) {
            out.append("<p>").append(MarkdownHtml.inline(coach.checkSummary())).append("</p>");
        }
        if (!coach.unlocked()) {
            out.append("<p>").append(MarkdownHtml.inline(coach.lockReason())).append("</p>");
        } else {
            if (coach.stale()) {
                out.append("<p>Edited since last check — AI Coach uses your current editor code alongside the last check result.</p>");
            }
            out.append("<p>Interactive AI Coach controls require JCEF.</p>");
        }
        if (coach.running()) {
            out.append("<p>AI Coach is thinking...</p>");
        }
        if (!coach.error().isBlank()) {
            out.append("<p>").append(MarkdownHtml.inline(coach.error())).append("</p>");
        }
        for (PracticePage.CoachTurn turn : coach.turns()) {
            String speaker = "assistant".equals(turn.role()) ? "Coach" : "You";
            out.append("<h3>").append(speaker).append("</h3>");
            out.append(MarkdownHtml.toHtml(turn.markdown()));
        }
        return out.toString();
    }
}
