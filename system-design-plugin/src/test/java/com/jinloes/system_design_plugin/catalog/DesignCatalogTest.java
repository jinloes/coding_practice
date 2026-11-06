package com.jinloes.system_design_plugin.catalog;

import com.google.gson.JsonParser;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignCatalogTest {

    @Test
    void loadsEightProblemsThatMatchTheirManifests() throws IOException {
        assertThat(DesignCatalog.all()).hasSize(8);
        assertThat(DesignCatalog.all()).extracting(Problem::id).doesNotHaveDuplicates();
        assertThat(DesignCatalog.all()).extracting(Problem::title).doesNotHaveDuplicates();
        for (Problem problem : DesignCatalog.all()) {
            var manifest = JsonParser.parseString(resource("/problems/" + problem.id() + ".json")).getAsJsonObject();
            assertThat(problem.title()).isEqualTo(manifest.get("title").getAsString());
            assertThat(problem.expectations()).hasSize(manifest.getAsJsonArray("expectations").size())
                    .hasSizeGreaterThanOrEqualTo(DesignCatalog.MIN_EXPECTATIONS);
            assertThat(problem.hints()).hasSize(DesignCatalog.HINT_COUNT);
            assertThat(DesignCatalog.DIFFICULTIES).contains(problem.difficulty());
            assertThat(problem.summary()).isNotBlank();
            assertThat(problem.statement()).isNotBlank();
            assertThat(DesignCatalog.find(problem.id())).isSameAs(problem);
            assertThat(DesignCatalog.exists(problem.id())).isTrue();
        }
    }

    @Test
    void exposesTheFixedRubricAndNotesTemplate() {
        assertThat(DesignCatalog.DIMENSIONS).hasSize(7).doesNotHaveDuplicates();
        assertThat(DesignCatalog.VERDICTS).hasSize(5).doesNotHaveDuplicates();
        assertThat(DesignCatalog.notesTemplate()).startsWith("# Design notes").contains("## Capacity estimates");
    }

    @Test
    void rejectsUnknownIds() {
        assertThat(DesignCatalog.exists("missing")).isFalse();
        assertThatThrownBy(() -> DesignCatalog.find("missing"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing");
    }

    @Test
    void rejectsInvalidManifestsNamingTheProblem() {
        assertThat(DesignCatalog.parse("demo", manifest("demo", "Easy", 3, 5)).id()).as("valid control").isEqualTo("demo");
        assertInvalid(manifest("other", "Easy", 3, 5));
        assertInvalid(manifest("demo", "Expert", 3, 5));
        assertInvalid(manifest("demo", "Easy", 2, 5));
        assertInvalid(manifest("demo", "Easy", 4, 5));
        assertInvalid(manifest("demo", "Easy", 3, 4));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"title\": \"T\"", "\"title\": \" \""));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"hints\": [", "\"hints\": [1, "));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"hints\": [", "\"hints\": [\" \", "));
        assertInvalid(manifest("demo", "Easy", 3, 5).replace("\"expectations\": [", "\"other\": ["));
        assertInvalid("not json");
        assertInvalid("[]");
    }

    @Test
    void missingOrUnreadableResourcesNameThePath() {
        assertThatThrownBy(() -> DesignCatalog.load(path -> null))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("url-shortener");
        assertThatThrownBy(() -> DesignCatalog.read("/problems/rate-limiter.json", path -> new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        })).isInstanceOf(IllegalStateException.class).hasMessageContaining("rate-limiter");
        assertThat(DesignCatalog.read("/x", path -> new ByteArrayInputStream("ok".getBytes(StandardCharsets.UTF_8))))
                .isEqualTo("ok");
    }

    private static void assertInvalid(String json) {
        assertThatThrownBy(() -> DesignCatalog.parse("demo", json))
                .as("manifest %s", json)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("demo");
    }

    private static String manifest(String id, String difficulty, int hints, int expectations) {
        return "{\"id\": \"" + id + "\", \"title\": \"T\", \"difficulty\": \"" + difficulty + "\", "
                + "\"summary\": \"S\", \"statement\": \"Body\", \"hints\": [" + strings("h", hints) + "], "
                + "\"expectations\": [" + strings("e", expectations) + "]}";
    }

    private static String strings(String prefix, int count) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < count; i++) {
            out.append(i == 0 ? "" : ", ").append('"').append(prefix).append(i).append('"');
        }
        return out.toString();
    }

    private static String resource(String path) throws IOException {
        try (var input = DesignCatalogTest.class.getResourceAsStream(path)) {
            assertThat(input).as(path).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
