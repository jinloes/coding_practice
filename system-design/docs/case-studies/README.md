# Interview case studies

These are worked interview answers, not production blueprints. Attempt each
prompt with the [design template](../template.md) before reading the solution.
Then compare requirements, forcing estimates, data flow, and failure handling
rather than trying to memorize the diagram.

Use the [case study practice prompts](practice-prompts.md) for spoiler-free base
questions, interviewer follow-ups, and failure injections.

## Recommended progression

| Level | Case study | Primary interview muscles |
| --- | --- | --- |
| 1 | [URL shortener](url-shortener.md) | Key generation, caching, read scaling, hot keys |
| 1 | [Distributed API rate limiter](api-rate-limiter.md) | Token bucket, atomic state, approximation, fail-open policy |
| 2 | [Notification platform](notification-platform.md) | Queues, provider isolation, preferences, retries |
| 2 | [Realtime chat](chat-system.md) | Persistent connections, ordering, offline delivery |
| 3 | [Home news feed](news-feed.md) | Fan-out, materialized views, skewed workloads |
| 3 | [Video upload and streaming](video-platform.md) | Object storage, media pipelines, CDN delivery |
| 4 | [Payment processing](payment-processing.md) | Idempotency, ledgers, ambiguous outcomes, reconciliation |
| 4 | [Ride-hailing dispatch](ride-hailing.md) | Geospatial indexing, realtime state, contention, leases |

## How to use a worked answer

For each case:

1. Read only the scope and requirements.
2. Spend 30 to 45 minutes producing your own design.
3. Compare your API, partition key, consistency choices, and failure paths.
4. Redesign it after applying one interviewer variation.
5. Explain which component you would remove at one-tenth the stated scale.

The goal is to make decisions from requirements. Two strong candidates can
produce different architectures if both state their assumptions and defend the
trade-offs.

## Common interviewer variations

| Variation | What it tests |
| --- | --- |
| Traffic grows by 10x | Bottleneck identification and partitioning |
| One key or customer dominates traffic | Skew, isolation, and fairness |
| The primary region is unavailable | Recovery point, failover, and data placement |
| Read-after-write becomes mandatory | Replica routing and consistency boundaries |
| A dependency times out after accepting work | Idempotency and ambiguous outcomes |
| Events arrive twice or out of order | Deduplication, versions, and state machines |
| Cost must be cut in half | Simplicity, storage tiers, and cache economics |
| Data must remain in one geography | Routing, partitioning, and operational boundaries |
