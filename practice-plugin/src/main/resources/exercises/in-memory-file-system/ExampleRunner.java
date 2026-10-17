package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: operations separated by commas, for example: mkdir /a/b, add /a/b/c.txt hi, ls /a/b, read /a/b/c.txt";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        Solution fs = new Solution(); fs.mkdir("/a/b"); fs.addContentToFile("/a/b/c.txt", "hi"); assertThat(fs.ls("/a/b")).containsExactly("c.txt");
        Solution second = new Solution(); second.addContentToFile("/a/b/c.txt", "hi"); second.addContentToFile("/a/b/c.txt", " there"); assertThat(second.readContentFromFile("/a/b/c.txt")).isEqualTo("hi there");
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        Solution fs = new Solution();
        try {
            for (String op : input.split(",")) {
                String t = op.trim();
                if (t.startsWith("mkdir ")) { fs.mkdir(t.substring(6).trim()); System.out.println("mkdir"); }
                else if (t.startsWith("add ")) { String rest=t.substring(4).trim(); int sp=rest.indexOf(' '); fs.addContentToFile(rest.substring(0, sp), rest.substring(sp+1)); System.out.println("add"); }
                else if (t.startsWith("ls ")) System.out.println(fs.ls(t.substring(3).trim()));
                else if (t.startsWith("read ")) System.out.println(fs.readContentFromFile(t.substring(5).trim()));
                else { System.out.println("Unknown operation. " + USAGE); return; }
            }
        } catch (RuntimeException exception) { System.out.println("Could not run file-system operations. " + USAGE); }
    }
}
