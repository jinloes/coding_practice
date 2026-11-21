package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyStoreAndZeroLimitReturnZeroAndEmptyList() {
        Solution report = new Solution();
        assertThat(report.totalBytes()).isZero();
        assertThat(report.topCollections(3)).isEmpty();

        report.putFile("notes.md", 50, List.of("docs"));
        assertThat(report.topCollections(0)).isEmpty();
    }

    @Test
    void retainsZeroByteCollectionWhileItStillContainsAFile() {
        Solution report = new Solution();
        report.putFile("empty.txt", 0, List.of("scratch"));
        report.putFile("icon.svg", 10, List.of("assets"));

        assertThat(report.totalBytes()).isEqualTo(10L);
        assertThat(report.topCollections(5)).containsExactly("assets", "scratch");
    }

    @Test
    void limitsOutputToRequestedTopKAcrossManyCollections() {
        Solution report = new Solution();
        report.putFile("f1", 40, List.of("delta"));
        report.putFile("f2", 90, List.of("bravo"));
        report.putFile("f3", 90, List.of("alpha"));
        report.putFile("f4", 60, List.of("charlie"));

        assertThat(report.topCollections(3)).containsExactly("alpha", "bravo", "charlie");
    }

    @Test
    void supportsMultiGigabyteFileTotalsAboveThirtyTwoBitRange() {
        Solution report = new Solution();
        long threeGiB = 3L * 1024 * 1024 * 1024;
        report.putFile("backup-1.tar", threeGiB, List.of("backups", "prod"));
        report.putFile("backup-2.tar", threeGiB, List.of("backups"));

        assertThat(report.totalBytes()).isEqualTo(2 * threeGiB);
        assertThat(report.topCollections(2)).containsExactly("backups", "prod");
    }

    @Test
    void rejectsInvalidFilesSizesCollectionsAndNegativeLimit() {
        Solution report = new Solution();
        assertThatThrownBy(() -> report.putFile("", 10, List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> report.putFile(null, 10, List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> report.putFile("a", -1, List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> report.putFile("a", 10, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> report.putFile("a", 10, List.of(" "))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> report.putFile("a", 10, Arrays.asList("ok", null)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> report.topCollections(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}
