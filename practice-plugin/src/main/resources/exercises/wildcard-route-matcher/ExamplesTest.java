package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void prefersExactLiteralOverWildcardRegardlessOfRegistrationOrder() {
        Solution router = new Solution();
        router.register("/projects/*/issues", "listIssues");
        router.register("/projects/core/issues", "coreIssues");

        assertThat(router.match("/projects/core/issues")).isEqualTo("coreIssues");
        assertThat(router.match("/projects/ops/issues")).isEqualTo("listIssues");
    }

    @Test
    void breaksTiesAtEarliestDifferingSegmentFromLeftToRight() {
        Solution router = new Solution();
        router.register("/api/*/members", "wildcardFirst");
        router.register("/api/v1/*", "literalFirst");

        assertThat(router.match("/api/v1/members")).isEqualTo("literalFirst");
    }

    @Test
    void resolvesRootRouteAndReturnsEmptyStringWhenUnmatched() {
        Solution router = new Solution();
        router.register("/", "home");

        assertThat(router.match("/")).isEqualTo("home");
        assertThat(router.match("/unknown")).isEmpty();
    }
}
