# Pattern catalog

Patterns are responses to specific pressures, not default architecture boxes.
Start with the simplest design that meets the requirements, then introduce a
pattern when you can name the bottleneck or failure it addresses.

## Choose by pressure

| Pressure | Primary pattern | Often paired with |
| --- | --- | --- |
| Global or bursty request traffic | [Load balancing and edge delivery](traffic-management.md) | Caching, rate limiting |
| Repeated or expensive reads | [Caching](caching.md) | Replication, materialized views |
| Fair use and overload protection | [Rate limiting](rate-limiting.md) | Load shedding, quotas |
| Read scale and failover | [Replication](replication.md) | Read routing, consistency tokens |
| Data or ownership exceeds one node | [Partitioning and consistent hashing](partitioning.md) | Replication, routing |
| Work should be decoupled in time | [Queues, pub/sub, and logs](messaging.md) | Idempotency, backpressure |
| Requests or events can be repeated | [Idempotency and deduplication](idempotency.md) | Retries, inbox table |
| Dependencies are slow or unreliable | [Resilience patterns](resilience.md) | Bulkheads, load shedding |
| A database write must cause an event | [Transactional outbox and CDC](outbox-and-cdc.md) | Idempotent consumers |
| One workflow spans service-owned data | [Saga](saga.md) | Outbox, compensation |
| Read and write models need different shapes | [CQRS and event sourcing](cqrs-and-event-sourcing.md) | Projections, CDC |
| One write affects many recipients | [Fan-out](fan-out.md) | Queues, hybrid materialization |
| One coordinator must be active | [Leader election and fencing](leader-election.md) | Leases, consensus |

## Learn each pattern with the same questions

1. **Pressure:** What measurable problem justifies it?
2. **Mechanism:** What sequence of reads, writes, and acknowledgments occurs?
3. **Guarantee:** What does it make stronger, faster, or more available?
4. **Cost:** What complexity, staleness, or amplification does it add?
5. **Failure:** What happens on a crash, partition, retry, or overload?
6. **Alternative:** What simpler design could work at the stated scale?

Common combinations matter more than isolated definitions. A queue normally
implies at-least-once delivery, which implies idempotent consumers. A read
replica implies lag, which implies an explicit read-after-write strategy. A
cache implies invalidation and a database fallback that must survive misses.
