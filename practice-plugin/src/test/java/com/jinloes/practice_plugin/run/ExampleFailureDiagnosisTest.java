package com.jinloes.practice_plugin.run;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleFailureDiagnosisTest {

    @Test
    void identifiesFailingExampleInputAndExpectedOutputOnUncaughtException() {
        var exercise = ExerciseCatalog.find("valid-palindrome");
        String runner = ExerciseCatalog.resource(exercise, "ExampleRunner.java");
        String trace = """
                Exception in thread "main" java.lang.StringIndexOutOfBoundsException: Index 28 out of bounds for length 28
                \tat java.base/java.lang.String.checkIndex(String.java:4904)
                \tat com.jinloes.practice.Solution.isPalindrome(Solution.java:13)
                \tat com.jinloes.practice.ExampleRunner.main(ExampleRunner.java:17)
                """;

        String diagnosis = ExampleFailureDiagnosis.describe(exercise, runner, "", trace);

        assertThat(diagnosis)
                .contains("Failed on Example 1:")
                .contains("Input:    " + exercise.examples().get(0).input())
                .contains("Expected: " + exercise.examples().get(0).output())
                .contains("Error:    java.lang.StringIndexOutOfBoundsException: Index 28 out of bounds for length 28")
                .contains("at Solution.isPalindrome(Solution.java:13)");
    }

    @Test
    void identifiesLaterExampleByRunnerLineOrAssertJLabel() {
        var exercise = ExerciseCatalog.find("valid-palindrome");
        String runner = ExerciseCatalog.resource(exercise, "ExampleRunner.java");
        String secondExampleCrash = """
                Exception in thread "main" java.lang.NullPointerException: boom
                \tat com.jinloes.practice.Solution.isPalindrome(Solution.java:8)
                \tat com.jinloes.practice.ExampleRunner.main(ExampleRunner.java:19)
                """;

        assertThat(ExampleFailureDiagnosis.describe(exercise, runner, "", secondExampleCrash))
                .contains("Failed on Example 2:")
                .contains("Input:    " + exercise.examples().get(1).input())
                .contains("Expected: " + exercise.examples().get(1).output());

        String labelFailure = """
                Exception in thread "main" java.lang.AssertionError: [Example 3: isPalindrome(".,!")]
                Expecting value to be true but was false
                \tat com.jinloes.practice.ExampleRunner.main(ExampleRunner.java:999)
                """;
        assertThat(ExampleFailureDiagnosis.describe(exercise, "", "", labelFailure))
                .contains("Failed on Example 3:")
                .contains("Input:    " + exercise.examples().get(2).input())
                .contains("Expected: " + exercise.examples().get(2).output());
    }

    @Test
    void listsVisibleExamplesWhenSingleLineRunnerCannotBePinpointed() {
        var exercise = ExerciseCatalog.find("apply-index-permutation");
        String runner = ExerciseCatalog.resource(exercise, "ExampleRunner.java");
        String trace = """
                Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds
                \tat com.jinloes.practice.Solution.applyPermutation(Solution.java:10)
                \tat com.jinloes.practice.ExampleRunner.main(ExampleRunner.java:7)
                """;

        String diagnosis = ExampleFailureDiagnosis.describe(exercise, runner, "", trace);

        assertThat(diagnosis)
                .contains("Error: java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds")
                .contains("at Solution.applyPermutation(Solution.java:10)")
                .contains("Visible examples:")
                .contains("Example 1: Input: " + exercise.examples().get(0).input()
                        + " | Expected: " + exercise.examples().get(0).output());
    }

    @Test
    void formatsCustomInputFailuresWithTheProvidedInput() {
        var exercise = ExerciseCatalog.find("valid-palindrome");
        String trace = """
                Exception in thread "main" java.lang.StringIndexOutOfBoundsException: Index 5 out of bounds
                \tat com.jinloes.practice.Solution.isPalindrome(Solution.java:13)
                """;

        String diagnosis = ExampleFailureDiagnosis.describe(exercise, "", "'ab!ba'", trace);

        assertThat(diagnosis)
                .contains("Failed on custom input:")
                .contains("Input: 'ab!ba'")
                .contains("Error:    java.lang.StringIndexOutOfBoundsException: Index 5 out of bounds")
                .contains("at Solution.isPalindrome(Solution.java:13)");
    }

    @Test
    void mapsExamplesTestMethodNamesToTheirVisibleExampleAndRendersInFailureText() {
        var exercise = ExerciseCatalog.find("valid-palindrome");
        String examplesTest = ExerciseCatalog.resource(exercise, "ExamplesTest.java");

        var first = ExampleFailureDiagnosis.exampleForTest(
                exercise, examplesTest, "ignoresCaseSpacesAndPunctuation()");
        var second = ExampleFailureDiagnosis.exampleForTest(
                exercise, examplesTest, "rejectsTextThatDiffersWhenReversed");
        var unknown = ExampleFailureDiagnosis.exampleForTest(exercise, examplesTest, "doesNotExist");

        assertThat(first).isEqualTo(exercise.examples().get(0));
        assertThat(second).isEqualTo(exercise.examples().get(1));
        assertThat(unknown).isNull();

        String rendered = FailureText.render(
                "com.jinloes.practice.ExamplesTest",
                "ignoresCaseSpacesAndPunctuation()",
                "java.lang.StringIndexOutOfBoundsException",
                "Index 28 out of bounds for length 28",
                "\tat com.jinloes.practice.Solution.isPalindrome(Solution.java:13)",
                first);
        assertThat(rendered)
                .contains("Visible example: ignores case spaces and punctuation")
                .contains("Input:    " + first.input())
                .contains("Expected: " + first.output())
                .contains("Index 28 out of bounds for length 28")
                .contains("at Solution.isPalindrome(Solution.java:13)");
    }

    @Test
    void handlesEdgeCasesForNullsOutOfRangeLabelsAndCausedByTraces() {
        var exercise = ExerciseCatalog.find("valid-palindrome");
        assertThat(ExampleFailureDiagnosis.describe(null, null, null, null)).isEmpty();
        assertThat(ExampleFailureDiagnosis.describe(null, "", "", "Caused by: java.lang.IllegalStateException: bad"))
                .isEqualTo("Error: java.lang.IllegalStateException: bad");
        assertThat(ExampleFailureDiagnosis.describe(exercise, null, "", "ExampleRunner.java:10"))
                .startsWith("Visible examples:");
        assertThat(ExampleFailureDiagnosis.describe(exercise, "   ", "", "Example 0\nExampleRunner.java:10"))
                .startsWith("Visible examples:");
        assertThat(ExampleFailureDiagnosis.describe(exercise, "   ", "", "Example 99"))
                .startsWith("Visible examples:");
        assertThat(ExampleFailureDiagnosis.describe(
                exercise,
                "public static void main(String[] args) {\n}\nassertThat(1);\nassertThat(2);\nassertThat(3);\n}",
                "",
                "at com.jinloes.practice.ExampleRunner.main(ExampleRunner.java:1)"))
                .startsWith("Visible examples:");
        assertThat(ExampleFailureDiagnosis.describe(exercise, "", "custom", ""))
                .isEqualTo("Failed on custom input:\n  Input: custom");
        assertThat(ExampleFailureDiagnosis.describe(
                exercise, "", "",
                "Exception in thread \"main\" java.lang.AssertionError: [Example 1]\n"
                        + "Expecting actual:\n"
                        + "  [5, 2, 8, 1, null, 7, 9]\n"
                        + "to be equal to:\n"
                        + "  [5, 8, 2, 9, 7, null, 1]\n"))
                .contains("Failed on Example 1:")
                .contains("Actual:   [5, 2, 8, 1, null, 7, 9]")
                .doesNotContain("AssertionError");
        assertThat(ExampleFailureDiagnosis.describe(
                exercise, "", "",
                "Exception in thread \"main\" java.lang.AssertionError: [Example 2]\n"
                        + "expected: false\n"
                        + " but was: true\n"))
                .contains("Failed on Example 2:")
                .contains("Actual:   true")
                .doesNotContain("AssertionError");
        assertThat(ExampleFailureDiagnosis.describe(
                exercise, "", "",
                "Exception in thread \"main\" java.lang.AssertionError: [Example 1]\n"
                        + "Expecting actual not to be null\n"))
                .contains("Actual:   null")
                .doesNotContain("AssertionError");
        assertThat(ExampleFailureDiagnosis.describe(
                exercise, "", "",
                "Exception in thread \"main\" java.lang.AssertionError: [Example 1]\n"
                        + "Expecting actual:"))
                .contains("Error:    java.lang.AssertionError: [Example 1]");
        assertThat(ExampleFailureDiagnosis.exampleForTest(null, "", "a")).isNull();
        assertThat(ExampleFailureDiagnosis.exampleForTest(exercise, null, "a")).isNull();
        assertThat(ExampleFailureDiagnosis.exampleForTest(exercise, "", null)).isNull();
        assertThat(ExampleFailureDiagnosis.exampleForTest(
                exercise,
                "@Test void a() {}\n@Test void b() {}\n@Test void c() {}\n@Test void d() {}",
                "d")).isNull();

        var control = new RunSession.RunControl();
        control.appendOutput("x".repeat(20_000));
        control.appendOutput("ignored");
        assertThat(control.capturedOutput()).hasSize(16_384);
    }

    @Test
    void diagnoseTerminationPublishesDiagnosisToConsoleAndReturnsMessage() {
        var attempt = new ManagedPracticeWorkspace.Attempt(
                "a1", "valid-palindrome", "Valid Palindrome", Path.of("/tmp/a1"));

        var controlWithHandler = new RunSession.RunControl();
        List<String> consoleOutput = new ArrayList<>();
        var handler = new ProcessHandler() {
            @Override
            protected void destroyProcessImpl() {
            }

            @Override
            protected void detachProcessImpl() {
            }

            @Override
            public boolean detachIsDefault() {
                return false;
            }

            @Override
            public OutputStream getProcessInput() {
                return null;
            }
        };
        handler.addProcessListener(new ProcessListener() {
            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                consoleOutput.add(event.getText());
            }
        });
        handler.startNotify();
        controlWithHandler.active(handler);
        controlWithHandler.appendOutput(
                "Exception in thread \"main\" java.lang.StringIndexOutOfBoundsException: Index 28\n"
                        + "\tat com.jinloes.practice.Solution.isPalindrome(Solution.java:13)\n"
                        + "\tat com.jinloes.practice.ExampleRunner.main(ExampleRunner.java:17)\n");
        var withHandler = new RunSession.ExampleSession(attempt, "", false, "fp", null, null, controlWithHandler);
        assertThat(ExampleExecution.diagnoseTermination(withHandler, 1))
                .contains("Failed on Example 1:")
                .contains("Expected:");
        assertThat(consoleOutput).anySatisfy(text -> assertThat(text)
                .contains("Failed on Example 1:")
                .contains("Input:")
                .contains("Expected:"));

        var noHandler = new RunSession.ExampleSession(attempt, "", false, "fp", null, null, new RunSession.RunControl());
        assertThat(ExampleExecution.diagnoseTermination(noHandler, 1))
                .contains("Visible examples:");

        var noAttempt = new RunSession.ExampleSession(null, "", false, "fp", null, null, new RunSession.RunControl());
        assertThat(ExampleExecution.diagnoseTermination(noAttempt, 1))
                .contains("finished (exit 1)");
    }
}
