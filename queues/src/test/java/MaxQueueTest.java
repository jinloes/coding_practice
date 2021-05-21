import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxQueueTest {
    private MaxQueue maxQueue;

    @BeforeEach
    void setUp() {
        maxQueue = new MaxQueue();
    }

    @Test
    void enqueueDequeue() {
        maxQueue.enqueue(5);
        maxQueue.enqueue(4);
        maxQueue.enqueue(3);

        assertThat(maxQueue.dequeue()).isEqualTo(5);
        assertThat(maxQueue.dequeue()).isEqualTo(4);
        assertThat(maxQueue.dequeue()).isEqualTo(3);
    }

    @Test
    void max() {
        maxQueue.enqueue(5);
        maxQueue.enqueue(4);
        maxQueue.enqueue(3);

        assertThat(maxQueue.max()).isEqualTo(5);

        maxQueue.enqueue(6);
        maxQueue.enqueue(6);

        assertThat(maxQueue.max()).isEqualTo(6);

        maxQueue.enqueue(5);

        assertThat(maxQueue.max()).isEqualTo(6);


        maxQueue.dequeue();
        maxQueue.dequeue();
        maxQueue.dequeue();

        assertThat(maxQueue.max()).isEqualTo(6);

        maxQueue.dequeue();

        assertThat(maxQueue.max()).isEqualTo(6);

        maxQueue.dequeue();

        assertThat(maxQueue.max()).isEqualTo(5);
    }
}