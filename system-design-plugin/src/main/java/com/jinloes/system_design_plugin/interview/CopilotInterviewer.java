package com.jinloes.system_design_plugin.interview;

import com.github.copilot.CopilotClient;
import com.github.copilot.rpc.CopilotClientOptions;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.Key;
import com.intellij.util.EnvironmentUtil;
import com.jinloes.system_design_plugin.platform.OwnedDirectory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

/**
 * Runs the GitHub Copilot CLI as the interviewer with every tool disabled. The prompt goes in on stdin and the
 * process runs in a fresh, otherwise empty plugin-owned directory, so learner text cannot make it read or change
 * anything. Only one review runs at a time per application.
 */
public final class CopilotInterviewer {
    /** A tool name that does not exist: an allowlist containing only it leaves the model with no tools. */
    public static final String NO_TOOLS = "practice_no_tools";
    public static final int MAX_OUTPUT_BYTES = 512 * 1024;
    public static final int LIST_TIMEOUT_SECONDS = 20;
    static final String MARKER = ".system-design-interviewer";
    static final String LIST_PREFIX = "Could not list models: ";
    static final int MAX_LIST_ERROR_CHARS = 300;
    /** Flags for the model-list CLI, before the SDK's own {@code --server ... --stdio}. */
    static final List<String> LIST_ARGUMENTS = List.of("--no-custom-instructions", "--disable-builtin-mcps");
    private static final long FORCE_STOP_SECONDS = 35;
    private static final long POLL_MILLIS = 100;
    private static final int MAX_ERROR_CHARS = 2_000;
    private static final AtomicBoolean RUNNING = new AtomicBoolean();

    private final OwnedDirectory runs;
    private final List<String> commandPrefix;
    private final String executableName;
    private final long pollMillis;

    public record Settings(String cliPath, String model, int timeoutSeconds) {
    }

    /** A failure with a message the learner can act on. */
    public static final class InterviewException extends Exception {
        public InterviewException(String message) {
            super(message);
        }
    }

    public CopilotInterviewer() {
        this(Path.of(PathManager.getSystemPath()).resolve("system-design-practice").resolve("interviewer"),
                List.of(), "copilot", POLL_MILLIS);
    }

    CopilotInterviewer(Path base, List<String> commandPrefix, String executableName) {
        this(base, commandPrefix, executableName, POLL_MILLIS);
    }

    CopilotInterviewer(Path base, List<String> commandPrefix, String executableName, long pollMillis) {
        this.runs = OwnedDirectory.under(base, MARKER);
        this.commandPrefix = List.copyOf(commandPrefix);
        this.executableName = executableName;
        this.pollMillis = pollMillis;
    }

    public static boolean isRunning() {
        return RUNNING.get();
    }

    /** The exact CLI argument list. Never add allow-tool flags here: the review must stay tool-free. */
    public static List<String> arguments(String executable, String model) {
        List<String> arguments = new ArrayList<>(List.of(executable, "-s", "--no-custom-instructions",
                "--disable-builtin-mcps", "--available-tools=" + NO_TOOLS, "--no-ask-user"));
        if (model != null && !model.isBlank()) {
            arguments.add("--model=" + model.strip());
        }
        return List.copyOf(arguments);
    }

    String resolveExecutable(String configured) throws InterviewException {
        if (configured != null && !configured.isBlank()) {
            Path path;
            try {
                path = Path.of(configured.strip());
            } catch (InvalidPathException invalid) {
                throw new InterviewException("\"" + configured.strip() + "\" is not a valid path. "
                        + "Fix the Copilot CLI path in Interviewer settings or clear it to search PATH.");
            }
            Path normalized = path.toAbsolutePath().normalize();
            if (Files.isRegularFile(normalized)) {
                return normalized.toString();
            }
            throw new InterviewException("Copilot CLI was not found at " + path
                    + ". Fix the path in Interviewer settings or clear it to search PATH.");
        }
        File found = PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS(executableName);
        if (found != null) {
            return found.getPath();
        }
        throw new InterviewException("Copilot CLI was not found on PATH. Install it with "
                + "'npm install -g @github/copilot', sign in by running 'copilot' once in a terminal, "
                + "or set its path in Interviewer settings.");
    }

    /** Runs one review and returns the parsed (or raw fallback) feedback. */
    public InterviewFeedback run(String prompt, Settings settings, BooleanSupplier canceled)
            throws InterviewException {
        if (!RUNNING.compareAndSet(false, true)) {
            throw new InterviewException("Another feedback request is already running. Wait for it or cancel it.");
        }
        try {
            String executable = resolveExecutable(settings.cliPath());
            cleanupAbandoned();
            Path directory = newRunDirectory();
            try {
                return InterviewFeedback.parse(execute(executable, settings, directory, prompt, canceled));
            } finally {
                deleteRunDirectory(directory);
            }
        } finally {
            RUNNING.set(false);
        }
    }

    /**
     * Lists the models the signed-in account can use, through the Copilot Java SDK. Only the SDK handshake,
     * {@code models.list}, and shutdown reach the CLI: never a session or learner content. Blocks, so call it off
     * the EDT. The CLI process is gone when this returns.
     */
    public List<CopilotModels.Model> listModels(String cliPath, BooleanSupplier canceled)
            throws InterviewException {
        return listModels(cliPath, canceled, Duration.ofSeconds(LIST_TIMEOUT_SECONDS));
    }

    List<CopilotModels.Model> listModels(String cliPath, BooleanSupplier canceled, Duration timeout)
            throws InterviewException {
        String executable = resolveExecutable(cliPath);
        Path directory = newRunDirectory();
        try {
            return queryModels(executable, directory, canceled, timeout);
        } finally {
            deleteRunDirectory(directory);
        }
    }

    private List<CopilotModels.Model> queryModels(String executable, Path directory, BooleanSupplier canceled,
                                                  Duration timeout) throws InterviewException {
        List<String> cliArgs = new ArrayList<>();
        String launcher = executable;
        if (!commandPrefix.isEmpty()) {
            launcher = commandPrefix.get(0);
            cliArgs.addAll(commandPrefix.subList(1, commandPrefix.size()));
            cliArgs.add(executable);
        }
        cliArgs.addAll(LIST_ARGUMENTS);
        CopilotClient client = new CopilotClient(new CopilotClientOptions()
                .setCliPath(launcher)
                .setCliArgs(cliArgs.toArray(String[]::new))
                .setCwd(directory.toString())
                .setEnvironment(EnvironmentUtil.getEnvironmentMap()));
        long deadline = System.nanoTime() + timeout.toNanos();
        boolean forceStopped = false;
        try {
            await(client.start(), canceled, deadline, timeout);
            return CopilotModels.from(await(client.listModels(), canceled, deadline, timeout));
        } catch (Abort abort) {
            forceStopped = true;
            stopQuietly(() -> client.forceStop().get(FORCE_STOP_SECONDS, TimeUnit.SECONDS));
            if (abort.interrupted) {
                Thread.currentThread().interrupt();
            }
            throw new InterviewException(abort.getMessage());
        } finally {
            if (!forceStopped) {
                client.close();
            }
        }
    }

    @FunctionalInterface
    interface ThrowingStop {
        void run() throws Exception;
    }

    static void stopQuietly(ThrowingStop stop) {
        try {
            stop.run();
        } catch (Exception ignored) {
            // The SDK kills the process on its own once an in-flight connect gives up.
        }
    }

    private static <T> T await(CompletableFuture<T> future, BooleanSupplier canceled, long deadline,
                               Duration timeout) throws Abort, InterviewException {
        while (true) {
            if (canceled.getAsBoolean()) {
                throw new Abort(LIST_PREFIX + "cancelled.", false);
            }
            if (System.nanoTime() - deadline > 0) {
                throw new Abort(LIST_PREFIX + "Copilot CLI did not answer within " + timeout.toSeconds()
                        + " seconds.", false);
            }
            try {
                return future.get(POLL_MILLIS, TimeUnit.MILLISECONDS);
            } catch (TimeoutException waiting) {
                // Check cancellation and the deadline again.
            } catch (InterruptedException interrupted) {
                throw new Abort(LIST_PREFIX + "interrupted.", true);
            } catch (java.util.concurrent.ExecutionException failed) {
                throw listFailure(failed.getCause());
            }
        }
    }

    static InterviewException listFailure(Throwable failure) {
        Throwable cause = failure;
        while ((cause instanceof CompletionException || cause instanceof java.util.concurrent.ExecutionException)
                && cause.getCause() != null) {
            cause = cause.getCause();
        }
        String detail = cause == null ? null : cause.getMessage();
        if (detail == null || detail.isBlank()) {
            detail = cause == null ? "unknown error" : cause.getClass().getSimpleName();
        }
        String message = LIST_PREFIX + detail.strip().replaceAll("\\s+", " ");
        if (message.length() > MAX_LIST_ERROR_CHARS) {
            message = message.substring(0, MAX_LIST_ERROR_CHARS - 3) + "...";
        }
        return new InterviewException(message);
    }

    /** Cancel, timeout, or interrupt: the client must be force-stopped rather than closed gracefully. */
    private static final class Abort extends Exception {
        private final boolean interrupted;

        Abort(String message, boolean interrupted) {
            super(message);
            this.interrupted = interrupted;
        }
    }

    private Path newRunDirectory() throws InterviewException {
        Path directory = runs.resolve("run-" + UUID.randomUUID().toString().replace("-", ""));
        try {
            runs.create(directory, "pid=" + ProcessHandle.current().pid() + "\n");
        } catch (IOException exception) {
            throw new InterviewException("Could not prepare the interviewer directory: " + exception.getMessage());
        }
        return directory;
    }

    void deleteRunDirectory(Path directory) {
        try {
            runs.deleteTree(directory);
        } catch (IOException ignored) {
            // cleanupAbandoned removes it on a later run once this process has exited.
        }
    }

    /** Deletes interviewer directories whose recorded process is no longer alive. */
    void cleanupAbandoned() {
        runs.cleanupAbandoned((directory, marker) -> !alive(marker));
    }

    private static boolean alive(Properties marker) throws IOException {
        try {
            long pid = Long.parseLong(marker.getProperty("pid", ""));
            return ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
        } catch (NumberFormatException malformed) {
            throw new IOException("Unreadable interviewer marker", malformed);
        }
    }

    private String execute(String executable, Settings settings, Path directory, String prompt,
                           BooleanSupplier canceled) throws InterviewException {
        List<String> command = new ArrayList<>(commandPrefix);
        command.addAll(arguments(executable, settings.model()));
        GeneralCommandLine commandLine = new GeneralCommandLine(command)
                .withWorkingDirectory(directory)
                .withCharset(StandardCharsets.UTF_8)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
        KillableProcessHandler handler;
        try {
            handler = new KillableProcessHandler(commandLine);
        } catch (ExecutionException exception) {
            throw new InterviewException("Could not start Copilot CLI: " + exception.getMessage());
        }
        handler.setShouldKillProcessSoftly(false);
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(settings.timeoutSeconds());
        Output output = new Output();
        handler.addProcessListener(output);
        handler.startNotify();
        byte[] promptBytes = prompt.getBytes(StandardCharsets.UTF_8);
        Thread.ofVirtual().name("system-design-interviewer-stdin").start(() -> {
            OutputStream input = handler.getProcessInput();
            try {
                input.write(promptBytes);
                input.close();
            } catch (IOException exception) {
                // The process may exit or be killed before reading stdin; its exit code or stopReason explains why.
            }
        });

        String stopReason = null;
        while (!handler.waitFor(pollMillis)) {
            if (canceled.getAsBoolean()) {
                stopReason = "Feedback request cancelled.";
            } else if (output.overflowed()) {
                stopReason = "The interviewer produced more than " + (MAX_OUTPUT_BYTES / 1024)
                        + " KiB of output, so the review was stopped. Try again.";
            } else if (System.nanoTime() - deadline > 0) {
                stopReason = "The interviewer did not answer within " + settings.timeoutSeconds()
                        + " seconds. Try again or raise the timeout in Interviewer settings.";
            }
            if (stopReason != null) {
                handler.killProcess();
                handler.destroyProcess();
                handler.waitFor(5_000);
                throw new InterviewException(stopReason);
            }
        }
        Integer exitCode = handler.getExitCode();
        if (output.overflowed()) {
            throw new InterviewException("The interviewer produced more than " + (MAX_OUTPUT_BYTES / 1024)
                    + " KiB of output, so the review was stopped. Try again.");
        }
        if (!Integer.valueOf(0).equals(exitCode)) {
            String errors = output.errors().strip();
            if (errors.length() > MAX_ERROR_CHARS) {
                errors = errors.substring(0, MAX_ERROR_CHARS) + "...";
            }
            throw new InterviewException("Copilot CLI exited with code " + exitCode
                    + ". Make sure it is signed in (run 'copilot' once in a terminal) and the model name is valid."
                    + (errors.isEmpty() ? "" : "\n" + errors));
        }
        return output.standard();
    }

    private static final class Output implements ProcessListener {
        private final StringBuilder standard = new StringBuilder();
        private final StringBuilder errors = new StringBuilder();
        private long bytes;
        private boolean overflowed;

        @Override
        public synchronized void onTextAvailable(ProcessEvent event, Key outputType) {
            if (ProcessOutputTypes.SYSTEM.equals(outputType) || overflowed) {
                return;
            }
            String text = event.getText();
            bytes += text.getBytes(StandardCharsets.UTF_8).length;
            if (bytes > MAX_OUTPUT_BYTES) {
                overflowed = true;
                return;
            }
            (ProcessOutputTypes.STDERR.equals(outputType) ? errors : standard).append(text);
        }

        synchronized boolean overflowed() {
            return overflowed;
        }

        synchronized String standard() {
            return standard.toString();
        }

        synchronized String errors() {
            return errors.toString();
        }
    }
}
