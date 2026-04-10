package com.jinloes.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NodeTest {

  @Test
  void testPropagateUpdate() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);
    Node node = new Node();
    Node backUp = new Node();

    new Thread(() -> {
      node.propagateUpdate("update", backUp);
      latch.countDown();
    }).start();

    new Thread(() -> {
      backUp.propagateUpdate("update2", node);
      latch.countDown();
    }).start();

    assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
  }
}