package com.jinloes.practice_plugin.runner;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Executes {@code ExamplesTest} and {@code CorrectnessTest} on the JUnit Platform and writes one
 * {@code TEST-<class>.xml} report per suite class in the schema {@code TestReports} consumes.
 */
public final class JUnitSuiteRunner {
    static final List<String> SUITE_CLASSES = List.of(
            "com.jinloes.practice.ExamplesTest",
            "com.jinloes.practice.CorrectnessTest");

    private JUnitSuiteRunner() {
    }

    public static void main(String[] args) throws Exception {
        Path results = Path.of(args.length > 0 ? args[0] : VerificationMain.RESULTS_DIR);
        System.exit(executeSuites(results, SUITE_CLASSES));
    }

    static int executeSuites(Path results, List<String> suiteClasses) throws Exception {
        Files.createDirectories(results);
        Map<String, List<RecordedCase>> suites = new LinkedHashMap<>();
        for (String suiteClass : suiteClasses) {
            suites.put(suiteClass, new ArrayList<>());
        }
        Recorder recorder = new Recorder(suites);
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();
        for (String suiteClass : suiteClasses) {
            builder.selectors(selectClass(suiteClass));
        }
        LauncherDiscoveryRequest request = builder.build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(recorder);
        launcher.execute(request);

        for (Map.Entry<String, List<RecordedCase>> entry : suites.entrySet()) {
            writeSuiteXml(results, entry.getKey(), entry.getValue());
        }
        return recorder.unsuccessful ? 1 : 0;
    }

    private static void writeSuiteXml(Path results, String className, List<RecordedCase> cases) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element suite = document.createElement("testsuite");
        suite.setAttribute("name", className);
        document.appendChild(suite);
        for (RecordedCase recorded : cases) {
            Element testcase = document.createElement("testcase");
            testcase.setAttribute("name", recorded.name());
            if (recorded.skipped()) {
                testcase.appendChild(document.createElement("skipped"));
            } else if (recorded.failure() != null) {
                Throwable failure = recorded.failure();
                Element child = document.createElement(failure instanceof AssertionError ? "failure" : "error");
                child.setAttribute("type", failure.getClass().getName());
                if (failure.getMessage() != null) {
                    child.setAttribute("message", failure.getMessage());
                }
                child.setTextContent(stackTrace(failure));
                testcase.appendChild(child);
            }
            suite.appendChild(testcase);
        }
        Path target = results.resolve("TEST-" + className + ".xml");
        Path temporary = Files.createTempFile(results, ".report-", ".tmp");
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            try (OutputStream output = Files.newOutputStream(temporary)) {
                transformer.transform(new DOMSource(document), new StreamResult(output));
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static String stackTrace(Throwable failure) {
        StringWriter buffer = new StringWriter();
        failure.printStackTrace(new PrintWriter(buffer));
        return buffer.toString();
    }

    private static final class Recorder implements TestExecutionListener {
        private final Map<String, List<RecordedCase>> suites;
        private boolean unsuccessful;

        private Recorder(Map<String, List<RecordedCase>> suites) {
            this.suites = suites;
        }

        @Override
        public void executionSkipped(TestIdentifier identifier, String reason) {
            String suite = suiteClass(identifier);
            if (suite != null && identifier.isTest()) {
                unsuccessful = true;
                suites.get(suite).add(new RecordedCase(caseName(identifier), true, null));
                System.err.println("SKIPPED " + suite + "." + caseName(identifier) + ": " + reason);
            }
        }

        @Override
        public void executionFinished(TestIdentifier identifier, TestExecutionResult result) {
            String suite = suiteClass(identifier);
            if (suite == null) {
                return;
            }
            TestExecutionResult.Status status = result.getStatus();
            if (identifier.isTest()) {
                if (status == TestExecutionResult.Status.SUCCESSFUL) {
                    suites.get(suite).add(new RecordedCase(caseName(identifier), false, null));
                } else if (status == TestExecutionResult.Status.ABORTED) {
                    unsuccessful = true;
                    suites.get(suite).add(new RecordedCase(caseName(identifier), true, null));
                } else {
                    unsuccessful = true;
                    Throwable failure = result.getThrowable()
                            .orElseGet(() -> new IllegalStateException("Test failed without throwable"));
                    suites.get(suite).add(new RecordedCase(caseName(identifier), false, failure));
                    System.err.println("FAILED " + suite + "." + caseName(identifier));
                    failure.printStackTrace(System.err);
                }
            } else if (status == TestExecutionResult.Status.FAILED) {
                unsuccessful = true;
                Throwable failure = result.getThrowable()
                        .orElseGet(() -> new IllegalStateException("Suite failed without throwable"));
                suites.get(suite).add(new RecordedCase("initializationError", false, failure));
                System.err.println("FAILED " + suite);
                failure.printStackTrace(System.err);
            }
        }

        private String suiteClass(TestIdentifier identifier) {
            TestSource source = identifier.getSource().orElse(null);
            if (source instanceof MethodSource methodSource && suites.containsKey(methodSource.getClassName())) {
                return methodSource.getClassName();
            }
            if (source instanceof ClassSource classSource && suites.containsKey(classSource.getClassName())) {
                return classSource.getClassName();
            }
            return null;
        }

        private static String caseName(TestIdentifier identifier) {
            TestSource source = identifier.getSource().orElse(null);
            if (source instanceof MethodSource methodSource) {
                return methodSource.getMethodName();
            }
            return identifier.getDisplayName();
        }
    }

    private record RecordedCase(String name, boolean skipped, Throwable failure) {
    }
}
