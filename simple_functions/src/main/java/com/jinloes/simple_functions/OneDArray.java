package com.jinloes.simple_functions;

import java.util.Scanner;

/**
 * Created by jinloes on 7/15/15.
 */
public class OneDArray {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int testCases = scanner.nextInt();
        for (int i = 0; i < testCases; i++) {
            scanner.nextLine();
            int arrLen = scanner.nextInt();
            int jumpSize = scanner.nextInt();
            scanner.nextLine();
            int[] arr = new int[arrLen];
            for (int j = 0; j < arrLen; j++) {
                arr[j] = scanner.nextInt();
            }
            if (checkRecursively(arr, 0, jumpSize)) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    private static boolean checkRecursively(int[] arr, int pos, int jumpSize) {
        if (pos >= arr.length) {
            return true;
        }
        if (arr[pos] == 1) {
            return false;
        }
        if (checkRecursively(arr, pos + jumpSize, jumpSize)) {
            return true;
        }
        return checkRecursively(arr, pos + 1, jumpSize);
    }
}
