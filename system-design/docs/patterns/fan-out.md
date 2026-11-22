# Fan-out

**Use when:** one write must become visible to many recipients, as in feeds,
notifications, subscription updates, and cache invalidations.

## Fan-out strategies

| Strategy | Write path | Read path | Best fit |
| --- | --- | --- | --- |
| Fan-out on write | Push an item into each recipient's materialized inbox | Read one precomputed list | Read-heavy systems with moderate audience size |
| Fan-out on read | Store once; gather followed sources at read time | Merge and rank many sources | Write-heavy systems or very large audiences |
| Hybrid | Push for ordinary sources, pull large or special sources | Merge inbox with pulled sources | Highly skewed follower distributions |

## Fan-out on write

The service commits the source item, publishes an event, and workers append a
lightweight item reference to recipient inboxes. Reads are fast and pagination
is straightforward, but a source with millions of followers creates write
amplification and propagation delay.

Do not place every recipient write in the original request. Queue bounded
batches, make each insertion idempotent, and monitor oldest-event age.

## Fan-out on read

At read time, fetch recent candidates from followed sources, merge by time or
score, rank, and paginate. Writes are cheap, but reads can touch many partitions
and have unpredictable tail latency. Cache source timelines and cap candidate
work.

## Hybrid design

Classify sources by fan-out cost. Precompute ordinary updates, but store
high-follower updates once and merge them into a user's feed during reads. The
threshold should follow measured audience size and posting frequency, not a
hard-coded notion of "celebrity."

## Other decisions

- Store item IDs and ranking metadata in inboxes, then hydrate content in
  batches to avoid duplication.
- Define behavior for follow/unfollow, deletions, blocks, and privacy changes.
- Use cursor pagination with a stable score and tie-breaker.
- Separate candidate generation from ranking so each can evolve.
- Expire old materialized entries or compact them according to product needs.

## Interview cues

Compute write amplification: posts per second times average followers. Then
address the skewed tail, propagation SLO, duplicate events, deletion, and queue
backlog.

**Sound bite:** "Fan-out on write buys predictable reads with write
amplification; a hybrid path isolates high-fan-out sources so the tail does not
dominate the whole system."
