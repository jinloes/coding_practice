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
        cancel(cancelled);
        super.destroyProcess();
    }

    @Override
    public void killProcess() {
        cancel(cancelled);
        super.killProcess();
    }

    @Override
    public synchronized void coloredTextAvailable(@NotNull String text, @NotNull Key outputType) {
        OutputChunk chunk = clip(text, captured);
        if (chunk.skip()) {
            return;
        }
        super.coloredTextAvailable(chunk.text(), outputType);
        captured = chunk.captured();
        if (chunk.truncated()) {
            super.coloredTextAvailable("\n[Practice console truncated at 128 KiB.]\n", ProcessOutputTypes.SYSTEM);
        }
    }

    static void cancel(Runnable cancelled) {
        cancelled.run();
    }

    static OutputChunk clip(String text, int captured) {
        if (captured >= LIMIT) {
            return new OutputChunk("", captured, false, true);
        }
        int length = Math.min(text.length(), LIMIT - captured);
        int next = captured + length;
        return new OutputChunk(text.substring(0, length), next, next == LIMIT, false);
    }

    record OutputChunk(String text, int captured, boolean truncated, boolean skip) {
    }
}
