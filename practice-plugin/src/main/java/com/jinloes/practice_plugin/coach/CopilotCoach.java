package com.jinloes.practice_plugin.coach;

import com.microsoft.copilot.CopilotClient;
import com.microsoft.copilot.rpc.CopilotClientMode;
import com.microsoft.copilot.rpc.CopilotClientOptions;
import com.microsoft.copilot.rpc.RuntimeConnection;
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
import com.jinloes.plugin_common.copilot.CopilotModels;
import com.jinloes.plugin_common.platform.OwnedDirectory;

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
 * Runs the GitHub Copilot CLI as a post-check algorithm coach with every tool disabled. The prompt is
 * delivered over stdin and the process runs in a fresh, project-free plugin-owned directory (containing only
 * its ownership marker), so learner text cannot read or modify workspace files. Only one coaching request
 * runs at a time per application.
 */
public final class CopilotCoach {
    /** A tool name that does not exist: an allowlist containing only it leaves the model with no tools. */
    public static final String NO_TOOLS = "practice_no_tools";
    public static final int MAX_OUTPUT_BYTES = 512 * 1024;
    public static final int LIST_TIMEOUT_SECONDS = 20;
    static final String MARKER = ".practice-coach";
    static final String LIST_PREFIX = "Could not list models: ";
    static final int MAX_LIST_ERROR_CHARS = 300;
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
    public static final class CoachException extends Exception {
        public CoachException(String message) {
            super(message);
        }
    }

    public CopilotCoach() {
        this(Path.of(PathManager.getSystemPath()).resolve("algorithm-practice").resolve("coach"),
                List.of(), "copilot", POLL_MILLIS);
    }

    CopilotCoach(Path base, List<String> commandPrefix, String executableName) {
        this(base, commandPrefix, executableName, POLL_MILLIS);
    }

    CopilotCoach(Path base, List<String> commandPrefix, String executableName, long pollMillis) {
        this.runs = OwnedDirectory.under(base, MARKER);
        this.commandPrefix = List.copyOf(commandPrefix);
        this.executableName = executableName;
        this.pollMillis = pollMillis;
    }

    public static boolean isRunning() {
        return RUNNING.get();
    }

    /** The exact CLI argument list. Never add allow-tool flags here: coaching must stay tool-free. */
    public static List<String> arguments(String executable, String model) {
        List<String> arguments = new ArrayList<>(List.of(executable, "-s", "--no-custom-instructions",
                "--disable-builtin-mcps", "--available-tools=" + NO_TOOLS, "--no-ask-user"));
        if (model != null && !model.isBlank()) {
            arguments.add("--model=" + model.strip());
        }
        return List.copyOf(arguments);
    }

    String resolveExecutable(String configured) throws CoachException {
        if (configured != null && !configured.isBlank()) {
            Path path;
            try {
                path = Path.of(configured.strip());
            } catch (InvalidPathException invalid) {
                throw new CoachException("\"" + configured.strip() + "\" is not a valid path. "
                        + "Fix the Copilot CLI path in AI Coach settings or clear it to search PATH.");
            }
            Path normalized = path.toAbsolutePath().normalize();
            if (Files.isRegularFile(normalized)) {
                return normalized.toString();
            }
            throw new CoachException("Copilot CLI was not found at " + path
                    + ". Fix the path in AI Coach settings or clear it to search PATH.");
        }
        File found = PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS(executableName);
        if (found != null) {
            return found.getPath();
        }
        throw new CoachException("Copilot CLI was not found on PATH. Install it with "
                + "'npm install -g @github/copilot', sign in by running 'copilot' once in a terminal, "
                + "or set its path in AI Coach settings.");
    }

    /** Runs one coaching turn and returns the stripped Markdown response. */
    public String run(String prompt, Settings settings, BooleanSupplier canceled) throws CoachException {
        if (canceled.getAsBoolean()) {
            throw new CoachException("AI Coach request cancelled.");
        }
        if (!RUNNING.compareAndSet(false, true)) {
            throw new CoachException("Another AI Coach request is already running. Wait for it or cancel it.");
        }
        try {
            String executable = resolveExecutable(settings.cliPath());
            cleanupAbandoned();
            Path directory = newRunDirectory();
            try {
                String response = execute(executable, settings, directory, prompt, canceled).strip();
                if (response.isEmpty()) {
                    throw new CoachException("Copilot CLI returned an empty response. Try again.");
                }
                return response;
            } finally {
                deleteRunDirectory(directory);
            }
        } finally {
            RUNNING.set(false);
        }
    }

    /**
     * Lists the models the signed-in account can use through the Copilot Java SDK. Only the SDK handshake,
     * {@code models.list}, and shutdown reach the CLI: never a session or learner content.
     */
    public List<CopilotModels.Model> listModels(String cliPath, BooleanSupplier canceled) throws CoachException {
        return listModels(cliPath, canceled, Duration.ofSeconds(LIST_TIMEOUT_SECONDS));
    }

    List<CopilotModels.Model> listModels(String cliPath, BooleanSupplier canceled, Duration timeout)
            throws CoachException {
        String executable = resolveExecutable(cliPath);
        Path directory = newRunDirectory();
        try {
            return queryModels(executable, directory, canceled, timeout);
        } finally {
            deleteRunDirectory(directory);
        }
    }

    private List<CopilotModels.Model> queryModels(String executable, Path directory, BooleanSupplier canceled,
                                                  Duration timeout) throws CoachException {
        List<String> cliArgs = new ArrayList<>();
        String launcher = executable;
        if (!commandPrefix.isEmpty()) {
            launcher = commandPrefix.get(0);
            cliArgs.addAll(commandPrefix.subList(1, commandPrefix.size()));
            cliArgs.add(executable);
        }
        cliArgs.addAll(LIST_ARGUMENTS);
        CopilotClient client = new CopilotClient(new CopilotClientOptions()
                .setMode(CopilotClientMode.EMPTY)
                .setRuntime(RuntimeConnection.forSpawnedStdio(launcher)
                        .setArgs(cliArgs)
                        .setBaseDirectory(directory.toString())
                        .setWorkingDirectory(directory.toString())
                        .setEnvironment(EnvironmentUtil.getEnvironmentMap())));
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
            throw new CoachException(abort.getMessage());
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
                               Duration timeout) throws Abort, CoachException {
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

    static CoachException listFailure(Throwable failure) {
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
        return new CoachException(message);
    }

    private static final class Abort extends Exception {
        private final boolean interrupted;

        Abort(String message, boolean interrupted) {
            super(message);
            this.interrupted = interrupted;
        }
    }

    private Path newRunDirectory() throws CoachException {
        Path directory = runs.resolve("run-" + UUID.randomUUID().toString().replace("-", ""));
        try {
            runs.create(directory, "pid=" + ProcessHandle.current().pid() + "\n");
        } catch (IOException exception) {
            throw new CoachException("Could not prepare the coach directory: " + exception.getMessage());
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

    /** Deletes coach directories whose recorded process is no longer alive. */
    void cleanupAbandoned() {
        runs.cleanupAbandoned((directory, marker) -> !alive(marker));
    }

    private static boolean alive(Properties marker) throws IOException {
        try {
            long pid = Long.parseLong(marker.getProperty("pid", ""));
            return ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
        } catch (NumberFormatException malformed) {
            throw new IOException("Unreadable coach marker", malformed);
        }
    }

    private String execute(String executable, Settings settings, Path directory, String prompt,
                           BooleanSupplier canceled) throws CoachException {
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
            throw new CoachException("Could not start Copilot CLI: " + exception.getMessage());
        }
        handler.setShouldKillProcessSoftly(false);
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(settings.timeoutSeconds());
        Output output = new Output();
        handler.addProcessListener(output);
        handler.startNotify();
        byte[] promptBytes = prompt.getBytes(StandardCharsets.UTF_8);
        Thread.ofVirtual().name("practice-coach-stdin").start(
                () -> writePrompt(handler.getProcessInput(), promptBytes));

        String stopReason = null;
        while (!handler.waitFor(pollMillis)) {
            if (canceled.getAsBoolean()) {
                stopReason = "AI Coach request cancelled.";
            } else if (output.overflowed()) {
                stopReason = "The AI Coach produced more than " + (MAX_OUTPUT_BYTES / 1024)
                        + " KiB of output, so the request was stopped. Try again.";
            } else if (System.nanoTime() - deadline > 0) {
                stopReason = "The AI Coach did not answer within " + settings.timeoutSeconds()
                        + " seconds. Try again or raise the timeout in AI Coach settings.";
            }
            if (stopReason != null) {
                handler.killProcess();
                handler.destroyProcess();
                handler.waitFor(5_000);
                throw new CoachException(stopReason);
            }
        }
        Integer exitCode = handler.getExitCode();
        if (canceled.getAsBoolean()) {
            throw new CoachException("AI Coach request cancelled.");
        }
        if (output.overflowed()) {
            throw new CoachException("The AI Coach produced more than " + (MAX_OUTPUT_BYTES / 1024)
                    + " KiB of output, so the request was stopped. Try again.");
        }
        if (!Integer.valueOf(0).equals(exitCode)) {
            String errors = output.errors().strip();
            if (errors.length() > MAX_ERROR_CHARS) {
                errors = errors.substring(0, MAX_ERROR_CHARS) + "...";
            }
            throw new CoachException("Copilot CLI exited with code " + exitCode
                    + ". Make sure it is signed in (run 'copilot' once in a terminal) and the model name is valid."
                    + (errors.isEmpty() ? "" : "\n" + errors));
        }
        return output.standard();
    }

    static void writePrompt(OutputStream input, byte[] promptBytes) {
        try {
            input.write(promptBytes);
            input.close();
        } catch (IOException exception) {
            // The process may exit or be killed before reading stdin; its exit code or stopReason explains why.
        }
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
