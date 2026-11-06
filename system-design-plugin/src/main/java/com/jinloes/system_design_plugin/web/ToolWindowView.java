package com.jinloes.system_design_plugin.web;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Builds the tool window's {@link PageState.View} from the selected problem, its attempts, progress, and the
 * latest feedback. Attempts are ordered by creation time, then ID, and labeled by that order.
 */
public final class ToolWindowView {
    private ToolWindowView() {
    }

    /** One attempt of the selected problem and when it was created. */
    public record AttemptEntry(String id, FileTime created) {
    }

    /** Read access to stored attempts and their feedback. */
    public interface AttemptStore {
        List<AttemptEntry> attempts(String problemId) throws IOException;

        Optional<InterviewFeedback.Stored> latestFeedback(String attemptId) throws IOException;

        boolean isStale(String attemptId, String fingerprint) throws IOException;
    }

    public static PageState.View build(String problemId, boolean running, String error, Set<String> reviewedIds,
                                       Function<String, String> selectedAttempt,
                                       ToIntFunction<String> hintsRevealed, AttemptStore store, ZoneId zone) {
        String currentError = error;
        if (problemId == null || !DesignCatalog.exists(problemId)) {
            return new PageState.View(reviewedIds, null, List.of(), "", 0, running, null, false, currentError);
        }
        Problem problem = DesignCatalog.find(problemId);
        List<AttemptEntry> attempts;
        try {
            attempts = new ArrayList<>(store.attempts(problemId));
        } catch (IOException exception) {
            attempts = new ArrayList<>();
            currentError = currentError.isEmpty() ? "Could not list attempts: " + exception.getMessage() : currentError;
        }
        attempts.sort(Comparator.comparing(AttemptEntry::created).thenComparing(AttemptEntry::id));
        DateTimeFormatter date = DateTimeFormatter.ofPattern("MMM d, HH:mm").withZone(zone);
        List<PageState.AttemptInfo> infos = new ArrayList<>();
        for (int index = 0; index < attempts.size(); index++) {
            AttemptEntry attempt = attempts.get(index);
            infos.add(new PageState.AttemptInfo(attempt.id(),
                    "Attempt " + (index + 1) + " (" + date.format(attempt.created().toInstant()) + ")"));
        }
        String selectedId = selectedAttempt.apply(problemId);
        Optional<AttemptEntry> selected = attempts.stream()
                .filter(attempt -> attempt.id().equals(selectedId)).findFirst();
        if (selected.isEmpty() && !attempts.isEmpty()) {
            selected = Optional.of(attempts.get(attempts.size() - 1));
        }
        if (selected.isEmpty()) {
            return new PageState.View(reviewedIds, problem, infos, "", 0, running, null, false, currentError);
        }
        String attemptId = selected.get().id();
        int hints = hintsRevealed.applyAsInt(attemptId);
        InterviewFeedback.Stored feedback = null;
        boolean stale = false;
        try {
            feedback = store.latestFeedback(attemptId).orElse(null);
            stale = feedback != null && store.isStale(attemptId, feedback.fingerprint());
        } catch (IOException exception) {
            // Unreadable feedback is shown as no feedback; the store reports the cause.
        }
        return new PageState.View(reviewedIds, problem, infos, attemptId, hints, running, feedback, stale,
                currentError);
    }
}
