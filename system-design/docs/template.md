# System design interview template

Copy this outline onto a whiteboard or blank page before a mock interview.

## 1. Scope

**Users and actors:**

**Three critical use cases:**

1.
2.
3.

**Out of scope:**

## 2. Non-functional requirements

| Concern | Target |
| --- | --- |
| Peak traffic | |
| Payload or object size | |
| Latency | |
| Availability | |
| Consistency | |
| Durability and retention | |
| Geography and compliance | |

## 3. Estimates

```text
average RPS =
peak RPS =
storage per day =
storage over retention =
peak bandwidth =
fan-out or other amplification =
```

**Decision changed by these estimates:**

## 4. API and data model

```http

```

```text
Entity(primary_key, important_fields, version)
```

**Partition key:**

**Indexes:**

**Idempotency key:**

## 5. High-level design

Draw and label:

- Client, edge, and synchronous request path.
- System of record.
- Cache or read model, if justified.
- Asynchronous boundary and acknowledgment point.
- Read path and write path.
- Data ownership and regional boundary.

## 6. Deep dive

**Hardest mechanism:**

**Step-by-step sequence:**

1.
2.
3.

**Guarantee provided:**

**Trade-off accepted:**

**Simpler alternative:**

## 7. Failure and overload

| Scenario | Detection | Behavior and recovery |
| --- | --- | --- |
| Dependency is slow | | |
| Request or event is duplicated | | |
| Queue or cache is unavailable | | |
| One key or tenant becomes hot | | |
| Zone or region fails | | |

## 8. Close

**Primary trade-off:**

**Likely next bottleneck:**

**Metric that would trigger redesign:**
