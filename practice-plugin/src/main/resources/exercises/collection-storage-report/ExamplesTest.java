package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsSharedAndUntaggedFilesOnceInTotalBytes() {
        Solution report = new Solution();
        report.putFile("report.pdf", 200, List.of("finance", "audit"));
        report.putFile("chart.png", 150, List.of("finance"));
        report.putFile("raw.csv", 100, List.of());

        assertThat(report.totalBytes()).isEqualTo(450L);
        assertThat(report.topCollections(2)).containsExactly("finance", "audit");
    }

    @Test
    void deduplicatesTagsOnOneFileAndBreaksTiesAlphabetically() {
        Solution report = new Solution();
        report.putFile("a.log", 300, List.of("ops", "ops"));
        report.putFile("b.log", 300, List.of("eng"));

        assertThat(report.totalBytes()).isEqualTo(600L);
        assertThat(report.topCollections(2)).containsExactly("eng", "ops");
    }

    @Test
    void replacingAnExistingFileUpdatesTotalsAndEvictsEmptyCollections() {
        Solution report = new Solution();
        report.putFile("build.zip", 500, List.of("legacy"));
        report.putFile("build.zip", 120, List.of("release"));

        assertThat(report.totalBytes()).isEqualTo(120L);
        assertThat(report.topCollections(2)).containsExactly("release");
    }
}
