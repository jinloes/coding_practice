package com.jinloes.practice_plugin.run;

import com.intellij.psi.PsiJavaFile;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

import java.io.IOException;

import static com.jinloes.practice_plugin.run.CheckResult.Status.CANCELLED;

/** Pure decisions and messages for {@link PracticeRunner}, kept separate so they are unit-testable. */
final class RunDecisions {
    private RunDecisions() {
    }

    static String normalizedInput(String input) {
        return input == null ? "" : input.trim();
    }

    static ExampleStart exampleStart(RunSession.ExampleSession examples) {
        if (examples.control().stopReason() == CANCELLED) {
            return new ExampleStart(true, "");
        }
        String verb = examples.debug() ? "Debugging" : "Running";
        return new ExampleStart(false, examples.input().isEmpty()
                ? verb + " visible examples."
                : verb + " the solution on: " + examples.input());
    }

    static String exampleFinishedText(String input, int exitCode) {
        return (input.isEmpty() ? "Example session" : "Input session")
                + " finished (exit " + exitCode
                + "). Use Check Solution to update correctness progress.";
    }

    static String exampleResultText(String resultText, String fingerprint, Fingerprinter current) {
        if (fingerprint == null || !resultText.contains("session finished (exit 0)")) {
            return resultText;
        }
        try {
            return fingerprint.equals(current.fingerprint())
                    ? resultText
                    : "STALE: files changed during the example run; run the current solution again.";
        } catch (IOException exception) {
            return "Example result is unavailable: " + exception.getMessage();
        }
    }

    static String fullCheckMessage(ManagedPracticeWorkspace.Attempt attempt, CheckResult completed) {
        return "Managed attempt: " + attempt.id() + "\nFull check: " + completed.status()
                + "\n" + completed.details()
                + (completed.complexity().isBlank() ? "" : "\n\n" + completed.complexity());
    }

    static ExampleInstallPreflight exampleInstallPreflight(boolean projectDisposed, RunSession current) {
        if (projectDisposed || !(current instanceof RunSession.ExampleSession examples)
                || examples.control().stopReason() == CANCELLED) {
            return new ExampleInstallPreflight(false, "Example launch cancelled.", null);
        }
        return new ExampleInstallPreflight(true, "", examples);
    }

    static boolean exampleRunnerOwned(PsiJavaFile javaFile, com.intellij.openapi.module.Module module,
                                      com.intellij.openapi.module.Module expectedModule) {
        return javaFile.getClasses().length == 1
                && module != null
                && !module.isDisposed()
                && module == expectedModule;
    }

    record ExampleStart(boolean cancelled, String message) {
    }

    record ExampleInstallPreflight(boolean proceed, String message, RunSession.ExampleSession current) {
    }

    @FunctionalInterface
    interface Fingerprinter {
        String fingerprint() throws IOException;
    }
}
