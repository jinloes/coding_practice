package com.jinloes.system_design_plugin.catalog;

import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.catalog.GuideCatalog.Guide;
import com.jinloes.system_design_plugin.catalog.TrackCatalog.Track;
import com.jinloes.system_design_plugin.web.MarkdownHtml;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrackAndGuideCatalogTest {

    @Test
    void loadsEveryTrackManifestAndCoversAllProblems() throws IOException {
        List<String> manifests = manifestIds("design.tracks.dir");
        assertThat(TrackCatalog.all()).extracting(Track::id).containsExactlyInAnyOrderElementsOf(manifests);
        assertThat(TrackCatalog.all()).extracting(Track::title).doesNotHaveDuplicates();
        for (Track track : TrackCatalog.all()) {
            assertThat(TrackCatalog.find(track.id())).isSameAs(track);
            assertThat(track.summary()).isNotBlank();
            assertThat(track.problemIds()).isNotEmpty().doesNotHaveDuplicates();
            for (String problemId : track.problemIds()) {
                assertThat(DesignCatalog.exists(problemId)).isTrue();
                assertThat(TrackCatalog.containing(problemId)).contains(track.id());
            }
        }
        for (Problem problem : DesignCatalog.all()) {
            assertThat(TrackCatalog.containing(problem.id()))
                    .as("every problem belongs to at least one track: %s", problem.id())
                    .isNotEmpty();
        }
        assertThat(TrackCatalog.containing("missing-problem")).isEmpty();
        assertThatThrownBy(() -> TrackCatalog.find("missing-track"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing-track");
    }

    @Test
    void loadsEveryGuideManifestAndRendersValidHtml() throws IOException {
        List<String> manifests = manifestIds("design.guides.dir");
        assertThat(GuideCatalog.all()).extracting(Guide::id).containsExactlyInAnyOrderElementsOf(manifests);
        assertThat(GuideCatalog.all()).extracting(Guide::title).doesNotHaveDuplicates();
        for (Guide guide : GuideCatalog.all()) {
            assertThat(GuideCatalog.find(guide.id())).isSameAs(guide);
            assertThat(guide.summary()).isNotBlank();
            assertThat(MarkdownHtml.toHtml(guide.body())).contains("<h4>").contains("<p>");
            assertThat(guide.relatedProblemIds()).isNotEmpty().doesNotHaveDuplicates();
            guide.relatedProblemIds().forEach(id -> assertThat(DesignCatalog.exists(id)).isTrue());
        }
        assertThatThrownBy(() -> GuideCatalog.find("missing-guide"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing-guide");
    }

    @Test
    void rejectsInvalidTrackAndGuideManifests() {
        String validTrack = "{\"id\":\"t\",\"title\":\"T\",\"summary\":\"S\",\"problemIds\":[\"p1\"]}";
        assertThat(TrackCatalog.parse("t", validTrack, "p1"::equals).id()).isEqualTo("t");
        assertThatThrownBy(() -> TrackCatalog.parse("t", validTrack.replace("\"t\"", "\"other\""), "p1"::equals))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> TrackCatalog.parse("t", validTrack.replace("[\"p1\"]", "[]"), "p1"::equals))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> TrackCatalog.parse("t", validTrack.replace("[\"p1\"]", "[\"p1\",\"p1\"]"), "p1"::equals))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> TrackCatalog.parse("t", validTrack, ignored -> false))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("p1");

        String validGuide = "{\"id\":\"g\",\"title\":\"G\",\"summary\":\"S\",\"body\":\"Body\",\"relatedProblemIds\":[\"p1\"]}";
        assertThat(GuideCatalog.parse("g", validGuide, "p1"::equals).id()).isEqualTo("g");
        assertThatThrownBy(() -> GuideCatalog.parse("g", validGuide.replace("\"g\"", "\"other\""), "p1"::equals))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> GuideCatalog.parse("g", validGuide, ignored -> false))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("p1");
    }

    private static List<String> manifestIds(String property) throws IOException {
        String dir = System.getProperty(property);
        assertThat(dir).as(property).isNotBlank();
        try (var files = Files.list(Path.of(dir))) {
            return files.map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".json"))
                    .map(name -> name.substring(0, name.length() - ".json".length()))
                    .toList();
        }
    }
}
