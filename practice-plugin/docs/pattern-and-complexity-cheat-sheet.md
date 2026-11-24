# Pattern Recognition & Complexity Cheat Sheet

A quick reference for mapping problem constraints to target complexity, recognizing which algorithmic pattern fits, and avoiding common Java pitfalls during interviews.

---

## Input Size (`N`) → Target Complexity Heuristic

Modern judges and interview benchmarks execute roughly `~10^7–10^8` simple operations per second:

| Max `N` | Target Time Complexity | Typical Techniques |
|---|---|---|
| `N ≤ 12` | `O(n!)` or `O(n · 2^n)` | Permutations, TSP bitmask DP, full backtracking |
| `N ≤ 20–25` | `O(2^n)` or `O(n · 2^n)` | Subsets backtracking, meet-in-the-middle, bitmask DP |
| `N ≤ 100` | `O(n^3)` | Floyd-Warshall, interval DP, 3D DP |
| `N ≤ 2,000` | `O(n^2)` | 2D DP, nested loops, all-pairs checks |
| `N ≤ 10^5` | `O(n log n)` | Sorting, binary search on answer, heap, divide & conquer, Fenwick/segment tree |
| `N ≤ 10^6` | `O(n)` | Hash map, two pointers, sliding window, monotonic stack/deque, prefix sums, BFS/DFS |
| `N ≥ 10^9` | `O(log n)` or `O(1)` | Binary search, math/closed form, binary exponentiation, bit manipulation |

---

## Signal → Pattern Mapping (Aligned with Plugin Guides)

Each pattern below maps directly to a study guide in `practice-plugin/src/main/resources/guides/index.txt`:

| Problem Signal | Pattern (Plugin Guide) | Core Idea / Invariant |
|---|---|---|
| Need fast lookup, frequency count, grouping, or complement pair | **`hash-maps`** | Trade `O(n)` space to turn `O(n)` lookup into `O(1)` average. |
| Sorted array/string, pair/triplet sum, palindrome, in-place dedupe/partition | **`two-pointers`** | Move `left`/`right` (or `slow`/`fast`) monotonically; each element visited `O(1)` times. |
| Contiguous subarray/substring with min/max length or count under a monotonic constraint | **`sliding-window`** | Expand `right`; shrink `left` while invariant is violated (or satisfied for min-window). |
| Next greater/smaller element, matching brackets, expression parsing, sliding-window max | **`stacks-queues`** | Monotonic stack/deque keeps only candidates that can still be an answer. |
| In-place reversal, cycle detection, midpoint, merge, LRU cache | **`linked-lists`** | Dummy head eliminates head-null branches; fast/slow pointers find cycles and midpoints. |
| Sorted array, rotated array, or monotonic `isFeasible(x)` predicate ("minimize the maximum") | **`binary-search`** | Maintain explicit loop invariant (`[lo, hi]` or `[lo, hi)`) and avoid overflow with `lo + (hi - lo) / 2`. |
| Range sum queries, subarray sum equals `k` (with negatives!), 2D submatrix sum | **`prefix-sums`** | `sum(i..j) = prefix[j+1] - prefix[i]`; combine with a hash map of prior prefix counts. |
| Top-K, K-th largest/smallest, K-way merge, streaming median, task scheduling | **`heaps`** | Min-heap of size `k` for top-K largest; two heaps (max-left, min-right) for running median. |
| Shortest path, connected components, topological order, islands, dependencies | **`graphs`** | BFS (unweighted shortest path), DFS (components/cycles), Kahn's BFS (topo sort), Dijkstra (non-negative weights), Union-Find (dynamic connectivity). |
| Hierarchical data, paths, ancestors, BST validation/range, serialization | **`trees`** | Post-order for bottom-up aggregation, pre-order for top-down state, in-order for sorted BST traversal, BFS for level-order. |
| Prefix search, autocomplete, dictionary word search on a grid | **`tries`** | Tree keyed by character; `O(L)` lookup/insert independent of dictionary size. |
| Generate all subsets, permutations, combinations, valid placements (N-Queens, Sudoku) | **`backtracking`** | Choose → recurse → undo; prune invalid branches early; copy path on record. |
| Optimal value, number of ways, or yes/no feasibility with overlapping subproblems | **`dynamic-programming`** | Define state + transition + base case; top-down memoization first, then bottom-up + space compression. |
| Overlapping time ranges, meeting rooms, calendar booking, merge/insert intervals | **`intervals`** | Sort by start time (merge/overlap) or end time (max non-overlapping greedy); min-heap of end times for room count. |
| Single unique element, power of two, subsets via bitmask, XOR tricks | **`bit-manipulation`** | `x ^ x == 0`, `x & (x - 1)` clears lowest set bit, `x & -x` isolates lowest set bit. |
| 2D grid rotation, spiral traversal, sorted matrix search, in-place markers | **`matrices`** | Layer-by-layer bounds (`top/bottom/left/right`) or `transpose + reverse` for 90° rotation. |
| Locally optimal choice provably leads to global optimum (jump game, gas station, scheduling) | **`greedy`** | Verify the exchange argument—if a counterexample breaks the local choice, switch to DP. |
| GCD/LCM, primes, modular arithmetic, fast exponentiation, combinatorics | **`math`** | Euclidean GCD `O(log min(a,b))`, Sieve of Eratosthenes `O(n log log n)`, binary exponentiation `O(log e)`. |

---

## Core Data Structure Operations (Java)

| Structure | Java Type | Access / Search | Insert / Delete | Notes |
|---|---|---|---|---|
| Dynamic array | `ArrayList<T>` | `O(1)` by index / `O(n)` search | `O(1)` amortized tail / `O(n)` mid | Prefer over `LinkedList` for cache locality |
| Hash map / set | `HashMap` / `HashSet` | `O(1)` avg | `O(1)` avg | `LinkedHashMap` preserves insertion/access order (LRU) |
| Balanced BST | `TreeMap` / `TreeSet` | `O(log n)` | `O(log n)` | Supports `floorKey`, `ceilingKey`, `subMap` |
| Stack / Queue / Deque | `ArrayDeque<T>` | `O(1)` ends | `O(1)` ends | **Prefer over legacy `Stack` and `LinkedList`** (no `null` elements) |
| Binary heap | `PriorityQueue<T>` | `O(1)` peek / `O(n)` contains | `O(log n)` offer/poll | Min-heap by default; `remove(obj)` is `O(n)` |
| String builder | `StringBuilder` | `O(1)` charAt | `O(1)` amortized append | Avoid `s += ch` in loops (`O(n^2)`) |

---

## Java Interview Idioms & Pitfalls

- **Comparator overflow:** Never write `(a, b) -> a - b` when values can be negative (`Integer.MIN_VALUE - 1` overflows). Always use `Integer.compare(a, b)` or `Comparator.comparingInt(...)`.
- **Max-heap:** `new PriorityQueue<>(Comparator.reverseOrder())` or `new PriorityQueue<>((a, b) -> Integer.compare(b, a))`.
- **Binary search midpoint:** Always `int mid = lo + (hi - lo) / 2;` (never `(lo + hi) / 2`).
- **Sorting primitives vs objects:** `Arrays.sort(int[])` uses Dual-Pivot Quicksort (`O(n^2)` worst case on adversarial inputs, `O(log n)` space); `Collections.sort` / `Arrays.sort(T[])` uses TimSort (`O(n log n)` worst case, stable).
- **Map updates:** Use `map.merge(k, 1, Integer::sum)` for counts and `map.computeIfAbsent(k, ignored -> new ArrayList<>()).add(v)` for adjacency lists / grouping.
- **Integer caching trap:** `Integer` caches `-128..127`, so `==` works on small numbers and silently fails on `128`. Always use `.equals(...)` or unbox to `int`.
- **Character arithmetic:** For lowercase ASCII, `ch - 'a'` indexes into `new int[26]`.
- **Modulo with negatives:** Java's `%` preserves sign (`-1 % 5 == -1`). Normalize with `((x % m) + m) % m`.
- **Copying mutable state in backtracking:** Always record `new ArrayList<>(current)`, never `result.add(current)`.
- **Deep recursion on skewed trees/graphs:** Java's default stack overflows around ~5k–10k frames; mention iterative stack/queue traversal if `N` can reach `10^5`.
