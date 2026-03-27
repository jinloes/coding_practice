import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxStackTest {
    private MaxStack stack;

    @BeforeEach
    void setUp() {
        stack = new MaxStack();
    }

    @Test
    void push() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertThat(stack.pop()).isEqualTo(3);
        assertThat(stack.pop()).isEqualTo(2);
        assertThat(stack.pop()).isEqualTo(1);
    }

    @Test
    void pop() {
        stack.push(1);
        stack.push(2);

        assertThat(stack.pop()).isEqualTo(2);

        stack.push(3);

        assertThat(stack.pop()).isEqualTo(3);
        assertThat(stack.pop()).isEqualTo(1);


    }

    @Test
    void max() {
        stack.push(1);
        assertThat(stack.max()).isEqualTo(1);
        stack.push(2);
        assertThat(stack.max()).isEqualTo(2);
        stack.push(3);
        assertThat(stack.max()).isEqualTo(3);
        stack.push(2);
        assertThat(stack.max()).isEqualTo(3);
    }
}