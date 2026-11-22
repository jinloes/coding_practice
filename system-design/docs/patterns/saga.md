# Saga

**Use when:** one business workflow updates data owned by multiple services and
a single local transaction cannot cover it.

A saga is a sequence of local transactions. Each successful step has a
compensating action for failures that occur later.

```text
reserve inventory -> authorize payment -> create shipment
       |                    |
release inventory    void authorization
```

Compensation is not a database rollback. It is a new business operation and may
fail, be delayed, or be unable to perfectly undo a real-world effect.

## Coordination styles

| Style | Mechanics | Strength | Risk |
| --- | --- | --- | --- |
| Choreography | Services react to each other's events | Loose runtime coupling | Workflow becomes hard to see and cycles emerge |
| Orchestration | A coordinator commands steps and records state | Explicit control and observability | Coordinator owns more workflow logic |

Prefer orchestration for long, branching, or business-critical workflows.
Choreography can remain clear for short, linear flows with few participants.

## Design each step

- Give the saga and each command stable IDs.
- Make forward and compensating actions idempotent.
- Persist workflow state before emitting the next command, often with an outbox.
- Define timeouts and distinguish retryable technical errors from business
  rejection.
- Store enough data to compensate later.
- Model terminal states such as completed, compensated, and manual review.
- Expose progress instead of holding an HTTP request open for a long workflow.

## Isolation problems

Other requests can observe intermediate saga state. Mitigations include semantic
locks, reservations with expiry, version checks, commutative updates, and
re-reading before a critical step. These are domain-specific; sagas do not
provide serializable isolation across services.

## Failure modes

- A compensation can fail and require retry or human intervention.
- Events can arrive out of order or more than once.
- A service may change behavior while an old saga remains active.
- Choreography can create invisible dependencies and event loops.
- Holding inventory or funds too long reduces business availability.

## Interview cues

Describe one happy path and one compensation path with persisted state.
Explain who decides the next step, which actions are idempotent, and what a user
sees while the saga is incomplete.
**Sound bite:** "A saga preserves a business invariant through durable local
steps and compensations; it trades global atomicity for availability and
explicit workflow state."
