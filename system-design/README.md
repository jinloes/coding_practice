# System Design Patterns, Labs & Reference Code

A pattern-first system design study guide paired with runnable JDK-only reference implementations and a hands-on Change Data Capture (CDC) lab.

For interactive timed mock interviews—Excalidraw diagramming, `notes.md` templates, level-calibrated (`Mid-Level`, `Senior`, `Staff+`) Copilot CLI grading, and interactive follow-up probing—use the companion [System Design Practice JetBrains plugin](../system-design-plugin/README.md). Keep the worked case studies in [`docs/case-studies/`](docs/case-studies/README.md) closed while attempting a problem cold in the plugin, then compare trade-offs afterward.

---

## Learning Path

1. **[Foundations](docs/foundations.md)** — latency/throughput/availability metrics, back-of-the-envelope capacity math, consistency/storage models, and network realities.
2. **[Interview Playbook](docs/interview-playbook.md)** — 45-minute delivery framework, requirement clarification questions, storage selection cheat sheet, and Staff-level trade-off framing.
3. **[Pattern Catalog](docs/patterns/README.md)** — 13 composable building blocks, each structured around *Intent, Context, Trade-offs, Failure Modes, Interview Signals,* and a *Sound Bite*.
4. **[Case Studies](docs/case-studies/README.md)** — 8 end-to-end worked designs showing how patterns compose under real constraints, plus **[Practice Prompts](docs/case-studies/practice-prompts.md)** (drills, constraint twists, and failure-injection exercises).
5. **[Deliberate Practice](docs/practice.md)** — self-scoring rubric, common anti-patterns, and blank **[Design Template](docs/template.md)**.

---

## Core Pattern Map

| Concern | Patterns |
|---|---|
| **Traffic & Edge** | [Traffic Management](docs/patterns/traffic-management.md), [Rate Limiting](docs/patterns/rate-limiting.md) |
| **Read & Latency Scaling** | [Caching](docs/patterns/caching.md), [Fan-Out](docs/patterns/fan-out.md), [CQRS & Event Sourcing](docs/patterns/cqrs-and-event-sourcing.md) |
| **Data Scale & Durability** | [Partitioning & Sharding](docs/patterns/partitioning.md), [Replication & Consensus](docs/patterns/replication.md) |
| **Async & Cross-Service Consistency** | [Async Messaging](docs/patterns/messaging.md), [Transactional Outbox & CDC](docs/patterns/outbox-and-cdc.md), [Saga](docs/patterns/saga.md) |
| **Correctness & Fault Tolerance** | [Idempotency & Deduplication](docs/patterns/idempotency.md), [Resilience Patterns](docs/patterns/resilience.md), [Leader Election & Leases](docs/patterns/leader-election.md) |

---

## Reference Code Map

Every class under `src/main/java/com/jinloes/system_design/` uses only standard Java 17 APIs and deterministic `Clock` injection so core distributed-systems invariants can be read and tested without external infrastructure:

| Package | Classes | Pattern Demonstrated |
|---|---|---|
| `ratelimit` | `TokenBucketRateLimiter`, `SlidingWindowCounterRateLimiter`, `RateLimitDecision` | Sustained rate + bounded burst vs weighted sliding-window boundary smoothing ([rate-limiting](docs/patterns/rate-limiting.md)) |
| `partitioning` | `ConsistentHashRing` | SHA-256 virtual-node hash ring, clockwise distinct-node replica selection, and ~`1/N` rebalance movement ([partitioning](docs/patterns/partitioning.md)) |
| `resilience` | `CircuitBreaker` | `CLOSED -> OPEN -> HALF_OPEN` fast-fail state machine with bounded recovery probes ([resilience](docs/patterns/resilience.md)) |
| `outbox` | `TransactionalOutbox`, `IdempotentInbox` | Atomic local write + outbox append, at-least-once relay replay, and scoped payload-hashed idempotency ([outbox-and-cdc](docs/patterns/outbox-and-cdc.md), [idempotency](docs/patterns/idempotency.md)) |
| `coordination` | `FencedLeaseCoordinator`, `FencedResource` | Expiring leadership leases + strictly monotonic fencing tokens that block GC-paused zombie leaders ([leader-election](docs/patterns/leader-election.md)) |
| `saga` | `SagaOrchestrator` | Multi-step forward execution with reverse-order compensating actions and explicit compensation-failure tracking ([saga](docs/patterns/saga.md)) |

### Running Tests

From the repository root:

```bash
./gradlew :system-design:test
```

---

## Hands-On Labs

- **[Change Data Capture (`labs/change-data-capture/`)](labs/change-data-capture/README.md)** — Runnable Docker Compose lab streaming PostgreSQL logical-WAL changes through Debezium and Kafka into Elasticsearch (materializes the [Transactional Outbox & CDC](docs/patterns/outbox-and-cdc.md) pattern).
