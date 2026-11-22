# Queues, pub/sub, and distributed logs

**Use when:** work should be decoupled in time, producers and consumers scale
independently, bursts need buffering, or multiple consumers need the same event.

## Choose the abstraction

| Abstraction | Consumption model | Good fit |
| --- | --- | --- |
| Work queue | One worker handles each item | Jobs, email delivery, media processing |
| Pub/sub | Each subscription receives a copy | Notifications and integration events |
| Retained log | Consumers track offsets and can replay | Event streams, CDC, projections, analytics |

A broker does not make work reliable by itself. Define when the producer gets
an acknowledgment, how many broker copies exist, when the consumer acknowledges,
and how long messages remain available.

## Delivery and ordering

- **At-most-once:** loss is possible, duplicates are avoided.
- **At-least-once:** retry prevents most loss, duplicates are possible.
- **Effectively once:** at-least-once delivery plus idempotent processing makes
  the externally visible result occur once within a defined boundary.

Global ordering is expensive and rarely required. Partition by an entity key
when events for one entity need order, then process each partition serially or
with sequence checks.

## Consumer design

1. Validate the schema and version.
2. Check idempotency or an inbox record.
3. Apply the domain change and deduplication marker atomically where possible.
4. Acknowledge only after durable completion.
5. Retry transient failures with bounded exponential backoff and jitter.
6. Quarantine poison messages after a policy threshold, with alerting and a
   replay process.

Scale consumers from lag and processing time, not only queue depth. Preserve
headroom for catch-up after an outage.

## Failure modes

- An unbounded queue hides overload until latency becomes hours.
- A poison event can block an ordered partition.
- Retrying immediately can overwhelm a recovering dependency.
- Large events consume broker memory and network; store blobs externally.
- Schema changes can break old consumers. Use additive evolution, versioning,
  and compatibility checks.
- A dead-letter queue without an owner and replay procedure is just silent data
  loss.

## Interview cues

Say why the work is asynchronous, which key determines ordering, what delivery
guarantee applies, when acknowledgment happens, and how lag is bounded.
**Sound bite:** "The broker decouples availability and absorbs a bounded burst;
durable acknowledgments and idempotent consumers provide the actual reliability."
