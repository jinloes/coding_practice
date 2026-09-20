package com.jinloes.practice_plugin.run;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.process.ProcessHandler;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What the runner is doing right now.
 *
 * <p>The runner holds exactly one of these in a single {@code AtomicReference}. Starting a run is a
 * compare-and-set from {@link Idle} to a fully constructed {@link Active} session, so the fields
 * describing a run can never be written by a caller that did not win the guard. Later facts about a
 * run — the run configuration the IDE eventually created — arrive by replacing the whole record,
 * never by mutating one field in place.
 *
 * <p>Signals that genuinely change while a run is in flight (the stop reason, the live process
 * handler, the watchdog, the owned child processes) live in {@link RunControl}, which belongs to one
 * run and is discarded with it. That is what makes the previous per-run reset assignments
 * unnecessary: a new run cannot observe the previous run's signals.
 */
sealed interface RunSession {
    /**
     * The single idle instance. The guard is claimed with a reference compare-and-set, so idleness
     * must be one identity rather than one of several equal values.
     */
    RunSession IDLE = new Idle();

    /** The runner is idle; a new run may claim the guard. */
    record Idle() implements RunSession {
    }

    /** A run that currently owns the guard. */
    sealed interface Active extends RunSession {
        RunControl control();
    }

    /**
     * A visible-example or input run executing in the learner's own IDE process tree.
     *
     * @param configuration the IDE run profile, known only once the configuration is installed
     * @param settings the temporary run settings to remove when the run finishes
     */
    record ExampleSession(
            ManagedPracticeWorkspace.Attempt attempt,
            String input,
            boolean debug,
            String fingerprint,
            ApplicationConfiguration configuration,
            RunnerAndConfigurationSettings settings,
            RunControl control
    ) implements Active {
        ExampleSession installed(ApplicationConfiguration installedConfiguration,
                                 RunnerAndConfigurationSettings installedSettings) {
            return new ExampleSession(attempt, input, debug, fingerprint, installedConfiguration,
                    installedSettings, control);
        }
    }

    /** A full correctness check executing in an isolated verification workspace. */
    record CheckSession(
            String attemptId,
            RunnerAndConfigurationSettings settings,
            RunControl control
    ) implements Active {
    }

    /**
     * The mutable signals of one run. Every field is scoped to a single run, so nothing here needs
     * to be reset before the next one.
     */
    final class RunControl {
        private final AtomicReference<CheckResult.Status> stopReason = new AtomicReference<>();
        private final AtomicReference<String> stopDetails = new AtomicReference<>();
        private final AtomicReference<ProcessHandler> active = new AtomicReference<>();
        private final AtomicReference<ScheduledFuture<?>> watchdog = new AtomicReference<>();
        private final Map<Long, ProcessHandle> ownedProcesses = new ConcurrentHashMap<>();

        /** Records the first reason this run stopped; a later cancel never overwrites a diagnosis. */
        void requestCancel() {
            stopReason.compareAndSet(null, CheckResult.Status.CANCELLED);
        }

        /** Records a diagnosed stop, which the watchdog reports in preference to a bare cancel. */
        void requestStop(CheckResult.Status reason, String details) {
            stopReason.set(reason);
            stopDetails.set(details);
        }

        CheckResult.Status stopReason() {
            return stopReason.get();
        }

        String stopDetails() {
            return stopDetails.get();
        }

        ProcessHandler active() {
            return active.get();
        }

        void active(ProcessHandler handler) {
            active.set(handler);
        }

        void watchdog(ScheduledFuture<?> future) {
            watchdog.set(future);
        }

        void cancelWatchdog() {
            ScheduledFuture<?> current = watchdog.getAndSet(null);
            if (current != null) {
                current.cancel(false);
            }
        }

        void capture(ProcessHandle child) {
            ownedProcesses.put(child.pid(), child);
        }

        /** Forcibly ends every child this run started, so no learner process outlives its run. */
        void terminateOwnedChildren() {
            ownedProcesses.values().forEach(handle -> {
                if (handle.isAlive()) {
                    handle.destroyForcibly();
                }
            });
            ownedProcesses.clear();
        }
    }
}
