package com.jinloes.sorting;

import java.util.List;

/**
 * The algorithm for quick sort is:
 * <pre>
 *     {@code
 *      1. Pick pivot
 *      2. Move move pivot to the end of the array sequence.
 *      3. for each element in the array sequence
 *          - if the element is less than the pivot move it to a stored index
 *          - increment the stored index
 *      4. move the pivot to the stored index, so that values to the right of it are greater
 *          than the pivot
 *      5. repeat process on left sub array and right sub array.
 * }
 * </pre>
 * Quick sort can be done in place but can be unstable.
 */
public class QuickSort {

    public static <T extends Comparable<T>> void sort(List<T> toSort) {
        if (toSort == null || toSort.isEmpty()) {
            return;
        }
        sort(toSort, 0, toSort.size() - 1);
    }

    private static <T extends Comparable<T>> void sort(List<T> toSort, int start, int end) {
        if (start < end) {
            int pIdx = partition(toSort, start, end);
            sort(toSort, start, pIdx - 1);
            sort(toSort, pIdx + 1, end);
        }
    }


    private static <T extends Comparable<T>> int partition(List<T> toSort, int start, int end) {
        int pIdx = start;
        T pivot = toSort.get(end);
        for (int i = start; i < end; i++) {
            T currentObj = toSort.get(i);
            T swapObj = toSort.get(pIdx);
            if (currentObj.compareTo(pivot) < 0) {
                toSort.set(i, swapObj);
                toSort.set(pIdx, currentObj);
                pIdx++;
            }
        }
        // Put pivot in place
        T tmp = toSort.get(pIdx);
        toSort.set(pIdx, pivot);
        toSort.set(end, tmp);
        return pIdx;
    }
}
