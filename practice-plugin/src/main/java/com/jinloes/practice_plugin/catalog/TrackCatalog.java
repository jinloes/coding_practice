package com.jinloes.practice_plugin.catalog;

import com.jinloes.practice_plugin.catalog.GuideCatalog.Guide;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Practice tracks: curated lists of bundled exercises, such as original versions of a widely shared
 * interview list or the patterns commonly reported in one company's interviews, plus notes on what to
 * expect. Tracks are hand curated, not official or verified question lists. A track uses the study
 * guide manifest schema and validation, so it is represented as a {@link Guide} whose lesson holds the
 * track notes.
 */
public final class TrackCatalog {
    /** Which tracks exist, in the order the UI lists them. */
    private static final List<String> IDS = List.of("blind-75", "google");

    private static final List<Guide> TRACKS = IDS.stream()
            .map(id -> GuideCatalog.parse(id, read("/tracks/" + id + ".json"), ExerciseCatalog::find))
            .toList();

    private static final Map<String, Guide> BY_ID = TRACKS.stream()
            .collect(Collectors.toUnmodifiableMap(Guide::id, track -> track));

    private TrackCatalog() {
    }

    public static List<Guide> all() {
        return TRACKS;
    }

    public static Guide find(String id) {
        Guide track = BY_ID.get(id);
        if (track == null) {
            throw new IllegalArgumentException("Unknown track ID: " + id);
        }
        return track;
    }

    /** The tracks that include {@code exerciseId}, in track order. */
    public static List<Guide> containing(String exerciseId) {
        return TRACKS.stream()
                .filter(track -> track.exercises().stream().anyMatch(exercise -> exercise.id().equals(exerciseId)))
                .toList();
    }

    private static String read(String path) {
        try (InputStream input = TrackCatalog.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Missing track resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read track resource: " + path, exception);
        }
    }
}
