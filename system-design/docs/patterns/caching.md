# Caching

**Use when:** reads are frequent, data is reused, and recomputation or origin
access is expensive enough that some staleness is acceptable.

## Common cache locations

- Browser or device cache for private, user-controlled reuse.
- CDN or reverse-proxy cache for geographically distributed responses.
- Service-local cache for very low latency, at the cost of duplication.
- Distributed cache shared by service instances.
- Database buffer and query caches, which should not replace application-level
  reasoning about access patterns.

## Access patterns

| Pattern | Flow | Best fit | Main risk |
| --- | --- | --- | --- |
| Cache-aside | App reads cache, loads origin on miss, then fills cache | General read caching | Stale entries and miss storms |
| Read-through | Cache provider loads missing values | Centralized loading logic | Provider coupling |
| Write-through | Write cache and backing store synchronously | Read-after-write behavior | Higher write latency |
| Write-behind | Accept cache write, persist later | High write throughput | Data loss and ordering complexity |
| Refresh-ahead | Refresh hot values before expiry | Predictable hot reads | Wasted refresh work |

## Design the policy

Define:

- Key structure, including tenant and authorization boundaries.
- Value size and serialization format.
- TTL based on acceptable staleness, not an arbitrary round number.
- Eviction policy and memory budget.
- Invalidation on updates, or versioned keys for immutable versions.
- Behavior when the cache is unavailable.

A rough benefit calculation is:

```text
origin RPS = total read RPS x (1 - cache hit ratio)
```

The hit ratio must be measured by useful dimensions. A 95% global hit rate can
hide a tenant or endpoint with near-zero hits.

## Failure modes

- **Stampede:** many callers regenerate one expired key. Use request coalescing,
  a short lock, probabilistic early refresh, or stale-while-revalidate.
- **Penetration:** repeated misses for nonexistent keys. Cache negative results
  briefly, validate input, or use a Bloom filter where appropriate.
- **Hot key:** one key overwhelms a shard. Replicate it, use local caches, or
  split the value if semantics permit.
- **Mass expiry:** synchronized TTLs create an origin spike. Add TTL jitter.
- **Cache outage:** all traffic falls back to the database. Bound concurrency
  and shed optional load so the cache failure does not become a database
  failure.
- **Stale or unsafe data:** invalidation races and incomplete cache keys can
  expose old or cross-user data.

## Interview cues

Say what is cached, where it is cached, the TTL, and how updates invalidate it.
Then explain whether stale data is acceptable and how the origin survives a
low-hit-rate event.
**Sound bite:** "A cache trades freshness and invalidation complexity for lower
latency and origin load; its fallback path must be capacity-safe."
