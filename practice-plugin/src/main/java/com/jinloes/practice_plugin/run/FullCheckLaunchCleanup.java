package com.jinloes.practice_plugin.run;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jinloes.practice_plugin.workspace.VerificationWorkspace;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tears down a partially started full-check process tree, listener, handler stream readers, and
 * verification workspace when {@link FullCheck#start} fails before handing ownership to the caller.
 */
final class FullCheckLaunchCleanup {
    private static final Logger LOG = Logger.getInstance(FullCheckLaunchCleanup.class);

    private FullCheckLaunchCleanup() {
    }

    static void releaseFailedLaunch(VerificationWorkspace workspace, Process acquired,
                                    ProcessHandler acquiredHandler, ProcessListener completionListener,
                                    RunSession.RunControl control, Exception exception) {
        if (acquiredHandler != null && completionListener != null) {
            acquiredHandler.removeProcessListener(completionListener);
        }
        cleanupLaunchFailure(workspace, acquired, control, exception);
        releaseFailedHandler(acquiredHandler, acquired, exception);
    }

    static void cleanupLaunchFailure(VerificationWorkspace workspace) {
        try {
            workspace.markFinished("launch-failed");
            workspace.cleanupAfterRun();
        } catch (IOException ignored) {
            // Preserve uncertain workspaces.
        }
    }

    static void cleanupLaunchFailure(VerificationWorkspace workspace, Process acquired,
                                     RunSession.RunControl control, Exception original) {
        if (acquired != null) {
            control.cancelWatchdog();
            var descendants = new LinkedHashMap<Long, ProcessHandle>();
            acquired.descendants().forEach(child -> descendants.put(child.pid(), child));
            acquired.destroyForcibly();
            acquired.descendants().forEach(child -> descendants.put(child.pid(), child));
            descendants.values().forEach(control::capture);
            control.terminateOwnedChildren();
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            try {
                if (!acquired.waitFor(5, TimeUnit.SECONDS)) {
                    throw new IOException("The acquired verification process did not exit.");
                }
                for (ProcessHandle child : descendants.values()) {
                    if (child.isAlive()) {
                        child.onExit().get(Math.max(1, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
                    }
                }
                control.active(null);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                IOException failure = new IOException(
                        "Interrupted while terminating the acquired verification process.", exception);
                original.addSuppressed(failure);
                LOG.warn("Verification launch cleanup failed; workspace preserved.", failure);
                return;
            } catch (IOException | ExecutionException | TimeoutException exception) {
                original.addSuppressed(exception);
                LOG.warn("Verification launch cleanup failed; workspace preserved.", exception);
                return;
            }
        }
        if (workspace != null) {
            cleanupLaunchFailure(workspace);
        }
    }

    static void releaseFailedHandler(ProcessHandler handler, Process acquired, Exception original) {
        releaseFailedHandler(AppExecutorUtil.getAppExecutorService(), handler, acquired, original);
    }

    static void releaseFailedHandler(ExecutorService executor, ProcessHandler handler,
                                     Process acquired, Exception original) {
        if (handler == null || acquired == null || acquired.isAlive()) {
            return;
        }
        boolean interrupted = Thread.interrupted();
        try {
            // Drain ProcessWaitFor and EOF stream readers off the EDT.
            Future<?> drain = executor.submit(() -> {
                handler.startNotify();
                if (!handler.waitFor(5_000L)) {
                    throw new IllegalStateException("Verification handler did not finish draining.");
                }
            });
            try {
                drain.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                drain.cancel(true);
                interrupted = true;
                original.addSuppressed(exception);
            } catch (ExecutionException | TimeoutException exception) {
                drain.cancel(true);
                original.addSuppressed(exception);
            }
        } catch (RuntimeException exception) {
            original.addSuppressed(exception);
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
