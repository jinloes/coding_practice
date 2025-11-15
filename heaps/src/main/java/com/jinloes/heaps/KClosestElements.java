package com.jinloes.heaps;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class KClosestElements {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
                Comparator.<Integer>comparingInt(val -> Math.abs(val - x)).reversed()
                        .thenComparing(Comparator.<Integer>naturalOrder().reversed())
        );

        for (int num : arr) {
            maxHeap.add(num);

            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        return maxHeap.stream()
                .sorted()
                .toList();
    }

    public List<Integer> findClosestElementsBinarySearch(int[] arr, int k, int x) {
        int left = 0;
        int right = arr.length - k;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (x - arr[mid] > arr[mid + k] - x) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return Arrays.stream(arr, left, left + k)
                .boxed()
                .toList();
    }
}
