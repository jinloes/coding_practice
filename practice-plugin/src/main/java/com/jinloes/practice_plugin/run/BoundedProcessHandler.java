package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/** A full-check process handler that caps console output and reports every stop as a cancellation. */
final class BoundedProcessHandler extends KillableColoredProcessHandler {
    private static final int LIMIT = 128 * 1024;
    private final Runnable cancelled;
    private int captured;

    BoundedProcessHandler(GeneralCommandLine command, Runnable cancelled) throws ExecutionException {
        super(command);
        this.cancelled = cancelled;
    }

    @Override
    public void destroyProcess() {
        cancelled.run();
        super.destroyProcess();
    }

    @Override
    public void killProcess() {
        cancelled.run();
        super.killProcess();
    }

    @Override
    public synchronized void coloredTextAvailable(@NotNull String text, @NotNull Key outputType) {
        if (captured >= LIMIT) {
            return;
        }
        int length = Math.min(text.length(), LIMIT - captured);
        super.coloredTextAvailable(text.substring(0, length), outputType);
        captured += length;
        if (captured == LIMIT) {
            super.coloredTextAvailable("\n[Practice console truncated at 128 KiB.]\n", ProcessOutputTypes.SYSTEM);
        }
    }
}
