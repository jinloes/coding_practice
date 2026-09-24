package com.jinloes.practice_plugin.catalog;

/**
 * One compile-compatible but incorrect solution per catalog exercise. Each must fail at least one
 * test in its exercise's suites; add an entry whenever an exercise is added to the catalog.
 */
final class KnownWrongSolutions {
    private KnownWrongSolutions() {
    }

    static String source(ExerciseCatalog.Exercise exercise) {
        return switch (exercise.id()) {
            case "pair-sum" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int[] findPair(int[] numbers, int target) {
                            return numbers.length >= 2 ? new int[]{0, 1} : new int[0];
                        }
                    }
                    """;
            case "binary-search" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int search(int[] sorted, int target) {
                            return sorted.length == 0 ? -1 : 0;
                        }
                    }
                    """;
            case "balanced-delimiters" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static boolean isBalanced(String input) {
                            return input.length() % 2 == 0;
                        }
                    }
                    """;
            case "reverse-linked-list" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static class Node {
                            public int value;
                            public Node next;
                            public Node(int value, Node next) {
                                this.value = value;
                                this.next = next;
                            }
                        }
                        public static Node reverse(Node head) {
                            return head;
                        }
                    }
                    """;
            case "array-stack" -> """
                    package com.jinloes.practice;
                    import java.util.ArrayDeque;
                    public class Solution {
                        private final ArrayDeque<Integer> values = new ArrayDeque<>();
                        public void push(int value) { values.addLast(value); }
                        public int pop() { return values.removeFirst(); }
                        public int peek() { return values.getFirst(); }
                        public int size() { return values.size(); }
                        public boolean isEmpty() { return values.isEmpty(); }
                    }
                    """;
            case "binary-min-heap" -> """
                    package com.jinloes.practice;
                    import java.util.ArrayDeque;
                    public class Solution {
                        private final ArrayDeque<Integer> values = new ArrayDeque<>();
                        public void add(int value) { values.addLast(value); }
                        public int removeMin() { return values.removeFirst(); }
                        public int peek() { return values.getFirst(); }
                        public int size() { return values.size(); }
                        public boolean isEmpty() { return values.isEmpty(); }
                    }
                    """;
            case "valid-anagram" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static boolean isAnagram(String first, String second) {
                            return first.length() == second.length();
                        }
                    }
                    """;
            case "first-unique-character" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int firstUniqueIndex(String word) {
                            return word.isEmpty() ? -1 : 0;
                        }
                    }
                    """;
            case "max-stock-profit" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int maxProfit(int[] prices) {
                            return prices.length < 2 ? 0 : prices[prices.length - 1] - prices[0];
                        }
                    }
                    """;
            case "move-zeroes" -> """
                    package com.jinloes.practice;
                    import java.util.Arrays;
                    public class Solution {
                        public static void moveZeroes(int[] numbers) {
                            Arrays.sort(numbers);
                        }
                    }
                    """;
            case "merge-sorted-lists" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static class Node {
                            public int value;
                            public Node next;
                            public Node(int value, Node next) {
                                this.value = value;
                                this.next = next;
                            }
                        }
                        public static Node merge(Node first, Node second) {
                            return first != null ? first : second;
                        }
                    }
                    """;
            case "valid-palindrome" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static boolean isPalindrome(String text) {
                            return new StringBuilder(text).reverse().toString().equals(text);
                        }
                    }
                    """;
            case "add-strings" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static String addStrings(String first, String second) {
                            return Long.toString(Long.parseLong(first) + Long.parseLong(second));
                        }
                    }
                    """;
            case "roman-to-integer" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int romanToInt(String numeral) {
                            int total = 0;
                            for (char symbol : numeral.toCharArray()) {
                                total += switch (symbol) {
                                    case 'I' -> 1;
                                    case 'V' -> 5;
                                    case 'X' -> 10;
                                    case 'L' -> 50;
                                    case 'C' -> 100;
                                    case 'D' -> 500;
                                    default -> 1000;
                                };
                            }
                            return total;
                        }
                    }
                    """;
            case "longest-common-prefix" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static String longestCommonPrefix(String[] words) {
                            return words.length == 0 ? "" : words[0];
                        }
                    }
                    """;
            case "missing-number" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int missingNumber(int[] numbers) {
                            return numbers.length;
                        }
                    }
                    """;
            case "majority-element" -> """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int majorityElement(int[] numbers) {
                            return numbers[numbers.length / 2];
                        }
                    }
                    """;
            default -> throw new IllegalArgumentException("No known wrong source for " + exercise.id());
        };
    }
}
