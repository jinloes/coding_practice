package com.jinloes.simple_functions;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PascalTriangleTest {
    private PascalTriangle pascalTriangle;


    @Before
    public void setUp() throws Exception {
        pascalTriangle = new PascalTriangle();
    }

    @Test
    public void generate() {
        assertThat(pascalTriangle.generate(3))
                .containsExactly(Lists.newArrayList(1),
                        Lists.newArrayList(1, 1),
                        Lists.newArrayList(1, 2, 1));

        assertThat(pascalTriangle.generate(5))
                .containsExactly(Lists.newArrayList(1),
                        Lists.newArrayList(1, 1),
                        Lists.newArrayList(1, 2, 1),
                        Lists.newArrayList(1, 3, 3, 1),
                        Lists.newArrayList(1, 4, 6, 4, 1));
    }
}