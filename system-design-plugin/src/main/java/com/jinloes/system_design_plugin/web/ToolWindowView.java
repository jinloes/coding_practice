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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Builds the tool window's {@link PageState.View} from the selected problem, its attempts, progress, and their
 * feedback history. Attempts are ordered by creation time, then ID, and labeled by that order.
 */
public final class ToolWindowView {
    private ToolWindowView() {
    }

    /** One attempt of the selected problem and when it was created. */
    public record AttemptEntry(String id, FileTime created) {
    }

    /** Read access to stored attempts and their feedback history. */
    public interface AttemptStore {
        List<AttemptEntry> attempts(String problemId) throws IOException;

        List<InterviewFeedback.Stored> feedbackHistory(String attemptId) throws IOException;

        String fingerprint(String attemptId) throws IOException;
    }

    public static PageState.View build(String problemId, boolean running, String error, Set<String> reviewedIds,
                                       Function<String, String> selectedAttempt,
                                       ToIntFunction<String> hintsRevealed, AttemptStore store, ZoneId zone) {
        return build(problemId, running, error, reviewedIds, DesignCatalog.DEFAULT_LEVEL,
                selectedAttempt, ignored -> "", hintsRevealed, store, zone);
    }

    public static PageState.View build(String problemId, boolean running, String error, Set<String> reviewedIds,
                                       String targetLevel, Function<String, String> selectedAttempt,
                                       Function<String, String> attemptLabel,
                                       ToIntFunction<String> hintsRevealed, AttemptStore store, ZoneId zone) {
        Map<String, String> verdicts = reviewedIds.stream()
                .collect(Collectors.toUnmodifiableMap(id -> id, ignored -> ""));
        return build(problemId, running, error, verdicts, targetLevel,
                selectedAttempt, attemptLabel, hintsRevealed, store, zone);
    }

    public static PageState.View build(String problemId, boolean running, String error, Map<String, String> verdicts,
                                       String targetLevel, Function<String, String> selectedAttempt,
                                       Function<String, String> attemptLabel,
                                       ToIntFunction<String> hintsRevealed, AttemptStore store, ZoneId zone) {
        String currentError = error;
        String level = DesignCatalog.normalizeLevel(targetLevel);
        if (problemId == null || !DesignCatalog.exists(problemId)) {
            return new PageState.View(verdicts, null, List.of(), "", 0, level, running, List.of(), currentError);
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
            String custom = attemptLabel.apply(attempt.id());
            String normalizedCustom = custom == null ? "" : custom.strip();
            String baseName = normalizedCustom.isEmpty() ? "Attempt " + (index + 1) : normalizedCustom;
            infos.add(new PageState.AttemptInfo(attempt.id(),
                    baseName + " (" + date.format(attempt.created().toInstant()) + ")",
                    normalizedCustom));
        }
        String selectedId = selectedAttempt.apply(problemId);
        Optional<AttemptEntry> selected = attempts.stream()
                .filter(attempt -> attempt.id().equals(selectedId)).findFirst();
        if (selected.isEmpty() && !attempts.isEmpty()) {
            selected = Optional.of(attempts.get(attempts.size() - 1));
        }
        if (selected.isEmpty()) {
            return new PageState.View(verdicts, problem, infos, "", 0, level, running, List.of(), currentError);
        }
        String attemptId = selected.get().id();
        int hints = hintsRevealed.applyAsInt(attemptId);
        List<PageState.Review> reviews = List.of();
        try {
            List<InterviewFeedback.Stored> history = store.feedbackHistory(attemptId);
            if (!history.isEmpty()) {
                String currentFingerprint = currentFingerprint(store, attemptId);
                reviews = history.stream()
                        .map(entry -> new PageState.Review(entry,
                                currentFingerprint != null && !currentFingerprint.equals(entry.fingerprint())))
                        .toList();
            }
        } catch (IOException exception) {
            // Unreadable feedback is shown as no feedback; the store reports the cause.
        }
        return new PageState.View(verdicts, problem, infos, attemptId, hints, level, running, reviews, currentError);
    }

    private static String currentFingerprint(AttemptStore store, String attemptId) {
        try {
            return store.fingerprint(attemptId);
        } catch (IOException exception) {
            return null;
        }
    }
}
