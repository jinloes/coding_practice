package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/** What a {@link PageView} shows: Problem, Study Guide, Hints, or AI Coach tab, with the learner's progress. */
sealed interface PracticePage {
    Set<String> passed();

    /** The selected exercise's statement and action state, or the empty state when {@code exercise} is null. */
    record Problem(Exercise exercise, Set<String> passed, boolean hasAttempt, boolean canRunExamples)
            implements PracticePage {
        public Problem {
            passed = Set.copyOf(Objects.requireNonNull(passed, "passed"));
        }

        Problem(Exercise exercise, Set<String> passed) {
            this(exercise, passed, false, false);
        }
    }

    /**
     * A study guide or practice track with its problems, or, when {@code guide} is null, which guides and
     * tracks include {@code selected}. Either may be null.
     */
    record Study(Guide guide, Exercise selected, Set<String> passed) implements PracticePage {
        public Study {
            passed = Set.copyOf(Objects.requireNonNull(passed, "passed"));
        }
    }

    /** Progressive hints for the selected exercise and attempt. */
    record Hints(Exercise exercise, Set<String> passed, boolean hasAttempt, int revealedCount)
            implements PracticePage {
        public Hints {
            passed = Set.copyOf(Objects.requireNonNull(passed, "passed"));
            revealedCount = Math.max(revealedCount, 0);
        }
    }

    /** One persisted or newly generated turn in the post-check AI Coach conversation. */
    record CoachTurn(String role, String markdown, String createdAt) {
        public CoachTurn {
            role = Objects.requireNonNullElse(role, "");
            markdown = Objects.requireNonNullElse(markdown, "");
            createdAt = Objects.requireNonNullElse(createdAt, "");
        }
    }

    /** The post-check AI Coach tab state for the selected attempt. */
    record Coach(
            Exercise exercise,
            String attemptId,
            Set<String> passed,
            boolean unlocked,
            String lockReason,
            boolean running,
            String checkSummary,
            boolean stale,
            String error,
            String modelLabel,
            List<CoachTurn> turns) implements PracticePage {
        public Coach {
            attemptId = attemptId == null || attemptId.isBlank() ? null : attemptId;
            passed = Set.copyOf(Objects.requireNonNull(passed, "passed"));
            lockReason = Objects.requireNonNullElse(lockReason, "");
            checkSummary = Objects.requireNonNullElse(checkSummary, "");
            error = Objects.requireNonNullElse(error, "");
            modelLabel = Objects.requireNonNullElse(modelLabel, "");
            turns = turns == null ? List.of() : List.copyOf(turns);
        }
    }

    /** The Results tab state: verdict, timestamps, staleness, raw output, and AI Coach unlock state. */
    record Results(
            String tone,
            String banner,
            String checkedAt,
            String lastPassedAt,
            boolean stale,
            String staleReason,
            boolean askCoachEnabled,
            String output) implements PracticePage {
        public Results {
            tone = Objects.requireNonNullElse(tone, "idle");
            banner = Objects.requireNonNullElse(banner, "");
            checkedAt = Objects.requireNonNullElse(checkedAt, "");
            lastPassedAt = Objects.requireNonNullElse(lastPassedAt, "");
            staleReason = Objects.requireNonNullElse(staleReason, "");
            output = Objects.requireNonNullElse(output, "");
        }

        @Override
        public Set<String> passed() {
            return Set.of();
        }
    }
}
