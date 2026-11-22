package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;

import java.util.Objects;
import java.util.Set;

/** What a {@link PageView} shows: the Problem tab or the Study Guide tab, with the learner's progress. */
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
}
