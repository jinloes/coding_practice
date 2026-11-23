package com.jinloes.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import com.jinloes.reflection.impl.DoublePrintFoo;
import com.jinloes.reflection.impl.MissingFoo;
import com.jinloes.reflection.impl.PrintFoo;
import com.jinloes.reflection.util.PolicyType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterfaceScannerTest {
  private InterfaceScanner scanner;

  @BeforeEach
  void setUp() {
    scanner = new InterfaceScanner();
  }

  @Test
  void test() {
    var result = scanner.doScan();
    var expected = Map.of(
        "Double", DoublePrintFoo.class,
        "print", PrintFoo.class
    );

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void executesMainImplementationsAndPolicyTypes() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream captured = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
      InterfaceScanner.main(new String[0]);
      new PrintFoo().doFoo();
      new DoublePrintFoo().doFoo();
      new MissingFoo().doFoo();
    } finally {
      System.setOut(originalOut);
    }

    String output = captured.toString(StandardCharsets.UTF_8);
    assertThat(output).contains("foo").contains("Foo");
    assertThat(PolicyType.values()).containsExactly(PolicyType.PRINT, PolicyType.DOUBLE_PRINT);
    assertThat(PolicyType.valueOf("PRINT")).isEqualTo(PolicyType.PRINT);
  }
}
