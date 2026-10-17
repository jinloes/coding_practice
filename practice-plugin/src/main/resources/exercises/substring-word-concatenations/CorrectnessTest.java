package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyWordsOrTooLongWindowHaveNoMatches() { assertThat(Solution.findStarts("abc", new String[]{})).isEmpty(); assertThat(Solution.findStarts("abc", new String[]{"abcd"})).isEmpty(); }
    @Test
    void rejectsWindowsWithExtraDuplicateWord() { assertThat(Solution.findStarts("foofoofoobar", new String[]{"foo", "bar"})).containsExactly(6); }
    @Test
    void worksAcrossDifferentOffsets() { assertThat(Solution.findStarts("xbarfoofoobar", new String[]{"foo", "bar"})).containsExactly(1, 7); }
    @Test
    void noMatchReturnsEmptyList() { assertThat(Solution.findStarts("abcdef", new String[]{"ab", "ef"})).isEmpty(); }
    @Test
    void matchesBruteForceOnLargerText() {
        StringBuilder text = new StringBuilder();
        String[] chunks = {"ab", "cd", "ef", "ab", "ef", "cd", "zz"};
        for (int i = 0; i < 200; i++) text.append(chunks[i % chunks.length]);
        String value = text.toString();
        String[] words = {"ab", "cd", "ef"};
        assertThat(Solution.findStarts(value, words)).isEqualTo(brute(value, words));
    }
    private static List<Integer> brute(String text, String[] words) { Map<String, Integer> need = new HashMap<>(); for (String word : words) need.merge(word, 1, Integer::sum); int wordLength = words[0].length(); int total = wordLength * words.length; List<Integer> result = new ArrayList<>(); for (int start = 0; start + total <= text.length(); start++) { Map<String, Integer> seen = new HashMap<>(); for (int offset = 0; offset < total; offset += wordLength) seen.merge(text.substring(start + offset, start + offset + wordLength), 1, Integer::sum); if (seen.equals(need)) result.add(start); } return result; }
}
