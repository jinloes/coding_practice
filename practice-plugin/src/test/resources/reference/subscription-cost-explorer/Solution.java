package com.jinloes.practice;

import java.util.List;

public class Solution {
    public record Subscription(String product, long monthlyPriceCents, String startDate, String endDate) {
    }

    public long[] monthlyCostReport(List<Subscription> subscriptions, int year) {
        long[] diff = new long[13];
        int yearStart = year * 12;
        int yearEnd = yearStart + 11;

        for (Subscription sub : subscriptions) {
            int start = toMonthIndex(sub.startDate());
            int end = sub.endDate() == null ? yearEnd : toMonthIndex(sub.endDate());
            int clampedStart = Math.max(start, yearStart);
            int clampedEnd = Math.min(end, yearEnd);
            if (clampedStart <= clampedEnd) {
                diff[clampedStart - yearStart] += sub.monthlyPriceCents();
                diff[clampedEnd - yearStart + 1] -= sub.monthlyPriceCents();
            }
        }

        long[] report = new long[12];
        long running = 0;
        for (int m = 0; m < 12; m++) {
            running += diff[m];
            report[m] = running;
        }
        return report;
    }

    private static int toMonthIndex(String isoDate) {
        int y = Integer.parseInt(isoDate, 0, 4, 10);
        int m = Integer.parseInt(isoDate, 5, 7, 10);
        return y * 12 + (m - 1);
    }
}
