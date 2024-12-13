package com.jinloes;

import net.jodah.concurrentunit.Waiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

/**
 * Created by jinloes on 11/11/15.
 */
public class NodeTest {
  @Test
  public void testPropagateUpdate() throws TimeoutException {
    Waiter waiter = new Waiter();
    Node node = new Node();
    Node backUp = new Node();
    new Thread((Runnable) () -> {
      node.propagateUpdate("update", backUp);
      waiter.resume();
    }) {
    }.start();

    new Thread((Runnable) () -> {
      backUp.propagateUpdate("update2", node);
      waiter.resume();
    }) {
    }.start();
    try {
      waiter.await(2, TimeUnit.SECONDS, 2);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
