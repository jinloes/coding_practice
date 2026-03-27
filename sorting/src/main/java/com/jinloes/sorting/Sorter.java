package com.jinloes.sorting;

import java.util.ArrayList;
import java.util.List;

public class Sorter {

    public static void quickSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(int[] arr, int start, int end) {
        if (start >= end) {
            return;
        }
        int pivotIdx = partition(arr, start, end);
        quickSort(arr, start, pivotIdx - 1);
        quickSort(arr, pivotIdx + 1, end);
    }

    private static int partition(int[] arr, int start, int end) {
        int pivotIdx = start;
        int pivotVal = arr[end];
        for (int i = start; i < end; i++) {
            if (arr[i] < pivotVal) {
                int tmp = arr[i];
                arr[i] = arr[pivotIdx];
                arr[pivotIdx] = tmp;
                pivotIdx++;
            }
        }
        int tmp = arr[pivotIdx];
        arr[pivotIdx] = pivotVal;
        arr[end] = tmp;
        return pivotIdx;
    }

    public static <T extends Comparable<T>> List<T> mergeSort(List<T> list) {
        if (list == null || list.size() <= 1) {
            return list;
        }
        int mid = list.size() / 2;
        List<T> left = mergeSort(new ArrayList<>(list.subList(0, mid)));
        List<T> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())));

        List<T> sorted = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                sorted.add(left.get(i++));
            } else {
                sorted.add(right.get(j++));
            }
        }
        while (i < left.size()) sorted.add(left.get(i++));
        while (j < right.size()) sorted.add(right.get(j++));
        return sorted;
    }
}