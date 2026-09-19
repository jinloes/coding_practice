package com.jinloes.practice_plugin.run;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static com.jinloes.practice_plugin.run.CheckResult.Status.*;

public final class TestReports {
    private TestReports() {}

    public static CheckResult read(Path reportDirectory, int expectedTests, boolean full, int exitCode)
            throws IOException {
        int tests = 0;
        int failures = 0;
        int skipped = 0;
        boolean runtimeError = false;
        boolean timedOut = false;
        Set<String> classes = new HashSet<>();
        StringBuilder details = new StringBuilder();
        if (!Files.isDirectory(reportDirectory)) {
            return new CheckResult(RUNNER_ERROR, 0, 0,
                    "No fresh test report was produced. See the Run console for setup or build errors.");
        }
        var factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            try (var paths = Files.list(reportDirectory)) {
                for (Path path : paths.filter(p -> p.getFileName().toString().matches("TEST-.*\\.xml")).toList()) {
                    if (Files.size(path) > 2_000_000) {
                        throw new IOException("Test report exceeds the 2 MB limit: " + path.getFileName());
                    }
                    Element suite = factory.newDocumentBuilder().parse(path.toFile()).getDocumentElement();
                    String className = suite.getAttribute("name");
                    if (!classes.add(className)) {
                        throw new IOException("Duplicate test suite: " + className);
                    }
                    var cases = suite.getElementsByTagName("testcase");
                    for (int i = 0; i < cases.getLength(); i++) {
                        Element test = (Element) cases.item(i);
                        tests++;
                        if (test.getElementsByTagName("skipped").getLength() > 0) {
                            skipped++;
                        }
                        var errors = test.getElementsByTagName("error");
                        var failed = test.getElementsByTagName("failure");
                        Element failure = errors.getLength() > 0 ? (Element) errors.item(0)
                                : failed.getLength() > 0 ? (Element) failed.item(0) : null;
                        if (failure != null) {
                            failures++;
                            String type = failure.getAttribute("type");
                            timedOut |= type.contains("TimeoutException");
                            runtimeError |= !type.contains("Assertion") && !type.contains("TimeoutException");
                            if (details.length() < 24_000) {
                                String text = className + "." + test.getAttribute("name") + "\n"
                                        + failure.getAttribute("message") + "\n" + failure.getTextContent() + "\n\n";
                                details.append(text, 0, Math.min(text.length(), 24_000 - details.length()));
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Invalid test report", e);
        }
        Set<String> expectedClasses = full
                ? Set.of("com.jinloes.practice.ExamplesTest", "com.jinloes.practice.CorrectnessTest")
                : Set.of("com.jinloes.practice.ExamplesTest");
        if (tests != expectedTests || skipped > 0 || !classes.equals(expectedClasses)) {
            return new CheckResult(RUNNER_ERROR, tests, failures,
                    "Incomplete test run: expected " + expectedTests + " tests in " + expectedClasses
                            + "; received " + tests + ", skipped " + skipped + ".\n" + details);
        }
        if (failures > 0) {
            return new CheckResult(timedOut ? TIMED_OUT : runtimeError ? RUNTIME_ERROR : ASSERTION_FAILED,
                    tests, failures, details.toString());
        }
        if (exitCode != 0) {
            return new CheckResult(RUNNER_ERROR, tests, 0,
                    "Tests produced results, but Gradle exited with code " + exitCode + ". See the Run console.");
        }
        return new CheckResult(PASSED, tests, 0,
                "All " + tests + " supplied cases passed. This does not prove optimal complexity.");
    }
}
