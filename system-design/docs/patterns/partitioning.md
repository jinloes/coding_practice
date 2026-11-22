# Partitioning and consistent hashing

**Use when:** data, write throughput, or ownership exceeds one node's capacity.

## Partitioning strategies

| Strategy | Strength | Weakness |
| --- | --- | --- |
| Hash by key | Even distribution for random keys | Range scans and resharding are harder |
| Range by key or time | Efficient range queries | Sequential keys can create a hot partition |
| Directory lookup | Flexible placement | Directory is an extra dependency |
| Geographic or tenant partition | Locality and policy isolation | Skew between regions or tenants |

A good partition key has high cardinality, spreads expected load, preserves the
most important query locality, and has stable semantics. `user_id` may suit a
per-user timeline; `timestamp` alone often concentrates current writes.

## Consistent hashing

Modulo hashing (`hash(key) % node_count`) remaps most keys when the node count
changes. Consistent hashing places nodes and keys on a logical ring; each key is
owned by the next node clockwise. Adding a node moves only a portion of keys.

In practice:

- Give each physical node many virtual nodes for smoother distribution.
- Replicate to subsequent owners for availability.
- Track membership and ring versions consistently.
- Move data gradually and support reads during handoff.

Rendezvous hashing is a simpler alternative: score every candidate node for a
key and choose the highest scores. It also limits movement as membership
changes, though evaluating many nodes may need optimization.

## Cross-partition costs

Partitioning makes these operations more expensive:

- Joins and transactions across keys.
- Global uniqueness constraints.
- Secondary indexes and broad scans.
- Aggregation and globally ordered pagination.
- Rebalancing, backup, and repair.

Common responses are denormalized local views, scatter-gather with strict
bounds, a separate search or analytics system, and application-generated IDs
that do not require a global sequence.

## Failure modes

- **Hot partition:** a celebrity, tenant, or current time range dominates.
  Split further, salt keys, isolate large tenants, or special-case the access
  pattern.
- **Uneven data size:** equal key counts do not imply equal bytes or traffic.
- **Rebalancing overload:** throttle movement and retain headroom.
- **Routing mismatch:** stale clients send requests to the former owner. Use a
  routing layer, redirects, and versioned metadata.

## Interview cues

Do not shard by habit. State the single-node threshold first, then choose a key
from actual queries and describe hot-key handling plus rebalancing.
**Sound bite:** "The partition key decides both scalability and which queries
remain cheap; consistent hashing reduces movement, but it does not solve skew."
