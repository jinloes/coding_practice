package com.jinloes.sorting;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RankTeams {
    public String rankTeams(String[] votes) {
        Map<Character, int[]> countMap = new HashMap<>();
        int numTeams = votes[0].length();

        for (String s : votes) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                int[] counts = countMap.getOrDefault(c, new int[numTeams]);
                counts[i]++;
                countMap.put(c, counts);
            }
        }

        return countMap.entrySet()
                .stream()
                .sorted((a, b) -> {
                            for (int i = 0; i < numTeams; i++) {
                                if (a.getValue()[i] != b.getValue()[i]) {
                                    return b.getValue()[i] - a.getValue()[i];
                                }
                            }
                            return 0;
                        }
                )
                .map(Map.Entry::getKey)
                .map(Objects::toString)
                .collect(Collectors.joining(""));
    }
}
