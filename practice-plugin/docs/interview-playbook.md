# Coding Interview Playbook

A repeatable structure for a 45-minute data-structures-and-algorithms interview. Most strong candidates fail not because they don't know the algorithm, but because they code before aligning on constraints, miss an edge case, or stay silent while stuck.

---

## 45-Minute Pacing

| Time | Phase | Goal |
|---|---|---|
| **0–5 min** | Clarify & confirm examples | Eliminate ambiguity, lock down input bounds, types, and edge cases. |
| **5–12 min** | Approach & complexity | State a working brute force in 30 seconds, optimize to target Big-O, and get interviewer sign-off *before* coding. |
| **12–32 min** | Clean implementation | Write modular, readable code while narrating key invariants. |
| **32–40 min** | Dry-run & edge cases | Walk through a normal case and 2–3 edge cases line-by-line tracking variable state. |
| **40–45 min** | Complexity & follow-ups | Confirm final time/space complexity and discuss scale, streaming, or concurrency twists. |

---

## Phase 1: Clarifying Questions (Ask Only What Matters)

Don't recite a robotic checklist—pick the 2–4 questions that actually change the algorithm or data structure:

- **Input size & range:** What is the maximum `N`? Can values be negative, zero, or exceed 32-bit `int` (`long` needed)?
- **Ordering & duplicates:** Is the array sorted? Are elements unique or can duplicates appear?
- **Empty / malformed inputs:** Can the array, string, or tree be empty or `null`? Is a solution guaranteed to exist?
- **Character set / alphabet:** Lowercase ASCII (`a–z`, fixed 26-element array) or arbitrary Unicode (`HashMap`)?
- **Graph properties:** Directed or undirected? Weighted (negative weights?) or unweighted? Possibly disconnected or containing cycles / self-loops?
- **Mutability & memory:** May I mutate the input array/list in place, or must it remain unchanged? Is `O(1)` extra space required?
- **Tie-breaking & output format:** If multiple answers are valid, may I return any one, or the lexicographically smallest / earliest index?

Write down one small non-trivial example and confirm your expected output with the interviewer before designing.

---

## Phase 2: Brute Force → Optimal

1. **Name the brute force first:** *"The naive approach checks every pair in `O(n^2)` time and `O(1)` space."* This guarantees you have a baseline and clarifies what bottleneck you are eliminating.
2. **Identify the repeated work:** Ask *what work am I repeating?*
   - Repeated linear lookup → **Hash Map / Set**
   - Repeated subarray/range sum → **Prefix Sum** or **Sliding Window**
   - Repeated min/max over a changing set → **Heap** or **Monotonic Stack/Deque**
   - Repeated subproblem with overlapping states → **Dynamic Programming**
   - Monotonic feasibility predicate → **Binary Search on Answer**
3. **State invariants & complexity before typing:** Describe the core state (e.g., *"the window `[left, right]` always has at most `k` distinct characters"*) and state time and space complexity. Ask: *"Does this approach look good to implement?"*

---

## Phase 3: Clean Implementation Habits

- **Name variables for their role:** `left`/`right`, `slow`/`fast`, `inDegree`, `bestProfit` beat `i`/`j`/`a`/`b`/`res`.
- **Use helpers for noisy boilerplate:** Extract `inBounds(r, c)`, `swap(nums, i, j)`, or `dfs(...)` so the main control flow stays under ~25 lines.
- **Avoid premature micro-optimizations:** Clear, bug-free `O(n log n)` beats clever, buggy bit-twiddling.
- **Narrate transitions, not syntax:** Say *"now I'll shrink from the left until the window is valid again"* rather than *"for int left equals zero..."*.
- **If you get stuck:** Don't go silent for more than 30 seconds. Work through a concrete 4-element example by hand, check if sorting/reversing/precomputing helps, or share two competing ideas and ask which direction the interviewer prefers.

---

## Phase 4: Dry-Run & Edge-Case Checklist

Never say *"I'm done"* right after the last closing brace. Trace a small table of variable values by hand across:

- **Empty / single-element input:** `n = 0`, `n = 1`, `null` root, single-node linked list.
- **All-equal or strictly monotonic inputs:** `[5, 5, 5, 5]`, already sorted, reverse sorted.
- **Boundary indices:** First element, last element, target smaller than min or larger than max.
- **Numeric boundaries:** Negative numbers, zero, `Integer.MIN_VALUE` / `Integer.MAX_VALUE`, sum overflow into `long`, modulo with negative values (`((x % m) + m) % m`).
- **Graph / tree corner cases:** Disconnected components, single node with no edges, skewed tree (recursion depth `O(n)`), cycles in undirected/directed graphs.
- **Mutation & aliasing:** Copying a path (`new ArrayList<>(path)`) before adding it to a backtracking result list.

---

## Phase 5: Common Follow-Up Twists

Be ready to discuss how your solution adapts if:
- **Input doesn't fit in memory** → external merge sort, streaming top-K with a min-heap, chunked processing, or Bloom filter / HyperLogLog approximations.
- **Read-heavy vs write-heavy workload** → precompute / cache / Fenwick or segment tree vs lazy evaluation.
- **Multi-threaded / concurrent callers** → lock granularity, `ConcurrentHashMap`, immutability, or sharded locks.
- **Generalizing a parameter** → `k` instead of `2`, weighted edges instead of unweighted, returning the actual path/subarray instead of just its length or count.
