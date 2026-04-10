package com.jinloes.concurrency;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ParallelStreamTest {

  @Test
  void testFixedThreadPoolParallel() throws Exception {
    try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
      pool.submit(() ->
          IntStream.of(1, 2, 3)
              .parallel()
              .forEach(val -> log.info("Val is {} on thread: {}", val, Thread.currentThread().getName()))
      ).get();
    }
  }

  @Test
  void testForkJoin() {
    try (ForkJoinPool forkJoinPool = new ForkJoinPool(10, pool -> {
      var worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
      worker.setName("test-pool-" + worker.getPoolIndex());
      return worker;
    }, null, false)) {
      CompletableFuture<?>[] futures = IntStream.range(0, 1000)
          .mapToObj(val -> CompletableFuture.runAsync(() -> handleVal(val), forkJoinPool))
          .toArray(CompletableFuture[]::new);

      CompletableFuture.allOf(futures).join();
    }
  }

  @Test
  void testFixedThreadPool() {
    try (ExecutorService executorService = Executors.newFixedThreadPool(10,
        new ThreadFactoryBuilder().setNameFormat("test-pool-%d").build())) {
      CompletableFuture<?>[] futures = IntStream.range(0, 10)
          .mapToObj(val -> CompletableFuture.runAsync(() -> handleVal(val), executorService))
          .toArray(CompletableFuture[]::new);

      CompletableFuture.allOf(futures).join();
    }
  }

  private void handleVal(int val) {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    log.info("Val is {} on thread: {} {}", val, Thread.currentThread().getName(),
        Thread.currentThread().getId());
  }
}
