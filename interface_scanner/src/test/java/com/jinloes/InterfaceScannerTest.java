package com.jinloes;

import static org.assertj.core.api.Assertions.assertThat;

import com.jinloes.impl.DoublePrintFoo;
import com.jinloes.impl.PrintFoo;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InterfaceScannerTest {
  private InterfaceScanner scanner;

  @BeforeEach
  public void setUp() {
    scanner = new InterfaceScanner();
  }

  @Test
  public void test() {
    var result = scanner.doScan();
    var expected = Map.of(
        "Double", DoublePrintFoo.class,
        "print", PrintFoo.class
    );

    assertThat(result).isEqualTo(expected);
  }
}
