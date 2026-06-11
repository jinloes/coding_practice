package com.jinloes.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Write a producer thread and a consumer thread that share a fixed-size buffer
 * and an index to access the buffer. The producer should place numbers into
 * the buffer, and the consumer should remove the numbers. The order in which
 * the numbers are added or removed is not important.
 */
public class ProducerConsumer {

    /** Sentinel value signalling the consumer that no more items will arrive. */
    private static final Integer POISON_PILL = Integer.MIN_VALUE;

    static class Producer extends Thread {
        private final BlockingQueue<Integer> buffer;

        public Producer(BlockingQueue<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 15; i++) {
                    buffer.put(i);
                }
                buffer.put(POISON_PILL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    static class Consumer extends Thread {
        private final BlockingQueue<Integer> buffer;

        public Consumer(BlockingQueue<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                Integer value;
                while ((value = buffer.take()) != POISON_PILL) {
                    System.out.println(value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(20);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}