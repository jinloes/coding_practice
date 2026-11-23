package com.jinloes.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
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

  @Test
  void retriesWhenOuterLockOrConfirmLockTimesOut() {
    AtomicInteger tryLockCalls = new AtomicInteger();
    Node node = new Node(new ScriptedLock(call -> switch (call) {
      case 1 -> false; // outer tryLock fails -> sleeps and retries
      case 2 -> true;  // outer tryLock succeeds
      case 3 -> false; // confirm tryLock fails -> pre-interrupt so sleep returns immediately
      default -> true; // next iteration succeeds
    }, call -> {
      if (call == 3) {
        Thread.currentThread().interrupt();
      }
    }, tryLockCalls));

    node.propagateUpdate("update", new Node());
    Thread.interrupted(); // clear interrupt left by interrupted retry sleep

    assertThat(tryLockCalls.get()).isEqualTo(5);
  }

  @Test
  void preservesInterruptStatusWhenTryLockIsInterrupted() {
    AtomicInteger tryLockCalls = new AtomicInteger();
    Node node = new Node(new ScriptedLock(call -> {
      if (call == 1 || call == 3) {
        throw new InterruptedException("interrupted");
      }
      return true;
    }, call -> {}, tryLockCalls));

    try {
      node.propagateUpdate("update", new Node());
      assertThat(Thread.currentThread().isInterrupted()).isTrue();
      assertThat(tryLockCalls.get()).isEqualTo(5);
    } finally {
      Thread.interrupted();
    }
  }

  private static final class ScriptedLock implements Lock {
    @FunctionalInterface
    interface TryLockBehavior {
      boolean apply(int call) throws InterruptedException;
    }

    private final TryLockBehavior behavior;
    private final java.util.function.IntConsumer afterCall;
    private final AtomicInteger calls;

    ScriptedLock(TryLockBehavior behavior, java.util.function.IntConsumer afterCall, AtomicInteger calls) {
      this.behavior = behavior;
      this.afterCall = afterCall;
      this.calls = calls;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      int call = calls.incrementAndGet();
      try {
        return behavior.apply(call);
      } finally {
        afterCall.accept(call);
      }
    }

    @Override
    public void unlock() {
    }

    @Override
    public void lock() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void lockInterruptibly() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Condition newCondition() {
      throw new UnsupportedOperationException();
    }
  }
}