package com.jinloes.regex;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class PCRE2GrepEngine {

  public List<String> test(String toTest, List<String> flags, String regex) throws IOException, InterruptedException {
    List<String> pcre2Cmd = new ArrayList<>();
    pcre2Cmd.add("pcre2grep");

    pcre2Cmd.add("-u");
    if (!flags.isEmpty()) {
      pcre2Cmd.addAll(flags);
    }

    /*pcre2Cmd.add("-f");
    pcre2Cmd.add(regexes.toString());*/
    pcre2Cmd.add(regex);

    List<ProcessBuilder> builderList = List.of(
        new ProcessBuilder("echo", toTest),
        new ProcessBuilder(pcre2Cmd)
    );

    List<Process> processList = ProcessBuilder.startPipeline(builderList);
    Process last = processList.getLast();

    last.waitFor(1, TimeUnit.MINUTES);

    List<String> error = IOUtils.readLines(last.getErrorStream(), Charset.defaultCharset());
    if (!error.isEmpty()) {
      throw new RuntimeException("Failed to execute regex: " + error);
    }
    return IOUtils.readLines(last.getInputStream(), Charset.defaultCharset());
  }

  public List<String> test(Path toTest, List<String> flags, String regex) throws IOException, InterruptedException {
    List<String> pcre2Cmd = new ArrayList<>();
    pcre2Cmd.add("pcre2grep");

    pcre2Cmd.add("--buffer-size=20M");
    pcre2Cmd.add("-u");
    if (!flags.isEmpty()) {
      pcre2Cmd.addAll(flags);
    }

    pcre2Cmd.add(StringUtils.wrap(regex, "\""));
    pcre2Cmd.add(toTest.toString());


    Process process = new ProcessBuilder(pcre2Cmd)
        .start();

    process.waitFor(1, TimeUnit.MINUTES);

    List<String> error = IOUtils.readLines(process.getErrorStream(), Charset.defaultCharset());
    if (!error.isEmpty()) {
      throw new RuntimeException("Failed to execute regex: " + error);
    }
    return IOUtils.readLines(process.getInputStream(), Charset.defaultCharset());
  }
}