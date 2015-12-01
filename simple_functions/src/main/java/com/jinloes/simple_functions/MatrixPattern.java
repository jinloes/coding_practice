package com.jinloes.simple_functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Finds a matrix pattern in another. Example:
 * <pre>
 *     {@code
 *      7283455864
 *      6731158619
 *      8988242643
 *      3830589324
 *      222[9505]813
 *      563[3845]374
 *      647[3530]293
 *      7053106601
 *      0834282956
 *      4607924137
 *
 *      matches
 *
 *      9505
 *      3845
 *      3530
 *     }
 * </pre>
 */
public class MatrixPattern {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numCases = scanner.nextInt();
        for (int i = 0; i < numCases; i++) {
            int rows = scanner.nextInt();
            int columns = scanner.nextInt();
            String[] matrix = new String[rows];
            scanner.nextLine();
            for (int j = 0; j < rows; j++) {
                matrix[j] = scanner.nextLine().trim();
            }
            List<String> patterns = new ArrayList<>();
            int patternRows = scanner.nextInt();
            int patternColumns = scanner.nextInt();
            scanner.nextLine();
            for (int j = 0; j < patternRows; j++) {
                patterns.add(scanner.nextLine().trim());
            }
            List<Integer> previousIndices = new ArrayList<>();
            int patternsIdx = 0;
            for (int j = 0; j < rows; j++) {
                String line = matrix[j];
                List<Integer> indices = new ArrayList<>();
                String searchStr = patterns.get(patternsIdx);
                int subStrIdx = line.indexOf(searchStr);
                // Handle multiple occurrences of the string on a line
                while (subStrIdx > -1) {
                    indices.add(subStrIdx);
                    subStrIdx = line.indexOf(searchStr, subStrIdx + searchStr.length());
                }
                if (!indices.isEmpty()) {
                    // if previous indices is not empty remove the indices for the current line
                    // that didn't exist in the previous
                    if (!previousIndices.isEmpty()) {
                        for (int k = 0; k < indices.size(); k++) {
                            Integer idx = indices.get(k);
                            if (!previousIndices.contains(idx)) {
                                indices.remove(idx);
                            }
                        }
                    }
                    previousIndices = indices;
                    // if there were not matching indices reset the pointer
                    if (indices.isEmpty()) {
                        patternsIdx = 0;
                    } else {
                        patternsIdx++;

                    }
                    // if we found all the matches print yes
                    if (patternsIdx >= patterns.size()) {
                        System.out.println("YES");
                        break;
                    }
                } else {
                    patternsIdx = 0;
                }
            }
            // if we never found enough pattern matches print no
            if (patternsIdx < patterns.size()) {
                System.out.println("NO");
            }
        }
    }
}
