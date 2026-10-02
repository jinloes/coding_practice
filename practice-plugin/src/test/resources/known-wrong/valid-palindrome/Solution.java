package com.jinloes.practice;
public class Solution {
    public static boolean isPalindrome(String text) {
        return new StringBuilder(text).reverse().toString().equals(text);
    }
}
