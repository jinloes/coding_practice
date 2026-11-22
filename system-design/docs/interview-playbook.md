# The 45-minute interview playbook

Use the same process for every prompt. The times are guidelines, not rigid
phases; narrate transitions and adjust when the interviewer redirects you.

## 0-5 minutes: clarify the product

Identify the actors and the three or four critical behaviors. Ask questions
that can change the architecture:

- What is in scope for this design?
- How many daily active users, and what is the read/write ratio?
- What object sizes and retention period should I assume?
- Is the product global?
- Which operation needs the strongest consistency?
- What latency and availability targets matter?
- Are abuse prevention, privacy, or compliance central to the prompt?

End with a concise contract: "I will design post creation and home-feed reads
for 100 million users. Feed reads target p99 under 300 ms, may be five seconds
stale, and should survive one availability-zone failure."

## 5-10 minutes: estimate the forcing functions

Calculate only numbers that influence a choice:

- Peak read and write RPS.
- Storage growth and retention.
- Peak ingress and egress bandwidth.
- Fan-out or amplification factor.
- Number of simultaneously open connections for chat or streaming.

State assumptions and round aggressively. A good estimate explains why a cache,
queue, partitioning scheme, or object store is needed.

## 10-15 minutes: define API and data

Write the core endpoints or message contracts and a minimal data model. Mark:

- Primary keys and likely partition keys.
- Important indexes and uniqueness constraints.
- Mutable versus immutable fields.
- Idempotency keys and version fields.
- Large blobs stored outside the primary database.

This prevents the diagram from becoming a collection of generic boxes.

## 15-25 minutes: draw the end-to-end design

Start with one readable path:

```text
client -> edge -> service -> primary data store
                         -> asynchronous work
```

Then add only components justified by a requirement: cache, replicas, queue,
search index, object storage, or regional routing. Draw both the write path and
the read path. Label synchronous calls, asynchronous events, data ownership,
and consistency boundaries.

Avoid naming a vendor when the important decision is the abstraction. "A
partitioned log with per-key ordering" communicates more than a product logo.

## 25-38 minutes: deep dive into the hard part

The interviewer normally wants one or two mechanisms in detail. Good deep dives
include:

- Partition key selection and rebalancing.
- Cache invalidation and hot-key protection.
- Feed fan-out and the celebrity problem.
- Ordering, deduplication, and consumer retries.
- Replication, failover, and read consistency.
- Rate-limiter state across many edge nodes.
- Multi-service workflows and compensation.

Explain a concrete sequence, not just the pattern name. State what is committed,
what is acknowledged, what can be duplicated, and what happens after a crash.

## 38-45 minutes: test the design

Walk through the most important failure and the next scale threshold:

- A host, zone, or region fails.
- A dependency slows down instead of failing cleanly.
- A queue consumer is offline for an hour.
- One tenant or key becomes 1,000x hotter than average.
- A request is retried after the response is lost.
- A schema or event changes during a rolling deployment.

Close with explicit trade-offs and the next bottleneck. Do not claim unlimited
scale or exactly-once behavior without defining its boundary.

## A useful narration pattern

For each major decision, use:

> Because **requirement**, I will use **mechanism**, which gives us **benefit**.
> The cost is **trade-off**. I would monitor **signal** and revisit this when
> **threshold**.

Example:

> Because feed reads outnumber writes 100 to 1, I will precompute feed entries
> for normal users. This gives predictable read latency. The cost is write
> amplification, so high-fan-out accounts use fan-out on read. I would monitor
> queue age and generated feed entries per post.

## Common interview mistakes

| Mistake | Better approach |
| --- | --- |
| Starting with a giant diagram | Lock scope and requirements first. |
| Listing technologies without mechanics | Describe ownership, data flow, and guarantees. |
| Saying "eventual consistency" everywhere | Name which read can be stale and by how much. |
| Claiming a queue prevents data loss | Explain producer acknowledgment, replication, retention, and retries. |
| Ignoring overload | Set deadlines, queue bounds, limits, and degradation behavior. |
| Sharding immediately | Start simple, then state the threshold that requires partitioning. |
| Treating a cache as free | Cover invalidation, stampedes, hot keys, and fallback load. |
| Solving every edge case | Prioritize the hardest requirement and follow interviewer signals. |

## End-of-design checklist

- Scope, users, and non-functional requirements are explicit.
- Estimates justify the major scaling decisions.
- APIs and data model support the requested behavior.
- Read and write paths are both visible.
- Every stateful component has an owner, partitioning strategy, and recovery
  story.
- Consistency is specified per operation.
- Retries are bounded and safe.
- Overload, partial failure, and observability are covered.
- Security and abuse controls are placed at real boundaries.
- The final trade-offs and next bottleneck are stated.
