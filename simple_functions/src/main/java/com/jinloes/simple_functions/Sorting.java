package com.jinloes.simple_functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Executes different sorting algorithms
 */
public class Sorting {
    /**
     * The basic algorithm for quick sort is:
     * 1. Pick pivot
     * 2. Move move pivot to the end of the array sequence.
     * 3. for each element in the array sequence
     * - if the element is less than the pivot move it to a stored index
     * - increment the stored index
     * - the point of the stored index is to contain all the values less than the pivot
     * 4. move the pivot to the stored index, so that values to the right of it are greater than the pivot
     * 5. repeat process on left sub array and right sub array
     * <p>
     * Quick sort can be done in place but can be unstable.
     *
     * @param arr array to sort
     */
    public static void quickSort(int[] arr) {
        if (arr == null) {
            return;
        }
        quickSortInternal(arr, 0, arr.length - 1);
    }

    private static void quickSortInternal(int[] arr, int start, int end) {
        if (start < end) {
            int partition = partition(arr, start, end);
            quickSortInternal(arr, start, partition - 1);
            quickSortInternal(arr, partition + 1, end);
        }
    }

    private static int partition(int[] arr, int start, int end) {
        int pivot = start + ((end - start) / 2);
        int pivotValue = arr[pivot];
        // move the pivot to the end
        arr[pivot] = arr[end];
        arr[end] = pivotValue;
        int finalPivotIndex = start;
        // for each value less than the pivot move it to the left of values greater than the pivot
        for (int i = start; i <= end; i++) {
            if (arr[i] < pivotValue) {
                int tmp = arr[i];
                arr[i] = arr[finalPivotIndex];
                arr[finalPivotIndex] = tmp;
                finalPivotIndex++;
            }
        }
        // Move the pivot into it's spot, so that values to the right are greater
        arr[end] = arr[finalPivotIndex];
        arr[finalPivotIndex] = pivotValue;
        return finalPivotIndex;
    }

    /**
     * Merge sort algorithm is:
     * <p>
     * 1. Split the list up in half
     * 2. Split the left and right lists up until a single value is left.
     * 3. Merge the left and right results
     * <p>
     * Merge sort requires extra memory, but works real well for data that does not have random access.
     *
     * @param list list to sort
     * @return sorted list
     */
    public static List<Integer> mergeSort(List<Integer> list) {
        if (list.size() == 1) {
            return list;
        }
        int mid = list.size() / 2;
        List<Integer> left = new ArrayList<>(list.subList(0, mid));
        List<Integer> right = new ArrayList<>(list.subList(mid, list.size()));
        left = mergeSort(left);
        right = mergeSort(right);
        return merge(left, right);
    }

    private static List<Integer> merge(List<Integer> left, List<Integer> right) {
        List<Integer> result = new ArrayList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            Integer leftVal = left.get(0);
            Integer rightVal = right.get(0);
            if (leftVal.compareTo(rightVal) <= 0) {
                result.add(leftVal);
                left.remove(0);
            } else {
                result.add(rightVal);
                right.remove(0);
            }
        }
        result.addAll(left.stream().collect(Collectors.toList()));
        result.addAll(right.stream().collect(Collectors.toList()));
        return result;
    }
}
