package com.jinloes.regex;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class PCRE2GrepTest implements RegexRunner {
  private PCRE2GrepEngine pcre2GrepEngine;

  @BeforeEach
  void setUp() {
    pcre2GrepEngine = new PCRE2GrepEngine();
  }

  @DisplayName("Pcre4j valid patterns")
  @ParameterizedTest
  @ArgumentsSource(ValidPCRE2TestPatterns.class)
  public void test(String pattern, String toTest, boolean expected) throws IOException, InterruptedException {
    //flags = Objects.requireNonNullElse(flags, 0);

    List<String> flagList = new ArrayList<>();
    /*if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
      flagList.add("-i");
    }*/

    List<String> output = pcre2GrepEngine.test(toTest, flagList, pattern);

    System.out.println(output);

    if (expected) {
      assertThat(output).isNotEmpty();
    } else {
      assertThat(output).isEmpty();
    }
  }

  @Test
  @Disabled
  public void testFromFile() throws IOException {
    List<String> regexes = getRegexes(null);

    try (Stream<Path> stream = Files.list(Paths.get(System.getenv("TEST_FILE_DIR")))) {
      stream.parallel()
          .forEach(testFile -> {
            MutableInt failures = new MutableInt();
            MutableInt success = new MutableInt();

            StopWatch watch = new StopWatch();

            watch.start();

            regexes.forEach(regex -> {
              try {
                List<String> output = pcre2GrepEngine.test(testFile, List.of(), regex);
                //System.out.println(output);
                success.increment();
              } catch (Exception e) {
                //e.printStackTrace();
                failures.increment();
              }
            });

            watch.stop();

            System.out.printf("Test file: %s\n", testFile);
            System.out.printf("Total regexes: %d\n", regexes.size());
            System.out.printf("Success regexes: %s\n", success);
            System.out.printf("Failure regexes: %s\n", failures);
            TimeUnit outputUnit = TimeUnit.MILLISECONDS;
            System.out.printf("Elapsed Time (%s): %s\n", outputUnit, watch.getTime(outputUnit));
          });
    }
  }
}
