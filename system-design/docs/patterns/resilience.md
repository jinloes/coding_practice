# Timeouts, retries, circuit breakers, and bulkheads

**Use when:** a request depends on remote systems that can fail slowly,
partially, or under load.

## Layer the controls

1. **Deadline:** the total time the caller is willing to spend.
2. **Timeout:** a bounded wait for each attempt, derived from the remaining
   deadline and normal latency distribution.
3. **Retry:** another attempt only for transient failures and safe operations.
4. **Backoff and jitter:** spread attempts so recovery is not hit by a wave.
5. **Circuit breaker:** stop calls temporarily when recent failures show the
   dependency is unhealthy.
6. **Bulkhead:** isolate connection pools, threads, queues, or quotas so one
   dependency or tenant cannot consume everything.
7. **Load shedding:** reject low-priority work before saturation causes a total
   collapse.

Exponential backoff is commonly:

```text
delay = min(cap, base x 2^attempt) + random_jitter
```

Bound both attempts and elapsed time. A service chain where every layer retries
can multiply a single request into many downstream calls. Prefer retries at one
well-informed layer and propagate deadlines.

## Circuit-breaker states

- **Closed:** calls flow and outcomes update health statistics.
- **Open:** calls fail fast or use an explicit fallback.
- **Half-open:** a small number of probes test recovery.

Use error rate and latency over a meaningful sample, not one failure. A circuit
breaker protects capacity; it does not repair the dependency.

## Fallback rules

Good fallbacks are honest and bounded: stale cached data, a reduced feature,
queued non-urgent work, or a clear error. Do not return success-shaped fake data
for a failed critical operation.

## Failure modes

- Retry storms prolong outages.
- Long timeouts hold connections and threads until the caller collapses.
- Unbounded queues convert overload into extreme latency and memory pressure.
- A shared pool lets one slow integration starve unrelated requests.
- A fallback may become stale, unsafe, or more expensive than the main path.
- Hedged requests reduce tail latency but increase load; use them only for
  idempotent reads with a delay and strict budget.

## Interview cues

Walk through a slow dependency, not only a clean crash. State the deadline,
retryable errors, retry budget, breaker behavior, pool isolation, and degraded
response.
**Sound bite:** "Timeouts bound waiting, retries handle rare transient faults,
and breakers plus bulkheads prevent those faults from consuming the whole
service."
