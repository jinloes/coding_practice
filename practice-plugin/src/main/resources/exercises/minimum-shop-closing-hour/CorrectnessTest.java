package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingleHourLogs() {
        assertThat(Solution.bestClosingHour("")).as("empty log").isEqualTo(0);
        assertThat(Solution.bestClosingHour("Y")).as("single Y").isEqualTo(1);
        assertThat(Solution.bestClosingHour("N")).as("single N").isEqualTo(0);
    }

    @Test
    void breaksTiesInFavorOfEarlierHour() {
        assertThat(Solution.bestClosingHour("YN"))
                .as("hours 0, 1, 2 have penalties 1, 0, 1")
                .isEqualTo(1);
        assertThat(Solution.bestClosingHour("NY"))
                .as("hours 0, 1, 2 have penalties 1, 2, 1; earliest is 0")
                .isEqualTo(0);
        assertThat(Solution.bestClosingHour("YNYN"))
                .as("hours 1 and 3 tie at penalty 1; earliest is 1")
                .isEqualTo(1);
    }

    @Test
    void recoversAfterEarlyIdleStretchWhenLateDemandDominates() {
        assertThat(Solution.bestClosingHour("NNYYYY"))
                .as("closing at 6 has penalty 2 vs penalty 4 at hour 0")
                .isEqualTo(6);
        assertThat(Solution.bestClosingHour("NNYYYNN"))
                .as("closing at 5 has penalty 2")
                .isEqualTo(5);
    }

    @Test
    void matchesBruteForceAcrossAllShortLogs() {
        for (int len = 1; len <= 8; len++) {
            int combinations = 1 << len;
            for (int mask = 0; mask < combinations; mask++) {
                StringBuilder sb = new StringBuilder(len);
                for (int i = 0; i < len; i++) {
                    sb.append(((mask >> i) & 1) == 1 ? 'Y' : 'N');
                }
                String log = sb.toString();
                int expected = bruteForce(log);
                assertThat(Solution.bestClosingHour(log))
                        .as("bestClosingHour(\"%s\")", log)
                        .isEqualTo(expected);
            }
        }
    }

    private static int bruteForce(String log) {
        int bestHour = 0;
        int bestPenalty = Integer.MAX_VALUE;
        for (int h = 0; h <= log.length(); h++) {
            int penalty = 0;
            for (int i = 0; i < h; i++) {
                if (log.charAt(i) == 'N') {
                    penalty++;
                }
            }
            for (int i = h; i < log.length(); i++) {
                if (log.charAt(i) == 'Y') {
                    penalty++;
                }
            }
            if (penalty < bestPenalty) {
                bestPenalty = penalty;
                bestHour = h;
            }
        }
        return bestHour;
    }
}
