package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void futureFoodIsInactiveUntilCurrentFoodIsEaten() {
        Solution game = new Solution(3, 2, new int[][]{{1, 2}, {0, 1}});
        // (0, 1) is the second food item, so visiting it first does not eat it.
        assertThat(game.step('R')).isZero();
        assertThat(game.step('R')).isZero();
        assertThat(game.step('D')).isEqualTo(1);
        assertThat(game.step('L')).isEqualTo(1);
        assertThat(game.step('U')).isEqualTo(2);
    }

    @Test
    void checksAllFourBoardBoundaries() {
        assertThat(new Solution(2, 2, new int[0][0]).step('U')).isEqualTo(-1);
        assertThat(new Solution(2, 2, new int[0][0]).step('L')).isEqualTo(-1);

        Solution rightWall = new Solution(1, 2, new int[0][0]);
        assertThat(rightWall.step('R')).isEqualTo(-1);

        Solution bottomWall = new Solution(2, 1, new int[0][0]);
        assertThat(bottomWall.step('D')).isEqualTo(-1);
    }

    @Test
    void reversingIntoNeckOfLengthThreeSnakeFailsImmediately() {
        Solution game = new Solution(4, 1, new int[][]{{0, 1}, {0, 2}});
        assertThat(game.step('R')).isEqualTo(1);
        assertThat(game.step('R')).isEqualTo(2);
        assertThat(game.step('L')).isEqualTo(-1);
    }

    @Test
    void defensiveCopyPreventsCallerMutatingFoodArrayAfterConstruction() {
        int[][] food = {{0, 1}};
        Solution game = new Solution(2, 1, food);
        food[0][1] = 0;

        assertThat(game.step('R')).isEqualTo(1);
    }

    @Test
    void validatesBoardDimensionsFoodAndDirection() {
        assertThatThrownBy(() -> new Solution(0, 2, new int[0][0]))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(2, 0, new int[0][0]))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(2, 2, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(2, 2, new int[][]{{1}}))
                .isInstanceOf(IllegalArgumentException.class);

        Solution game = new Solution(2, 2, new int[0][0]);
        assertThatThrownBy(() -> game.step('X')).isInstanceOf(IllegalArgumentException.class);
    }
}
