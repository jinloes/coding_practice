package com.jinloes;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

public class ParallelStreamTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ParallelStreamTest.class);

  @Test
  public void testFixedThreadPoolParallel() throws ExecutionException, InterruptedException {
    Executors.newFixedThreadPool(10).submit(() -> {
      Stream.of(1, 2, 3)
          .parallel()
          .forEach(val -> LOGGER.info(() -> "Val is %s on thread: %s".formatted(val,
              Thread.currentThread().getName())));
    }).get();
  }

  @Test
  public void testForkJoin() {
    ForkJoinPool forkJoinPool = new ForkJoinPool(10, factory, null, false);
    CompletableFuture<?>[] futures = IntStream.range(0, 1000)
        .mapToObj(val -> CompletableFuture.runAsync(() -> handleVal(val), forkJoinPool))
        .toArray(CompletableFuture[]::new);

    CompletableFuture.allOf(futures)
        .join();

  }

  ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool -> {
    final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
    worker.setName("test-pool-" + worker.getPoolIndex());
    return worker;
  };

  @Test
  public void testFixedThreadPool() {
    ExecutorService executorService = Executors.newFixedThreadPool(10,
        new ThreadFactoryBuilder()
            .setNameFormat("test-pool-%d")
            .build());
    CompletableFuture<?>[] futures = IntStream.range(0, 1000)
        .mapToObj(val -> CompletableFuture.runAsync(() -> handleVal(val), executorService))
        .toArray(CompletableFuture[]::new);

    CompletableFuture.allOf(futures)
        .join();
  }

  private void handleVal(int val) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    LOGGER.info(() -> "Val is %s on thread: %s %s ".formatted(val, Thread.currentThread().getName(),
        Thread.currentThread().getId()));
  }
}
