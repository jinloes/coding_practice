# System design foundations

These concepts are the inputs to pattern selection. In an interview, use them
to turn a vague prompt into explicit requirements and defensible decisions.

## 1. Start with requirements

Separate requirements before drawing an architecture.

**Functional requirements** describe user-visible behavior:

- Create, read, update, and delete which entities?
- Search, rank, recommend, notify, upload, or stream?
- Which operations are synchronous?
- Who can perform each operation?

**Non-functional requirements** describe system qualities:

- Scale: requests per second, data volume, object size, and growth.
- Latency: average is not enough; state a p95 or p99 target.
- Availability: which operations must survive a host, zone, or region failure?
- Consistency: where can users see stale data, and for how long?
- Durability: what data loss, if any, is acceptable?
- Security: authentication, authorization, abuse prevention, encryption, and
  sensitive-data boundaries.

Do not assume every requirement is equally strong. A design becomes clearer
when you say, for example, "writes require read-after-write consistency, while
the public feed may lag by five seconds."

## 2. Estimate only what affects the design

Round numbers are enough. State assumptions so the interviewer can correct
them.

```text
average RPS = daily operations / 86,400
peak RPS    = average RPS x peak factor
storage     = writes per day x bytes per write x retention days x replicas
bandwidth   = requests per second x average payload size
concurrency = requests per second x average latency in seconds
```

Example: 100 million reads per day is about 1,200 average RPS. At a 10x peak,
design for roughly 12,000 RPS. If each response is 20 KB, peak egress is about
240 MB/s before compression and cache hits. Those numbers justify horizontal
read scaling and edge caching; an exact result would not change the decision.

Useful orders of magnitude:

| Quantity | Approximation |
| --- | --- |
| Day | 86,400 seconds, often rounded to 100,000 |
| Million operations/day | About 12 average operations/second |
| 1 Gbps | About 125 MB/s before protocol overhead |
| Three nines availability | About 44 minutes of downtime/year |
| Four nines availability | About 53 minutes of downtime/year |

## 3. Understand state

A **stateless service** can process any request on any healthy instance because
durable session or domain state lives elsewhere. Stateless services are easier
to load balance, replace, and scale horizontally.

A **stateful service** owns durable or coordination state. Databases, brokers,
and consensus nodes are stateful. Scaling them requires replication,
partitioning, recovery, and explicit ownership.

Vertical scaling adds resources to one machine. It is simple but has a ceiling
and a larger failure domain. Horizontal scaling adds machines. It raises
capacity and availability but introduces distributed coordination.

## 4. Name the consistency guarantee

"Consistent" is too vague. Choose the guarantee needed by each operation:

- **Strong consistency:** a read observes the latest completed write.
- **Read-your-writes:** a user sees their own completed writes.
- **Monotonic reads:** a user does not move backward to older versions.
- **Causal consistency:** causally related operations are observed in order.
- **Eventual consistency:** replicas converge if writes stop.

During a network partition, a distributed operation cannot guarantee both
linearizable consistency and availability for every request. That is the useful
part of CAP; it is not a general instruction to choose "CP" or "AP" for an
entire product. PACELC adds the normal-operation trade-off: even without a
partition, lower latency often competes with stronger consistency.

Tie the choice to an invariant. Duplicate likes may be repairable. Charging an
account twice is not.

## 5. Choose storage from access patterns

Start with entities, relationships, invariants, and queries. Then select a
storage model.

| Need | Common fit | Watch for |
| --- | --- | --- |
| Transactions, constraints, joins | Relational database | Horizontal write scaling and hot indexes |
| Key-based access at high scale | Key-value store | Limited query flexibility |
| Flexible aggregate documents | Document database | Duplicated data and cross-document invariants |
| Large immutable blobs | Object storage | Metadata lookup and upload lifecycle |
| Full-text and relevance queries | Search index | It is usually a derived, eventually consistent view |
| Ordered event retention and replay | Distributed log | Retention, partition ordering, and consumer lag |
| Relationship traversal | Graph database | Operational complexity and partitioning |
| Time-window aggregation | Time-series store | Cardinality and retention |

Indexes exchange write cost and storage for faster reads. A query is not
"indexed" in the abstract: column order, selectivity, sort order, and covering
fields determine whether an index helps.

## 6. Model APIs around behavior

Define a small API surface before drawing internals. It exposes request size,
read/write ratio, consistency boundaries, and pagination needs.

```http
POST /v1/posts
GET  /v1/feeds/{user_id}?cursor=...
PUT  /v1/follows/{followee_id}
```

Prefer cursor pagination for changing, large result sets. Offset pagination
becomes expensive at high offsets and can skip or duplicate items when rows are
inserted concurrently.

For mutating requests, discuss authentication, authorization, validation,
idempotency, and a stable error contract. For large objects, clients commonly
upload directly to object storage through a short-lived signed URL rather than
proxying bytes through application servers.

## 7. Treat reliability as a budget

End-to-end availability cannot exceed the availability of every dependency on
the critical path. Reduce synchronous dependencies, define timeouts, and
degrade optional features.

For each component, ask:

- What happens when it is slow, unavailable, or returning bad data?
- Can the caller retry safely?
- Is there a queue or buffer, and what happens when it fills?
- How is state rebuilt after replacement?
- Which metric detects the problem before users report it?

Use service-level indicators such as latency, traffic, errors, saturation,
consumer lag, cache hit rate, replication lag, and queue age. Logs explain
events, metrics show trends, and traces connect one request across services.

## 8. Include security in the architecture

At minimum, cover:

- Authentication at the edge and authorization at the resource boundary.
- TLS in transit and encryption plus key management at rest.
- Rate limits and quotas by user, tenant, IP, or API credential.
- Input and file validation before storage or processing.
- Least-privilege service identities and secret rotation.
- Audit logs for sensitive mutations.
- Data classification, retention, deletion, and regional residency.

Security decisions can affect partition keys, cache keys, log content, and
whether derived data may cross regional boundaries. They are not a final box
labeled "security."

## Compact glossary

| Term | Meaning |
| --- | --- |
| Throughput | Work completed per unit of time |
| Latency | Time required for one operation |
| Availability | Fraction of valid requests the system can serve |
| Durability | Probability that acknowledged data remains intact |
| Backpressure | Slowing or rejecting producers when consumers cannot keep up |
| Hot key | A key receiving disproportionate traffic |
| Replica lag | Delay between a write and its visibility on a replica |
| Partition | A subset of data or work assigned to one ownership group |
| Quorum | Minimum participants required to accept or observe an operation |
| SLO | Target reliability level for a measured service indicator |
