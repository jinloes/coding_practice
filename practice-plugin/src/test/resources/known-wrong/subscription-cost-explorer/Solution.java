package com.jinloes.practice;

import java.util.List;

public class Solution {
    public record Subscription(String product, long monthlyPriceCents, String startDate, String endDate) {
    }

    public long[] monthlyCostReport(List<Subscription> subscriptions, int year) {
        long[] report = new long[12];
        for (Subscription sub : subscriptions) {
            int startYear = Integer.parseInt(sub.startDate().substring(0, 4));
            int startMonth = Integer.parseInt(sub.startDate().substring(5, 7));
            // Bug: ignores subscriptions that started in a prior year.
            if (startYear != year) {
                continue;
            }
            int endMonth = 12;
            if (sub.endDate() != null) {
                int endYear = Integer.parseInt(sub.endDate().substring(0, 4));
                if (endYear == year) {
                    endMonth = Integer.parseInt(sub.endDate().substring(5, 7));
                }
            }
            for (int m = startMonth; m <= endMonth; m++) {
                report[m - 1] += sub.monthlyPriceCents();
            }
        }
        return report;
    }
}
