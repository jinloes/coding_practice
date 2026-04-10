package com.jinloes.concurrency;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AsyncIdFetchTest {

  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

  /** Simulates a remote call that resolves a name for a given ID. */
  private CompletableFuture<String> fetchById(int id) {
    return CompletableFuture.supplyAsync(() -> {
      if (id < 0) {
        throw new IllegalArgumentException("Invalid id: " + id);
      }
      return "name-" + id;
    }, EXECUTOR);
  }

  @Test
  void combinesResultsFromAllIds() {
    List<Integer> ids = List.of(1, 2, 3, 4, 5);

    // Kick off one future per ID and attach whenComplete to log/inspect each result.
    List<CompletableFuture<String>> futures = ids.stream()
        .map(id -> fetchById(id)
            .whenComplete((result, ex) -> {
              if (ex != null) {
                log.error("Failed to fetch id {}", id, ex);
              } else {
                log.debug("Fetched id {} -> {}", id, result);
              }
            }))
        .toList();

    // Wait for all futures to finish, then collect the results.
    List<String> names = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(ignored -> futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList()))
        .join();

    assertThat(names).containsExactlyInAnyOrder("name-1", "name-2", "name-3", "name-4", "name-5");
  }

  @Test
  void whenCompleteDetectsExceptionAndSkipsBadIds() {
    List<Integer> ids = List.of(1, -1, 3);   // -1 will throw

    AtomicInteger errorCount = new AtomicInteger(0);

    List<CompletableFuture<String>> futures = ids.stream()
        .map(id -> fetchById(id)
            .whenComplete((result, ex) -> {
              if (ex != null) {
                // Record the error but do NOT rethrow — whenComplete passes the
                // exception through so callers can still handle it downstream.
                errorCount.incrementAndGet();
                log.error("Error for id {}", id, ex);
              }
            })
            // Recover from the failed future so allOf doesn't short-circuit.
            .exceptionally(ex -> null))
        .toList();

    List<String> names = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(ignored -> futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)   // drop recovered nulls
            .collect(Collectors.toList()))
        .join();

    assertThat(errorCount.get()).isEqualTo(1);
    assertThat(names).containsExactlyInAnyOrder("name-1", "name-3");
  }
}