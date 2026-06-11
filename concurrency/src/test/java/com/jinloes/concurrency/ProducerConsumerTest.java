package com.jinloes.concurrency;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProducerConsumerTest {

    @Test
    void consumerReceivesEveryProducedItemAndTerminates() throws InterruptedException {
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(20);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));

        try {
            ProducerConsumer.Producer producer = new ProducerConsumer.Producer(buffer);
            ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(buffer);

            producer.start();
            consumer.start();

            producer.join(5_000);
            consumer.join(5_000);

            assertThat(producer.isAlive()).isFalse();
            assertThat(consumer.isAlive()).isFalse();
        } finally {
            System.setOut(originalOut);
        }

        String[] lines = captured.toString().trim().split("\\R");
        assertThat(lines).hasSize(15);
        for (int i = 0; i < 15; i++) {
            assertThat(Integer.parseInt(lines[i].trim())).isEqualTo(i);
        }
        // The poison pill must have been consumed, leaving the buffer empty.
        assertThat(buffer).isEmpty();
    }

    @Test
    void mainRunsEndToEnd() throws InterruptedException {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try {
            ProducerConsumer.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }
        // Reaching here means main() started, joined both threads, and returned.
        assertThat(new ProducerConsumer()).isNotNull();
    }

    @Test
    void producerInterruptedWhileBlockedOnFullBufferExitsCleanly() throws InterruptedException {
        // Capacity 1 with no consumer: the producer blocks on put() after the first item.
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(1);
        ProducerConsumer.Producer producer = new ProducerConsumer.Producer(buffer);

        producer.start();
        // Give the producer a moment to fill the buffer and block on the next put().
        while (buffer.remainingCapacity() > 0 && producer.isAlive()) {
            Thread.onSpinWait();
        }
        producer.interrupt();
        producer.join(5_000);

        assertThat(producer.isAlive()).isFalse();
    }

    @Test
    void consumerInterruptedWhileBlockedOnEmptyBufferExitsCleanly() throws InterruptedException {
        // Empty buffer with no producer: the consumer blocks on take().
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(1);
        ProducerConsumer.Consumer consumer = new ProducerConsumer.Consumer(buffer);

        consumer.start();
        // Wait until the consumer thread is actually blocked in take().
        while (consumer.getState() != Thread.State.WAITING && consumer.isAlive()) {
            Thread.onSpinWait();
        }
        consumer.interrupt();
        consumer.join(5_000);

        assertThat(consumer.isAlive()).isFalse();
    }
}
