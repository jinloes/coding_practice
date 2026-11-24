package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    private final Solution solution = new Solution();

    @Test
    void ignoresSubscriptionsThatEndedBeforeTargetYear() {
        List<Solution.Subscription> subs = List.of(
                new Solution.Subscription("Jira", 2000L, "2023-01-01", "2024-12-31"));
        assertThat(solution.monthlyCostReport(subs, 2025))
                .containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    void billsFullYearWhenSubscriptionSpansBeforeAndAfterTargetYear() {
        List<Solution.Subscription> subs = List.of(
                new Solution.Subscription("Bitbucket", 750L, "2024-06-15", "2026-03-01"));
        assertThat(solution.monthlyCostReport(subs, 2025))
                .containsExactly(750, 750, 750, 750, 750, 750, 750, 750, 750, 750, 750, 750);
    }

    @Test
    void handlesSingleDaySubscriptionAndLargeCentsWithoutOverflow() {
        List<Solution.Subscription> subs = List.of(
                new Solution.Subscription("Enterprise", 3_000_000_000L, "2025-07-31", "2025-07-31"),
                new Solution.Subscription("Support", 3_000_000_000L, "2025-07-01", "2025-07-01"));
        long[] report = solution.monthlyCostReport(subs, 2025);
        assertThat(report[6]).isEqualTo(6_000_000_000L);
        assertThat(report[5]).isEqualTo(0L);
        assertThat(report[7]).isEqualTo(0L);
    }

    @Test
    void handlesEmptySubscriptionList() {
        assertThat(solution.monthlyCostReport(List.of(), 2025))
                .containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
