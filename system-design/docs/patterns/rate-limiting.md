# Rate limiting

**Use when:** the system must enforce fair use, protect capacity, control cost,
or slow abusive clients.

## Algorithms

| Algorithm | Behavior | Trade-off |
| --- | --- | --- |
| Fixed window | Count requests in a clock-aligned window | Simple, but permits bursts at window boundaries |
| Sliding log | Store timestamps for all recent requests | Accurate, but memory intensive |
| Sliding-window counter | Blend adjacent fixed-window counts | Approximate with modest state |
| Token bucket | Refill tokens at a steady rate; requests spend tokens | Supports controlled bursts |
| Leaky bucket | Drain queued work at a steady rate | Smooth output, but adds queueing |

Token bucket is a strong interview default because it separates sustained rate
from allowed burst:

```text
bucket capacity = maximum burst
refill rate     = sustained requests per second
```

## Distributed design

Choose a limit key such as user, tenant, API key, IP, endpoint, or a hierarchy
of them. Edge nodes need a fast shared counter, partitioned ownership, or local
allowances leased from a global budget.

An atomic token-bucket operation conceptually:

1. Read the stored token count and last-refill time.
2. Add tokens earned since the last refill, capped at capacity.
3. If enough tokens remain, subtract the request cost and allow it.
4. Persist the new count and timestamp atomically.

Return a clear rejection, normally HTTP `429`, with retry guidance when useful.
Different requests may spend different numbers of tokens based on cost.

## Trade-offs and failure modes

- A centralized limiter can add latency and become a bottleneck.
- Per-node limits are fast but oversubscribe the global limit as nodes scale.
- Clock skew breaks naive time calculations; use one authoritative operation or
  server-side time.
- Decide whether limiter failure is fail-open or fail-closed. Public reads may
  fail open with a local emergency cap; expensive writes may fail closed.
- IP limits can punish users behind NAT and are easy to evade.
- Rate limiting protects fairness, while load shedding protects the system when
  total demand exceeds capacity. Mature designs use both.

## Interview cues

State the key, algorithm, burst size, refill rate, state location, and failure
policy. Distinguish a product quota ("10,000 requests per month") from a
short-term rate limit ("100 requests per second").
**Sound bite:** "The limiter enforces policy before scarce work is allocated;
token bucket allows a bounded burst while preserving a sustained rate."
