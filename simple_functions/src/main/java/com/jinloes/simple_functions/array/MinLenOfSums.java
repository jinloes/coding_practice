package com.jinloes.simple_functions.array;

import java.util.PriorityQueue;

/**
 * Given an array of integers arr and an integer target.
 * <p>
 * You have to find two non-overlapping sub-arrays of arr each with a sum equal target. There can be multiple answers so you have to find an answer where the sum of the lengths of the two sub-arrays is minimum.
 * <p>
 * Return the minimum sum of the lengths of the two required sub-arrays, or return -1 if you cannot find such two sub-arrays.
 */
public class MinLenOfSums {

    public int minSumOfLengths(int[] arr, int target) {
        PriorityQueue<Interval> minHeap = new PriorityQueue<>();

        int left = 0;
        int sum = 0;

        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];

            while (sum > target) {
                sum -= arr[left];
                left++;
            }

            if (sum == target) {
                Interval interval = new Interval(left, i);
                minHeap.add(interval);
            }

        }

        int minLength = Integer.MAX_VALUE;

        PriorityQueue<Interval> nonMatches = new PriorityQueue<>();

        while (!minHeap.isEmpty()) {
            Interval first = minHeap.poll();
            while (!minHeap.isEmpty()) {
                Interval second = minHeap.poll();
                if (first.intersects(second)) {
                    nonMatches.add(second);
                } else {
                    minLength = Math.min(minLength, first.getLength() + second.getLength());
                    break;
                }
            }
            minHeap = nonMatches;
            nonMatches = new PriorityQueue<>();
        }


        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    private class Interval implements Comparable<Interval> {
        int start;
        int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public boolean intersects(Interval other) {
            return start <= other.end && end >= other.start;
        }

        public int getLength() {
            return end - start + 1;
        }

        public int compareTo(Interval other) {
            return getLength() - other.getLength();
        }
    }
}
