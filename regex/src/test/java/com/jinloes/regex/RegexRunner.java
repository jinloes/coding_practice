package com.jinloes.regex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.time.StopWatch;

public interface RegexRunner {

  default void runRegexes(BiFunction<String, String, MatchResult> regexRunner) throws IOException {
    runRegexes(null, regexRunner);
  }

  default void runRegexes(Integer regexLimit, BiFunction<String, String, MatchResult> regexRunner) throws IOException {
    List<String> regexes = getRegexes(regexLimit);

    try (Stream<Path> stream = getPathStream()) {
      stream.forEach(testFile -> {
        MutableInt count = new MutableInt(0);
        MutableInt failures = new MutableInt();
        MutableInt success = new MutableInt();

        StopWatch watch = new StopWatch();

        System.out.printf("Test file: %s\n", testFile);
        watch.start();
        try {
          String data = Files.readString(testFile);
          regexes.forEach(regex -> {
            try {
              MatchResult result = regexRunner.apply(regex, data);
              count.add(result.numMatches);
              success.increment();
            } catch (Exception e) {
              //e.printStackTrace();
              failures.increment();
            }
          });
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        watch.stop();

        System.out.printf("Num matches: %s\n", count);
        System.out.printf("Total regexes: %d\n", regexes.size());
        System.out.printf("Success regexes: %s\n", success);
        System.out.printf("Failure regexes: %s\n", failures);
        TimeUnit outputUnit = TimeUnit.MILLISECONDS;
        System.out.printf("Elapsed Time (%s): %s\n", outputUnit, watch.getTime(outputUnit));
      });
    }
  }

  default Stream<Path> getPathStream() throws IOException {
    return Files.list(Paths.get(System.getenv("TEST_FILE_DIR")));
  }

  default List<String> getRegexes(Integer limit) {
    Path regexPath = Path.of(System.getenv("TEST_REGEX_FILE"));
    try {
      List<String> regexes = Files.readAllLines(regexPath);
      if (limit != null) {
        regexes = regexes.subList(0, limit);
      }
      return regexes;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
