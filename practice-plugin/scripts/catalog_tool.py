#!/usr/bin/env python3
"""Fast static validator and scaffolder for practice-plugin catalog content.

Usage:
  python3 practice-plugin/scripts/catalog_tool.py validate
  python3 practice-plugin/scripts/catalog_tool.py scaffold <id> --title "..." --topic "..." --difficulty Easy|Medium|Hard
"""

from __future__ import annotations

import argparse
import json
import re
import sys
import time
from pathlib import Path

VALID_ID = re.compile(r"^[a-z0-9]+(-[a-z0-9]+)*$")
TEST_ANNOTATION = re.compile(r"@Test\b")
DIFFICULTIES = ["Easy", "Medium", "Hard"]
DIFFICULTY_ORDER = {d: i for i, d in enumerate(DIFFICULTIES)}
REQUIRED_EXERCISE_FILES = [
    "exercise.json",
    "Solution.java",
    "ExamplesTest.java",
    "CorrectnessTest.java",
    "ExampleRunner.java",
    "Workload.java",
]
TRACK_NOTE_SECTIONS = ("About this list", "What to expect", "How to prepare")
GUIDE_LESSON_SECTIONS = ("How it works", "When to reach for it", "Common patterns", "Pitfalls")
STUDY_LIST_IDS = {"blind-75", "neetcode-150"}


def plugin_root() -> Path:
    return Path(__file__).resolve().parent.parent


def read_index(path: Path, label: str, errors: list[str]) -> list[str]:
    if not path.is_file():
        errors.append(f"Missing {label}: {path}")
        return []
    ids: list[str] = []
    seen: set[str] = set()
    for raw in path.read_text(encoding="utf-8").splitlines():
        entry = raw.strip()
        if not entry:
            continue
        if not VALID_ID.match(entry):
            errors.append(f"Invalid ID in {label}: {entry!r}")
        if entry in seen:
            errors.append(f"Duplicate ID in {label}: {entry!r}")
        seen.add(entry)
        ids.append(entry)
    return ids


def validate_exercises(root: Path, errors: list[str]) -> dict[str, dict]:
    ex_dir = root / "src/main/resources/exercises"
    ref_dir = root / "src/test/resources/reference"
    wrong_dir = root / "src/test/resources/known-wrong"
    indexed = read_index(ex_dir / "index.txt", "exercises/index.txt", errors)
    indexed_set = set(indexed)

    for label, d in (("exercises", ex_dir), ("reference", ref_dir), ("known-wrong", wrong_dir)):
        dirs = {p.name for p in d.iterdir() if p.is_dir()} if d.is_dir() else set()
        missing = sorted(indexed_set - dirs)
        extra = sorted(dirs - indexed_set)
        if missing:
            errors.append(f"{label} missing directories for indexed IDs: {missing}")
        if extra:
            errors.append(f"{label} has unindexed directories: {extra}")

    titles: dict[str, str] = {}
    manifests: dict[str, dict] = {}
    for ex_id in indexed:
        d = ex_dir / ex_id
        for fname in REQUIRED_EXERCISE_FILES:
            if not (d / fname).is_file():
                errors.append(f"{ex_id}: missing {fname}")
        if not (ref_dir / ex_id / "Solution.java").is_file():
            errors.append(f"{ex_id}: missing reference/Solution.java")
        if not (wrong_dir / ex_id / "Solution.java").is_file():
            errors.append(f"{ex_id}: missing known-wrong/Solution.java")

        manifest_path = d / "exercise.json"
        if not manifest_path.is_file():
            continue
        try:
            data = json.loads(manifest_path.read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            errors.append(f"{ex_id}/exercise.json: invalid JSON: {exc}")
            continue
        manifests[ex_id] = data

        title = data.get("title", "")
        if not isinstance(title, str) or not title.strip():
            errors.append(f"{ex_id}: blank title")
        elif title in titles:
            errors.append(f"{ex_id}: duplicate title {title!r} (also used by {titles[title]})")
        else:
            titles[title] = ex_id

        difficulty = data.get("difficulty")
        if difficulty not in DIFFICULTY_ORDER:
            errors.append(f"{ex_id}: invalid difficulty {difficulty!r}")

        hints = data.get("hints")
        if not isinstance(hints, list) or len(hints) != 3 or any(not isinstance(h, str) or not h.strip() for h in hints):
            errors.append(f"{ex_id}: hints must be 3 non-blank strings")

        statement = data.get("statement", "")
        if "Contract" not in statement or "Examples" not in statement:
            errors.append(f"{ex_id}: statement must include Contract and Examples sections")

        input_syntax = data.get("inputSyntax", "")
        sample_input = data.get("sampleInput", "")
        if not (
            isinstance(input_syntax, str)
            and isinstance(sample_input, str)
            and input_syntax.startswith("Input: ")
            and "for example: " in input_syntax
            and input_syntax.endswith(sample_input)
        ):
            errors.append(f"{ex_id}: inputSyntax must start with 'Input: ', contain 'for example: ', and end with sampleInput")

        runner_path = d / "ExampleRunner.java"
        if runner_path.is_file():
            runner = runner_path.read_text(encoding="utf-8")
            if input_syntax and input_syntax not in runner:
                errors.append(f"{ex_id}/ExampleRunner.java: missing verbatim inputSyntax")
            for required in (
                "class ExampleRunner",
                "public static void main(String[] args)",
                "import static org.assertj.core.api.Assertions.assertThat",
                "if (args.length > 0) {",
                "private static void runInput(String input)",
            ):
                if required not in runner:
                    errors.append(f"{ex_id}/ExampleRunner.java: missing {required!r}")
            for forbidden in ("org.junit", "org.hamcrest", "throw new AssertionError"):
                if forbidden in runner:
                    errors.append(f"{ex_id}/ExampleRunner.java: forbidden {forbidden!r}")

        starter_path = d / "Solution.java"
        if starter_path.is_file():
            starter = starter_path.read_text(encoding="utf-8")
            if "UnsupportedOperationException(\"Implement " not in starter:
                errors.append(f"{ex_id}/Solution.java: starter must throw UnsupportedOperationException(\"Implement ...\")")
            for forbidden in ("static void main", "@Test", "assertEquals", "assertPair", "assertValues"):
                if forbidden in starter:
                    errors.append(f"{ex_id}/Solution.java: starter contains forbidden {forbidden!r}")

        examples = data.get("examples")
        ex_count = len(examples) if isinstance(examples, list) else -1
        examples_test_count = 0
        correctness_test_count = 0
        for tname in ("ExamplesTest.java", "CorrectnessTest.java"):
            tpath = d / tname
            if not tpath.is_file():
                continue
            src = tpath.read_text(encoding="utf-8")
            if "import static org.assertj.core.api.Assertions.assertThat" not in src:
                errors.append(f"{ex_id}/{tname}: must use AssertJ assertThat")
            for forbidden in (
                "org.junit.jupiter.api.Assertions",
                "org.hamcrest",
                "throw new AssertionError",
                "@Timeout",
                "@ParameterizedTest",
                "@TestFactory",
                f"public class {tname[:-5]}",
            ):
                if forbidden in src:
                    errors.append(f"{ex_id}/{tname}: forbidden {forbidden!r}")
            count = len(TEST_ANNOTATION.findall(src))
            if tname == "ExamplesTest.java":
                examples_test_count = count
            else:
                correctness_test_count = count

        if ex_count != examples_test_count:
            errors.append(f"{ex_id}: exercise.json has {ex_count} examples but ExamplesTest has {examples_test_count} @Test methods")
        if correctness_test_count <= 0:
            errors.append(f"{ex_id}: CorrectnessTest must have at least 1 @Test method")

    return manifests


def validate_tracks(root: Path, manifests: dict[str, dict], errors: list[str]) -> int:
    tracks_dir = root / "src/main/resources/tracks"
    indexed = read_index(tracks_dir / "index.txt", "tracks/index.txt", errors)
    disk = {p.stem for p in tracks_dir.glob("*.json")} if tracks_dir.is_dir() else set()
    missing = sorted(set(indexed) - disk)
    extra = sorted(disk - set(indexed))
    if missing:
        errors.append(f"tracks/ missing json files for indexed IDs: {missing}")
    if extra:
        errors.append(f"tracks/ has unindexed json files: {extra}")

    for track_id in indexed:
        path = tracks_dir / f"{track_id}.json"
        if not path.is_file():
            continue
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            errors.append(f"tracks/{track_id}.json: invalid JSON: {exc}")
            continue
        summary = data.get("summary", "")
        if "not an official or verified question list" not in summary:
            errors.append(f"tracks/{track_id}.json: summary missing official-list disclaimer")
        if track_id not in STUDY_LIST_IDS and "Approximate" not in summary:
            errors.append(f"tracks/{track_id}.json: company track summary must contain 'Approximate'")

        lesson_lines = {line.strip() for line in data.get("lesson", "").splitlines()}
        for sec in TRACK_NOTE_SECTIONS:
            if sec not in lesson_lines:
                errors.append(f"tracks/{track_id}.json: lesson missing section {sec!r}")

        ex_ids = data.get("exercises", [])
        if len(ex_ids) != len(set(ex_ids)):
            errors.append(f"tracks/{track_id}.json: duplicate exercises")
        present_diffs: set[str] = set()
        for ex_id in ex_ids:
            if ex_id not in manifests:
                errors.append(f"tracks/{track_id}.json: unknown exercise {ex_id!r}")
            else:
                present_diffs.add(manifests[ex_id].get("difficulty", ""))
        if track_id not in STUDY_LIST_IDS:
            for diff in DIFFICULTIES:
                if diff not in present_diffs:
                    errors.append(f"tracks/{track_id}.json: company track missing {diff} exercise")

    return len(indexed)


def validate_guides(root: Path, manifests: dict[str, dict], errors: list[str]) -> int:
    guides_dir = root / "src/main/resources/guides"
    indexed = read_index(guides_dir / "index.txt", "guides/index.txt", errors)
    disk = {p.stem for p in guides_dir.glob("*.json")} if guides_dir.is_dir() else set()
    missing = sorted(set(indexed) - disk)
    extra = sorted(disk - set(indexed))
    if missing:
        errors.append(f"guides/ missing json files for indexed IDs: {missing}")
    if extra:
        errors.append(f"guides/ has unindexed json files: {extra}")

    in_any_guide: set[str] = set()
    for guide_id in indexed:
        path = guides_dir / f"{guide_id}.json"
        if not path.is_file():
            continue
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            errors.append(f"guides/{guide_id}.json: invalid JSON: {exc}")
            continue
        if not isinstance(data.get("summary"), str) or not data["summary"].strip():
            errors.append(f"guides/{guide_id}.json: blank summary")
        lesson_lines = {line.strip() for line in data.get("lesson", "").splitlines()}
        for sec in GUIDE_LESSON_SECTIONS:
            if sec not in lesson_lines:
                errors.append(f"guides/{guide_id}.json: lesson missing section {sec!r}")
        ex_ids = data.get("exercises", [])
        if not ex_ids:
            errors.append(f"guides/{guide_id}.json: empty exercises list")
        if len(ex_ids) != len(set(ex_ids)):
            errors.append(f"guides/{guide_id}.json: duplicate exercises")
        for ex_id in ex_ids:
            if ex_id not in manifests:
                errors.append(f"guides/{guide_id}.json: unknown exercise {ex_id!r}")
            else:
                in_any_guide.add(ex_id)

    unguided = sorted(set(manifests) - in_any_guide)
    if unguided:
        errors.append(f"exercises not listed in any study guide: {unguided}")

    return len(indexed)


def cmd_validate(_: argparse.Namespace) -> int:
    start = time.monotonic()
    root = plugin_root()
    errors: list[str] = []
    manifests = validate_exercises(root, errors)
    guide_count = validate_guides(root, manifests, errors)
    track_count = validate_tracks(root, manifests, errors)
    elapsed_ms = int((time.monotonic() - start) * 1000)
    if errors:
        print(f"FAIL ({len(errors)} issue(s) across {len(manifests)} exercises, {guide_count} guides, {track_count} tracks in {elapsed_ms} ms):")
        for err in errors:
            print(f"  - {err}")
        return 1
    print(f"OK: validated {len(manifests)} exercises, {guide_count} guides, and {track_count} tracks in {elapsed_ms} ms.")
    return 0


def cmd_scaffold(args: argparse.Namespace) -> int:
    ex_id = args.id
    if not VALID_ID.match(ex_id):
        print(f"Error: invalid exercise ID {ex_id!r} (expected lowercase kebab-case)", file=sys.stderr)
        return 2
    root = plugin_root()
    ex_dir = root / "src/main/resources/exercises" / ex_id
    ref_dir = root / "src/test/resources/reference" / ex_id
    wrong_dir = root / "src/test/resources/known-wrong" / ex_id
    if ex_dir.exists() or ref_dir.exists() or wrong_dir.exists():
        print(f"Error: exercise {ex_id!r} already exists", file=sys.stderr)
        return 2

    ex_dir.mkdir(parents=True)
    ref_dir.mkdir(parents=True)
    wrong_dir.mkdir(parents=True)

    manifest = {
        "title": args.title,
        "topic": args.topic,
        "difficulty": args.difficulty,
        "statement": (
            f"TODO: Describe {args.title}.\n\n"
            "Contract\n"
            "- TODO: Specify exact inputs, outputs, and edge cases.\n\n"
            "Examples\n"
            "1. Input: [1, 2, 3]\n"
            "   Output: 0"
        ),
        "examples": [
            {"input": "[1, 2, 3]", "output": "0"},
        ],
        "hints": [
            "TODO: First gentle nudge.",
            "TODO: Key data structure or invariant.",
            "TODO: Intended time and space complexity.",
        ],
        "inputSyntax": "Input: comma-separated integers, for example: 1,2,3",
        "sampleInput": "1,2,3",
        "intendedTime": "O(n)",
        "intendedSpace": "O(n)",
    }
    (ex_dir / "exercise.json").write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")

    (ex_dir / "Solution.java").write_text(
        "package com.jinloes.practice;\n\n"
        "public final class Solution {\n"
        "    public int solve(int[] nums) {\n"
        f"        throw new UnsupportedOperationException(\"Implement {args.title}\");\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    (ex_dir / "ExamplesTest.java").write_text(
        "package com.jinloes.practice;\n\n"
        "import org.junit.jupiter.api.Test;\n\n"
        "import static org.assertj.core.api.Assertions.assertThat;\n\n"
        "class ExamplesTest {\n"
        "    @Test\n"
        "    void example1() {\n"
        "        assertThat(new Solution().solve(new int[] {1, 2, 3})).isEqualTo(0);\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    (ex_dir / "CorrectnessTest.java").write_text(
        "package com.jinloes.practice;\n\n"
        "import org.junit.jupiter.api.Test;\n\n"
        "import static org.assertj.core.api.Assertions.assertThat;\n\n"
        "class CorrectnessTest {\n"
        "    @Test\n"
        "    void edgeCase() {\n"
        "        assertThat(new Solution().solve(new int[0])).isEqualTo(0);\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    (ex_dir / "ExampleRunner.java").write_text(
        "package com.jinloes.practice;\n\n"
        "import java.util.Arrays;\n\n"
        "import static org.assertj.core.api.Assertions.assertThat;\n\n"
        "public final class ExampleRunner {\n"
        "    private static final String USAGE = \"Input: comma-separated integers, for example: 1,2,3\";\n\n"
        "    private ExampleRunner() {\n"
        "    }\n\n"
        "    public static void main(String[] args) {\n"
        "        if (args.length > 0) {\n"
        "            runInput(String.join(\" \", args));\n"
        "            return;\n"
        "        }\n"
        "        assertThat(new Solution().solve(new int[] {1, 2, 3})).isEqualTo(0);\n"
        "        System.out.println(\"Examples passed: 1\");\n"
        "    }\n\n"
        "    private static void runInput(String input) {\n"
        "        try {\n"
        "            int[] nums = Arrays.stream(input.split(\",\")).map(String::trim).mapToInt(Integer::parseInt).toArray();\n"
        "            System.out.println(new Solution().solve(nums));\n"
        "        } catch (RuntimeException exception) {\n"
        "            System.out.println(USAGE);\n"
        "        }\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    (ex_dir / "Workload.java").write_text(
        "package com.jinloes.practice;\n\n"
        "public final class Workload {\n"
        "    public int[] sizes() {\n"
        "        return new int[] {1_000, 4_000, 16_000, 64_000};\n"
        "    }\n\n"
        "    public Object prepare(int n) {\n"
        "        int[] nums = new int[n];\n"
        "        for (int i = 0; i < n; i++) {\n"
        "            nums[i] = i;\n"
        "        }\n"
        "        return nums;\n"
        "    }\n\n"
        "    public Object run(Object input) {\n"
        "        return new Solution().solve((int[]) input);\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    (ref_dir / "Solution.java").write_text(
        "package com.jinloes.practice;\n\n"
        "public final class Solution {\n"
        "    public int solve(int[] nums) {\n"
        "        return 0;\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    (wrong_dir / "Solution.java").write_text(
        "package com.jinloes.practice;\n\n"
        "public final class Solution {\n"
        "    public int solve(int[] nums) {\n"
        "        return -1;\n"
        "    }\n"
        "}\n",
        encoding="utf-8",
    )

    print(f"Scaffolded 8 files for {ex_id!r}.")
    print("Next steps:")
    print(f"  1. Fill in the problem, tests, reference, and known-wrong files under {ex_dir.relative_to(root)}")
    print(f"  2. Add {ex_id!r} to practice-plugin/src/main/resources/exercises/index.txt")
    print(f"  3. Run: python3 practice-plugin/scripts/catalog_tool.py validate")
    print(f"  4. Run: ./gradlew :practice-plugin:test --tests 'com.jinloes.practice_plugin.catalog.ExerciseCatalogTest' -PcatalogOnly={ex_id}")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate or scaffold practice-plugin catalog content.")
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("validate", help="Statically validate all exercises and tracks in <1s.")
    sc = sub.add_parser("scaffold", help="Create the 8-file skeleton for a new exercise.")
    sc.add_argument("id", help="Exercise ID (lowercase kebab-case)")
    sc.add_argument("--title", required=True, help="Exercise display title")
    sc.add_argument("--topic", required=True, help="Exercise topic")
    sc.add_argument("--difficulty", required=True, choices=DIFFICULTIES, help="Exercise difficulty")
    args = parser.parse_args()
    if args.command == "validate":
        return cmd_validate(args)
    return cmd_scaffold(args)


if __name__ == "__main__":
    raise SystemExit(main())
