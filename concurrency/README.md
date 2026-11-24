# Java Concurrency & Multithreading Study Guide

A concise study guide for Java concurrency interviews and production multithreading, paired with runnable examples and tests in this module.

```bash
./gradlew :concurrency:check
```

## Reference Code Map

| Class | Concepts Demonstrated |
|---|---|
| `ProducerConsumer` | Bounded buffer coordination, thread interruption handling (`Thread.currentThread().interrupt()`), clean shutdown |
| `Node` | Concurrent state updates and retry/interruption semantics |
| `ParallelStreamTest` | `ForkJoinPool` vs `Executors.newFixedThreadPool`, custom `ForkJoinWorkerThreadFactory`, `CompletableFuture.allOf` |
| `AsyncIdFetchTest` | Asynchronous fan-out/fan-in composition with `CompletableFuture` |

---

## 1. Java Memory Model (JMM) & Visibility

Concurrency bugs stem from **visibility** (stale CPU caches), **ordering** (compiler/CPU instruction reordering), and **atomicity** (interleaved read-modify-write).

- **Happens-before guarantees:**
  - Unlocking a monitor happens-before every subsequent lock of the same monitor.
  - Writing a `volatile` field happens-before every subsequent read of that field.
  - `Thread.start()` happens-before any action in the started thread; all actions in a thread happen-before `Thread.join()` returns.
  - Final fields initialized safely during construction are visible to all threads once the constructor finishes (without leaking `this`).
- **`volatile` vs atomic:** `volatile` gives visibility and ordering, **not** atomicity (`count++` on a `volatile int` is still a data race). Use `AtomicInteger` / `LongAdder` for counters.
- **Safe publication:** Publish shared objects via `final` fields, a `volatile` reference, a synchronized block, or a thread-safe collection (`ConcurrentHashMap`, `BlockingQueue`).

---

## 2. Locks, Monitors & Synchronization Primitives

| Primitive | When to Use | Key Trade-off |
|---|---|---|
| `synchronized` | Simple mutual exclusion on one lock | Concise, automatic unlock, JVM biased/thin lock optimizations; no try-lock, timeout, or interruptible wait |
| `ReentrantLock` | Need `tryLock(timeout)`, `lockInterruptibly()`, fairness, or multiple `Condition` queues | Must always unlock in `finally` |
| `ReadWriteLock` / `StampedLock` | Heavily read-dominated shared state | `StampedLock` supports optimistic reads (not reentrant!) |
| `Semaphore` | Bound concurrent access to `N` permits (connection pools, rate gates) | Permits are not owned by a specific thread |
| `CountDownLatch` | One-shot barrier: wait for `N` tasks to complete | Cannot be reset |
| `CyclicBarrier` / `Phaser` | Reusable multi-phase barrier where threads wait for each other | `Phaser` supports dynamic party registration |

- **Always guard `wait()` / `Condition.await()` in a `while` loop** (never `if`) to protect against spurious wakeups and stolen conditions.
- **Avoid deadlock** by acquiring multiple locks in a strict global order (e.g., by account ID) or using `tryLock(timeout)` with backoff.

---

## 3. Lock-Free Concurrency & Concurrent Collections

- **CAS (`AtomicReference`, `AtomicInteger`, `VarHandle`):** Optimistic compare-and-swap loops scale better than locks under low-to-moderate contention; watch for the **ABA problem** (use `AtomicStampedReference` when identity reuse matters).
- **`LongAdder` vs `AtomicLong`:** Under high write contention (metrics/counters), `LongAdder` stripes increments across cells to avoid cache-line bouncing, at the cost of a slightly more expensive `sum()`.
- **Collections:**
  - `ConcurrentHashMap`: lock-free reads, per-bin locking on writes, atomic `compute`/`merge`/`putIfAbsent`. Never `null` keys/values. Iterators are weakly consistent (no `ConcurrentModificationException`).
  - `CopyOnWriteArrayList`: `O(1)` lock-free reads, copies the full array on every write—great for tiny listener lists, terrible for write-heavy workloads.
  - `ArrayBlockingQueue` / `LinkedBlockingQueue`: bounded queues provide natural backpressure between producers and consumers.

---

## 4. Executors, `ForkJoinPool`, `CompletableFuture` & Virtual Threads

- **Thread pool sizing:**
  - CPU-bound work: `≈ Runtime.getRuntime().availableProcessors()` (or `N + 1`).
  - Blocking I/O with platform threads: `N * (1 + waitTime / computeTime)` — always bound the work queue to prevent OOM under overload.
- **`ForkJoinPool` & parallel streams:** `parallelStream()` runs on `ForkJoinPool.commonPool()` by default. Never run blocking I/O on the common pool—it starves unrelated parallel work across the JVM.
- **`CompletableFuture`:**
  - Pass an explicit `Executor` to `*Async` methods so blocking calls stay off the common pool.
  - Handle errors with `exceptionally` / `handle` / `whenComplete`; always apply `orTimeout(...)` for external calls.
- **Virtual threads (Java 21+):** `Executors.newVirtualThreadPerTaskExecutor()` decouples concurrency from OS threads for blocking I/O. Avoid pooling virtual threads, limit downstream concurrency with a `Semaphore` instead of a thread pool, and watch for **pinning** (historically `synchronized` blocks around blocking I/O on JDK 21–23; largely resolved in JDK 24+ via JEP 491, though native calls still pin).

---

## 5. Interruption & Graceful Shutdown

- **Never swallow `InterruptedException`:** Either propagate it or restore the interrupt flag (`Thread.currentThread().interrupt()`) before returning/throwing so callers know cancellation was requested.
- **Executor shutdown pattern:** Call `shutdown()`, wait with `awaitTermination(...)`, then fall back to `shutdownNow()` on timeout.

---

## 6. Classic Concurrency Interview Problems

Practice implementing these cleanly from scratch and proving both **safety** (no races/deadlocks) and **liveness** (no starvation):

1. **Bounded Blocking Queue (Producer-Consumer)** — `ReentrantLock` + two `Condition`s (`notFull`, `notEmpty`) vs `Semaphore`s.
2. **Thread-Safe Rate Limiter** — token bucket or sliding window under concurrent callers (see `system-design`'s `TokenBucketRateLimiter`).
3. **Read-Write Lock with Writer Preference** — prevent writer starvation under continuous readers.
4. **Concurrent LRU Cache** — `ReentrantReadWriteLock` or sharded stripes vs lock-free trade-offs.
5. **Web Crawler / Task Scheduler with Dependencies** — `ExecutorService` + `ConcurrentHashMap.newKeySet()` + `CountDownLatch`/`Phaser`.
6. **Singleflight / Request Coalescing** — `ConcurrentHashMap<K, CompletableFuture<V>>` using `computeIfAbsent` so concurrent misses share one upstream call (and evict failed futures cleanly).
