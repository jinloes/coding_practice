package com.jinloes.simple_functions;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


/**
 * Executes different sorting algorithms.
 */
public class Sorter {
    /**
     * The basic algorithm for quick sort is:
     * <pre>
     *     {@code
     *      1. Pick pivot
     *      2. Move move pivot to the end of the array sequence.
     *      3. for each element in the array sequence
     *          - if the element is less than the pivot move it to a stored index
     *              - increment the stored index
     *              - the point of the stored index is to contain all the values less than the
     *                  pivot
     *      4. move the pivot to the stored index, so that values to the right of it are greater
     *          than the pivot
     *      5. repeat process on left sub array and right sub array.
     * }
     * </pre>
     * Quick sort can be done in place but can be unstable.
     *
     * @param arr array to sort
     */
    public static void qsort(int[] arr) {
        if (arr == null || arr.length == 1) {
            return;
        }
        qsortInternal(arr, 0, arr.length - 1);
    }

    private static void qsortInternal(int[] arr, int start, int end) {
        if (start >= end) {
            return;
        }
        int partitionIdx = qsortPartition(arr, start, end);
        quickSortInternal(arr, start, partitionIdx - 1);
        quickSortInternal(arr, partitionIdx + 1, end);

    }

    private static int qsortPartition(int[] arr, int start, int end) {
        int partitionIdx = (start - 1);
        int partition = arr[end];
        for (int i = start; i < end; i++) {
            if (partition > arr[i]) {
                partitionIdx++;
                int temp = arr[i];
                arr[i] = partition;
                arr[partitionIdx] = temp;
            }
        }
        int temp = arr[partitionIdx + 1];
        arr[partitionIdx + 1] = arr[end];
        arr[end] = temp;
        return partitionIdx + 1;
    }

    /**
     * Uses mid partition
     *
     * @param arr to sort
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
     * <pre>
     *     {@code
     *      1. Split the list up in half
     *      2. Split the left and right lists up until a single value is left.
     *      3. Merge the left and right results
     * }
     * </pre>
     * Merge sort requires extra memory, but works real well for data that does not have random
     * access.
     *
     * @param list list to sort
     * @return sorted list
     */
    public static List<Integer> mergeSort(List<Integer> list) {
        if (CollectionUtils.isEmpty(list) || list.size() == 1) {
            return list;
        }
        MergeSorter mergeSorter = new MergeSorter(list);
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        forkJoinPool.invoke(mergeSorter);
        return mergeSorter.getResult();
    }

    private static class MergeSorter extends RecursiveAction {
        private final List<Integer> toSort;
        private final List<Integer> result;

        public MergeSorter(List<Integer> toSort) {
            this.toSort = toSort;
            result = new ArrayList<>(toSort.size());
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
            result.addAll(left);
            result.addAll(right);
            return result;
        }

        public List<Integer> getResult() {
            return result;
        }

        @Override
        protected void compute() {
            if (CollectionUtils.isEmpty(toSort) || toSort.size() <= 1) {
                result.addAll(toSort);
                return;
            }
            int mid = toSort.size() / 2;
            List<Integer> left = new ArrayList<>(toSort.subList(0, mid));
            List<Integer> right = new ArrayList<>(toSort.subList(mid, toSort.size()));
            MergeSorter leftSorter = new MergeSorter(left);
            MergeSorter rightSorter = new MergeSorter(right);
            invokeAll(leftSorter, rightSorter);
            result.addAll(merge(leftSorter.getResult(), rightSorter.getResult()));
        }
    }
}
