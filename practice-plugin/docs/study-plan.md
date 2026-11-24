# DSA Interview Study Plans

Two structured schedules built around the [Algorithm Practice plugin](../README.md)'s 18 study guides (`src/main/resources/guides/index.txt`) and 15 tracks (`src/main/resources/tracks/index.txt`).

Pick the **4-Week Sprint** if an interview is ~1 month away, or the **8-Week Plan** for deeper coverage and company-specific polish.

---

## Daily Practice Protocol (60–90 min/day)

1. **Attempt cold (25 min for Medium, 40 min for Hard):** Read the problem, state constraints + target Big-O out loud, implement, and dry-run edge cases before running **Check Solution**.
2. **Unstick cleanly:** If stuck at 15 minutes with no viable approach, reveal **one** hint. If still stuck at 25 minutes, review the technique guide and solve it, then mark it for spaced repetition.
3. **Post-pass review (5–10 min):** Check the complexity probe output, run **AI Coach → Compare Approaches** or **Follow-Up**, and write a 1-sentence takeaway ("signal → invariant → complexity").
4. **Spaced repetition:** Re-solve any problem that needed a hint or failed its first check cold **3 days later** and **14 days later**.

---

## Option A: 4-Week Sprint (Blind 75 + Core Patterns)

Aim for 3–4 problems per weekday and 1 timed mock session each weekend (~75–85 problems total). Use the **`blind-75`** track in the plugin as your core checklist.

| Week | Focus Guides (Plugin IDs) | Goals & Weekend Milestone |
|---|---|---|
| **Week 1: Linear & Hashing** | `hash-maps`, `two-pointers`, `sliding-window`, `prefix-sums`, `stacks-queues` | Master `O(n)` array/string passes and monotonic stacks. **Weekend:** 2 timed Mediums (45 min total). |
| **Week 2: Pointers, Search & Heaps** | `linked-lists`, `binary-search`, `intervals`, `heaps` | Nail pointer manipulation, binary search on answer, interval sweeps, and top-K/two-heap patterns. **Weekend:** Re-solve Week 1 misses + 1 timed mock. |
| **Week 3: Trees, Tries & Graphs** | `trees`, `tries`, `graphs`, `matrices` | Build fluency in DFS/BFS, BST invariants, topological sort, Dijkstra, and Union-Find. **Weekend:** 2 timed graph/tree Mediums. |
| **Week 4: Recursion, DP & Mocks** | `backtracking`, `dynamic-programming`, `greedy`, `bit-manipulation`, `math` | 1D/2D DP state transitions, backtracking pruning, and mixed-topic timed drills. **Weekend:** 2 full 45-min mocks (unknown topic, no hints). |

---

## Option B: 8-Week Comprehensive Plan (NeetCode 150 + Company Tracks)

Aim for 2–3 problems per weekday + weekend review (~150+ problems). Use **`neetcode-150`** as your spine, then switch to your target company track in Weeks 7–8.

- **Week 1:** `hash-maps`, `two-pointers`, `prefix-sums`
- **Week 2:** `sliding-window`, `stacks-queues`, `linked-lists`
- **Week 3:** `binary-search`, `intervals`, `heaps`
- **Week 4:** `trees`, `tries` + cumulative spaced-repetition review of Weeks 1–3
- **Week 5:** `graphs` (BFS/DFS, topo sort, Union-Find, shortest paths), `matrices`
- **Week 6:** `backtracking`, `greedy`, `bit-manipulation`, `math`
- **Week 7:** `dynamic-programming` (1D, 2D, knapsack, strings, state machines) + mixed Hard problems
- **Week 8:** Target company track (`google`, `meta`, `amazon`, `apple`, `atlassian`, `stripe`, `anthropic`, `databricks`, `openai`, `uber-doordash`, `microsoft`, `coinbase-airbnb`, `bloomberg-citadel`) + 3–4 full 45-minute timed mocks

---

## Readiness Self-Check

Before your interview loop, verify you can consistently:
- [ ] Identify the right pattern and target Big-O within 5 minutes on unseen Medium problems.
- [ ] Write bug-free Java for binary search, sliding window, BFS/DFS, topological sort, Union-Find, and 1D/2D DP from scratch without autocomplete crutches.
- [ ] Dry-run your own code on edge cases and catch off-by-one or overflow bugs *before* running tests.
- [ ] Finish an unseen Medium in ≤25 minutes and explain time/space complexity and one follow-up twist clearly.
