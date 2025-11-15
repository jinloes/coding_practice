package com.jinloes.sorting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HighAccessEmployeeTest {
    private HighAccessEmployee highAccessEmployee;

    @BeforeEach
    void setUp() {
        highAccessEmployee = new HighAccessEmployee();
    }

    @ParameterizedTest
    @MethodSource("findHighAccessEmployeesArgs")
    void findHighAccessEmployees(List<List<String>> times, List<String> expected) {
        assertThat(highAccessEmployee.findHighAccessEmployees(times))
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    private static Stream<Arguments> findHighAccessEmployeesArgs() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                List.of("a", "0549"),
                                List.of("b", "0457"),
                                List.of("a", "0532"),
                                List.of("a", "0621"),
                                List.of("b", "0540")
                        ),
                        List.of("a")
                ),
                Arguments.of(
                        List.of(
                                List.of("kchzzdso", "2329"),
                                List.of("kchzzdso", "2326"),
                                List.of("kchzzdso", "2329")
                        ),
                        List.of("kchzzdso")
                ),
                Arguments.of(
                        List.of(
                                List.of("eazbkekis","1034"),
                                List.of("relf","1126"),
                                List.of("afwpabwyds","1114"),
                                List.of("afwpabwyds","1105"),
                                List.of("relf","1031"),
                                List.of("afwpabwyds","1010"),
                                List.of("vzqpz","1047"),
                                List.of("relf","1103")
                        ),
                        List.of("relf")
                ),
                Arguments.of(
                        List.of(
                                List.of("qzgyyji","1945"),
                                List.of("qzgyyji","1855"),
                                List.of("jsxkxtugi","1859"),
                                List.of("hhjuaxal","1940"),
                                List.of("hhjuaxal","1831"),
                                List.of("jsxkxtugi","1841"),
                                List.of("hhjuaxal","1918"),
                                List.of("jsxkxtugi","1941"),
                                List.of("hhjuaxal","1852")
                        ),
                        List.of("hhjuaxal")
                )
        );
    }
}