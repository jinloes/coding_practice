# 08 Staff Engineer: Leadership, Influence, and Execution

At Staff level the bar is less "can you code" and more "can you set direction for an area and land
multi-quarter, multi-team work." Expect a leadership/behavioral round and leadership signals in design rounds.

## What evaluators look for

| Signal | What it looks like |
|---|---|
| Scope | You owned outcomes across several teams or an entire problem space, not a single feature |
| Ambiguity | You turned a vague problem ("our secrets are a mess") into a strategy, a roadmap, and milestones |
| Technical judgment | You made trade-offs explicit, chose boring tech where appropriate, and can explain what you'd do differently |
| Influence without authority | You got teams that don't report to you to change behavior |
| Multiplying others | Mentoring, raising the review bar, reusable platforms, docs, and tooling |
| Operational ownership | You care about incidents, SLOs, on-call health, and safe rollouts |
| Communication | You adapt to execs, partner engineers, security/compliance, and juniors |

## Story bank (prepare 6 to 8, in STAR(R) format)

**Situation, Task, Action, Result, Reflection.** Quantify results. Spend about 60% of the time on Actions,
and say "I" for your own actions.

Prompts to prepare stories for:

1. A large cross-team program you drove end to end (migration, platform adoption).
2. A time you changed a technical direction others were committed to.
3. A design you got wrong, how you found out, and what you did.
4. A severe incident you led or were part of, and the systemic fixes afterwards.
5. Disagreement with a peer or manager, and how it was resolved.
6. Mentoring someone to a promotion or to owning an area.
7. Improving engineering productivity for many teams (tooling, CI, libraries, paved roads).
8. Balancing security against product velocity and finding a design that served both.
9. Saying no or descoping to hit a deadline.
10. Using AI tools to accelerate delivery while keeping quality.

Tie at least half of them to security, infrastructure, or reliability, since that matches the role.

## Driving a company-wide security program (template)

Example: "Every service must use short-lived credentials from the secrets platform by the end of next year."

1. **Define the problem with data**: inventory static secrets, ages, owners, incidents caused.
2. **Write the strategy doc**: goals, non-goals, success metrics, the platform investment needed,
   options considered, and the ask for partner teams.
3. **Get sponsorship**: security leadership + an engineering VP; agree on deadlines and exception process.
4. **Make adoption cheap**: SDKs, sidecar/agent, migration codemods, templates, a pilot with two
   friendly teams, published case study.
5. **Track and communicate**: per-org dashboards, monthly updates, escalate blockers early.
6. **Ratchet**: block new static secrets in CI and admission control once the paved road works.
7. **Close out**: decommission the old path, write the retrospective, hand off steady-state ownership.

## Influence and consensus techniques

- Write it down: a crisp design doc or one-pager beats ten meetings.
- Pre-wire: talk 1:1 with key stakeholders before the big review; incorporate their concerns.
- Separate **reversible vs irreversible** decisions; move fast on the former.
- Disagree and commit, and record decisions (ADRs) so they aren't reopened every quarter.
- Use prototypes and data (benchmarks, incident stats) to settle debates.
- Frame security asks in the partner's terms: fewer pages, faster audits, less toil.

## Design doc structure for a Staff-level proposal

1. Context and problem (with data)
2. Goals / non-goals
3. Requirements (functional, scale, latency, availability, security/compliance)
4. Proposed design (diagrams, APIs, data model, key hierarchy, failure modes)
5. Alternatives considered and why rejected
6. Security and threat model
7. Rollout, migration, and rollback plan
8. Operations: SLOs, alerts, runbooks, capacity
9. Risks, open questions, milestones, and staffing

## Synchronous vs asynchronous design trade-offs (from the job description)

| Choose synchronous when | Choose asynchronous when |
|---|---|
| Caller needs the result to proceed (decrypt on the read path) | Work can be deferred (re-encryption backfill, audit shipping) |
| Strong consistency is required (key disable must be immediate) | Throughput and smoothing load spikes matter more than latency |
| Simple failure semantics are preferred | Retries, idempotency, and DLQs are acceptable complexity |

KMS examples: `Decrypt` is synchronous with tight latency budgets and caching; audit log export,
rotation re-encryption jobs, and key usage analytics are asynchronous via queues/streams; key
revocation propagates by push + short cache TTLs (hybrid).

Async pitfalls to mention: at-least-once delivery needs idempotency keys, ordering per key,
backpressure, poison messages, and visibility into lag.

## Using AI for production code responsibly

Interviewers increasingly ask about this. A strong answer:

- **Where it helps**: boilerplate, tests (especially negative and edge-case tests), migrations and
  codemods across many repos, explaining unfamiliar code, drafting docs and runbooks.
- **Where to be careful**: cryptographic code and security-sensitive logic. Models produce plausible but
  subtly wrong crypto (static IVs, ECB, non-constant-time comparisons, missing AAD).
- **Guardrails**: same review bar as human code, mandatory tests and known-answer vectors, static
  analysis rules for crypto misuse, never paste secrets or proprietary data into external tools,
  and prefer approved enterprise tools with data protections.
- **Platform angle**: give AI agents context such as repo conventions and approved crypto APIs so
  they generate code on the paved road. Measure outcomes (cycle time, defect rate), not hype.

## Mentoring and growing the team

- Delegate ownership of meaningful components, not just tasks; stay available as a reviewer.
- Pair on design docs; give specific, timely feedback.
- Create learning paths (like this guide) and rotate people through on-call and incident command.
- Raise the bar through code review that teaches (explain why, link to references).
