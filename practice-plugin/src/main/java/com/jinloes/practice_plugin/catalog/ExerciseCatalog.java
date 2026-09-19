package com.jinloes.practice_plugin.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ExerciseCatalog {
    private static final List<Exercise> EXERCISES = List.of(
            new Exercise(
                    "pair-sum",
                    "Pair Sum",
                    "Arrays",
                    "Easy",
                    """
                    Find any valid pair of distinct indices whose values add to a target.

                    Contract
                    - `numbers` is non-null and may be empty. Its values and `target` may be any `int`.
                    - Return either an empty array when no pair exists, or an array of exactly two
                      distinct, in-bounds indices. The values at those indices must add to `target`
                      using mathematical integer addition (use `long` when adding).
                    - Any valid pair is accepted; the order of the two indices does not matter.
                      Do not mutate `numbers`.

                    Examples
                    - `findPair(new int[]{2, 7, 11, 15}, 9)` may return `{0, 1}`.
                    - `findPair(new int[]{3, 3}, 6)` returns `{0, 1}`.
                    - `findPair(new int[]{1, 2, 3}, 7)` returns `{}`.
                    """,
                    List.of(
                            "Try every pair of indices and first make the validity rules explicit.",
                            "A value seen earlier can be paired with the target complement; remember its index.",
                            "Use a single pass with a map and long arithmetic for complements. "
                                    + "Intended complexity: expected O(n) time and O(n) extra space."
                    ),
                    3,
                    11
            ),
            new Exercise(
                    "binary-search",
                    "Binary Search",
                    "Searching",
                    "Easy",
                    """
                    Search a sorted array for a target value.

                    Contract
                    - `sorted` is non-null and sorted in nondecreasing order; it may be empty.
                    - Return any in-bounds index whose value equals `target`, or `-1` when the
                      target is absent. Duplicate values may therefore produce any matching index.
                    - Do not mutate the input array.

                    Examples
                    - `search(new int[]{-4, -1, 0, 6, 9}, 6)` returns `3`.
                    - `search(new int[]{1, 2, 2, 2, 8}, 2)` may return `1`, `2`, or `3`.
                    - `search(new int[]{1, 4, 7}, 5)` returns `-1`.
                    """,
                    List.of(
                            "Keep an inclusive search interval and decide what happens when it becomes empty.",
                            "Compare the target with the middle value to discard one half of the interval.",
                            "Compute the midpoint as low + (high - low) / 2 and update the bounds after every comparison. "
                                    + "Intended complexity: O(log n) time and O(1) extra space."
                    ),
                    3,
                    11
            ),
            new Exercise(
                    "balanced-delimiters",
                    "Balanced Delimiters",
                    "Stacks",
                    "Easy",
                    """
                    Decide whether a string of delimiters is properly balanced and nested.

                    Contract
                    - `input` is non-null and contains only the six characters `(`, `)`, `[`, `]`,
                      `{`, and `}`. The empty string is valid.
                    - Return true exactly when every opening delimiter is closed by the same kind
                      in last-in-first-out order, with no unmatched delimiters left over.

                    Examples
                    - `isBalanced("{[()]}")` returns `true`.
                    - `isBalanced("([]{})")` returns `true`.
                    - `isBalanced("([)]")` returns `false`.
                    """,
                    List.of(
                            "An empty input has no unmatched opening delimiters.",
                            "Push opening delimiters and compare each closing delimiter with the most recent opening one.",
                            "Reject a closing delimiter when the stack is empty or its partner does not match; accept only an empty stack at the end. "
                                    + "Intended complexity: O(n) time and O(n) extra space in the worst case."
                    ),
                    3,
                    11
            ),
            new Exercise(
                    "reverse-linked-list",
                    "Reverse Linked List",
                    "Linked Lists",
                    "Easy",
                    """
                    Reverse a singly linked list in place.

                    Contract
                    - `head` may be null or may point to the first node of an acyclic list. Each node
                      has an integer `value` and a mutable `next` reference.
                    - Return the new head after reversing every link. Reuse exactly the nodes supplied:
                      do not allocate replacement nodes and do not change node values.
                    - A one-node list remains the same node and a null list returns null.

                    Examples
                    - A list `1 -> 2 -> 3` becomes `3 -> 2 -> 1`.
                    - A one-node list containing `8` returns that same node.
                    - Reversing an empty list returns `null`.
                    """,
                    List.of(
                            "Keep the already-reversed prefix, the current node, and the not-yet-visited suffix.",
                            "Before changing current.next, save the suffix so it is not lost.",
                            "Advance the two pointers until current is null; the prefix pointer is the new head. "
                                    + "Intended complexity: O(n) time and O(1) extra space."
                    ),
                    3,
                    11
            ),
            new Exercise(
                    "array-stack",
                    "Array Stack",
                    "Data Structures",
                    "Medium",
                    """
                    Implement a last-in-first-out stack of integers backed by a growable array.

                    Contract
                    - `push` adds one value to the top. The stack has no fixed capacity and accepts
                      every `int`, including duplicates and negative values.
                    - `pop` removes and returns the newest value; `peek` returns it without removing it.
                      Both methods throw `NoSuchElementException` when the stack is empty.
                    - `size` reports the number of stored values and `isEmpty` is equivalent to `size() == 0`.

                    Examples
                    - Pushing `4`, then `9`, makes `pop()` return `9` and then `4`.
                    - `peek()` observes the top while leaving the size unchanged.
                    - A new stack is empty and `pop()`/`peek()` throw `NoSuchElementException`.
                    """,
                    List.of(
                            "Track the number of elements and treat the next free array slot as the top.",
                            "Growing needs a larger array before writing when the current storage is full.",
                            "On pop, read the last element and decrement the size. Double array capacity when growing. "
                                    + "Intended complexity: amortized O(1) push, O(1) pop/peek/size/isEmpty, and O(n) space."
                    ),
                    3,
                    11
            ),
            new Exercise(
                    "binary-min-heap",
                    "Binary Min Heap",
                    "Heaps",
                    "Medium",
                    """
                    Implement a growable binary min-heap of integers.

                    Contract
                    - `add` inserts any `int`, including duplicates and negative values. The heap has
                      no fixed capacity and must grow as needed.
                    - `removeMin` removes and returns the smallest stored value. `peek` returns the
                      smallest value without removing it. Both throw `NoSuchElementException` when empty.
                    - `size` reports the number of elements and `isEmpty` is equivalent to `size() == 0`.

                    Examples
                    - Adding `7`, `2`, and `5` makes `peek()` return `2`, then removals return `2`, `5`, `7`.
                    - Duplicate minima are returned one at a time.
                    - A new heap is empty and `removeMin()`/`peek()` throw `NoSuchElementException`.
                    """,
                    List.of(
                            "Store the complete tree in an array; for index i, children are at 2*i+1 and 2*i+2.",
                            "After adding at the end, repeatedly swap upward while the child is smaller than its parent.",
                            "After removing the root, move the last value to the root and swap downward with the smaller child. "
                                    + "Intended complexity: amortized O(log n) add, O(log n) removeMin, "
                                    + "O(1) peek/size/isEmpty, and O(n) space."
                    ),
                    3,
                    12
            )
    );

    private static final Map<String, Exercise> BY_ID = EXERCISES.stream()
            .collect(java.util.stream.Collectors.toUnmodifiableMap(Exercise::id, exercise -> exercise));

    private ExerciseCatalog() {
    }

    public record Exercise(
            String id,
            String title,
            String topic,
            String difficulty,
            String statement,
            List<String> hints,
            int exampleCount,
            int fullCount
    ) {
        public Exercise {
            id = Objects.requireNonNull(id, "id");
            title = Objects.requireNonNull(title, "title");
            topic = Objects.requireNonNull(topic, "topic");
            difficulty = Objects.requireNonNull(difficulty, "difficulty");
            statement = Objects.requireNonNull(statement, "statement");
            hints = List.copyOf(Objects.requireNonNull(hints, "hints"));
            if (exampleCount < 0 || fullCount < exampleCount) {
                throw new IllegalArgumentException("Invalid exercise test counts");
            }
        }
    }

    public static List<Exercise> all() {
        return EXERCISES;
    }

    public static Exercise find(String id) {
        Exercise exercise = BY_ID.get(id);
        if (exercise == null) {
            throw new IllegalArgumentException("Unknown exercise ID: " + id);
        }
        return exercise;
    }

    public static String resource(Exercise exercise, String filename) {
        Objects.requireNonNull(exercise, "exercise");
        if (!BY_ID.containsKey(exercise.id())) {
            throw new IllegalArgumentException("Unknown exercise ID: " + exercise.id());
        }
        if (filename == null || filename.isEmpty()
                || !filename.matches("[A-Za-z0-9][A-Za-z0-9._-]*")
                || filename.contains("..")) {
            throw new IllegalArgumentException("Unsafe resource filename: " + filename);
        }
        String path = "/exercises/" + exercise.id() + "/" + filename;
        try (InputStream input = ExerciseCatalog.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalArgumentException("Unknown exercise resource: " + filename);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read exercise resource: " + path, exception);
        }
    }
}
