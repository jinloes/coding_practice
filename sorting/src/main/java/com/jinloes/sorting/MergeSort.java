package com.jinloes.sorting;

import java.util.ArrayList;
import java.util.List;

public class MergeSort {

    public static <T extends Comparable<T>> List<T> sort(List<T> toSort) {
        if (toSort == null || toSort.size() <= 1) {
            return toSort;
        }
        int start = 0;
        int end = toSort.size();
        int mid = start + (end - start) / 2;

        List<T> part1 = new ArrayList<>(toSort.subList(start, mid));
        List<T> part2 = new ArrayList<>(toSort.subList(mid, end));
        part1 = sort(part1);
        part2 = sort(part2);

        List<T> sorted = new ArrayList<>();
        while (!part1.isEmpty() && !part2.isEmpty()) {
            T obj1 = part1.get(0);
            T obj2 = part2.get(0);
            if (obj1.compareTo(obj2) < 0) {
                sorted.add(obj1);
                part1.remove(0);
            } else {
                sorted.add(obj2);
                part2.remove(0);
            }
        }
        if (!part1.isEmpty()) {
            sorted.addAll(part1);
        }
        if (!part2.isEmpty()) {
            sorted.addAll(part2);
        }
        return sorted;
    }
}
