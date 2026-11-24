package com.jinloes.practice;

import java.util.List;

public class Solution {
    public record Subscription(String product, long monthlyPriceCents, String startDate, String endDate) {
    }

    public long[] monthlyCostReport(List<Subscription> subscriptions, int year) {
        throw new UnsupportedOperationException("Implement monthlyCostReport");
    }
}
