package com.jinloes.simple_functions;

import com.jinloes.simple_functions.array.ProductCalculator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductCalculatorTest {

  @Test public void testCalculate() {
    assertThat(ProductCalculator.calculate(new int[] { 3, 5, 4, 7 })).isEqualTo(new int[] { 140, 84, 105, 60 });
    assertThat(ProductCalculator.calculate(new int[] { 3, 4, 1, 2 })).isEqualTo(new int[] { 8, 6, 24, 12 });
    assertThat(ProductCalculator.calculate(new int[] { 3, 4, 0, 2 })).isEqualTo(new int[] { 0, 0, 24, 0 });
  }
}
