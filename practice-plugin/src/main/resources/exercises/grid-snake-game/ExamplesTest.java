package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void growsOnFoodAndEndsOnTopWallCollision() {
        Solution game = new Solution(3, 2, new int[][]{{1, 2}, {0, 1}});
        assertThat(runMoves(game, 'R', 'D', 'R', 'U', 'L', 'U'))
                .containsExactly(0, 0, 1, 1, 2, -1);
    }

    @Test
    void allowsSteppingIntoCellVacatedByTailOnSameTick() {
        Solution game = new Solution(2, 2, new int[][]{{0, 1}, {1, 1}, {1, 0}});
        assertThat(runMoves(game, 'R', 'D', 'L', 'U', 'R'))
                .containsExactly(1, 2, 3, 3, 3);
    }

    @Test
    void detectsBodyCollisionAndKeepsGameOverSticky() {
        Solution game = new Solution(3, 3, new int[][]{{0, 1}, {0, 2}, {1, 2}, {1, 1}});
        assertThat(runMoves(game, 'R', 'R', 'D', 'L', 'U', 'R'))
                .containsExactly(1, 2, 3, 4, -1, -1);
    }

    private static List<Integer> runMoves(Solution game, char... moves) {
        List<Integer> scores = new ArrayList<>(moves.length);
        for (char move : moves) {
            scores.add(game.step(move));
        }
        return scores;
    }
}
