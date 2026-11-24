package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void billsOpenEndedSubscriptionFromStartMonthThroughDecember() {
        List<Solution.Subscription> subs = List.of(
                new Solution.Subscription("Jira", 1000L, "2025-03-15", null));
        assertThat(solution.monthlyCostReport(subs, 2025))
                .containsExactly(0, 0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000);
    }

    @Test
    void sumsOverlappingBoundedSubscriptionsAcrossYearBoundary() {
        List<Solution.Subscription> subs = List.of(
                new Solution.Subscription("Jira", 1000L, "2024-11-01", "2025-02-10"),
                new Solution.Subscription("Confluence", 500L, "2025-02-20", "2025-04-01"));
        assertThat(solution.monthlyCostReport(subs, 2025))
                .containsExactly(1000, 1500, 500, 500, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    void returnsAllZerosWhenSubscriptionsStartAfterTargetYear() {
        List<Solution.Subscription> subs = List.of(
                new Solution.Subscription("Jira", 1000L, "2026-01-01", null));
        assertThat(solution.monthlyCostReport(subs, 2025))
                .containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
