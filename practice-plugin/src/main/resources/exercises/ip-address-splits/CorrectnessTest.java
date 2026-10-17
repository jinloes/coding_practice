package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void rejectsTooLongInput() {
        assertThat(Solution.splitIpAddresses("1234567890123")).isEmpty();
    }

    @Test
    void rejectsLeadingZerosInMultiDigitParts() {
        assertThat(Solution.splitIpAddresses("010010"))
                .containsExactly("0.10.0.10", "0.100.1.0");
    }

    @Test
    void rejectsPartsAbove255() {
        assertThat(Solution.splitIpAddresses("256256256256")).isEmpty();
    }

    @Test
    void everyReturnedAddressIsValidForSeededInputs() {
        for (String digits : List.of("101023", "19216811", "1111", "99999999", "172162541")) {
            assertThat(Solution.splitIpAddresses(digits))
                    .as("valid restorations for %s", digits)
                    .allSatisfy(address -> assertValidAddress(digits, address));
        }
    }

    private static void assertValidAddress(String original, String address) {
        assertThat(address.replace(".", "")).isEqualTo(original);
        String[] parts = address.split("\\.", -1);
        assertThat(parts).hasSize(4);
        for (String part : parts) {
            assertThat(part).isNotEmpty();
            if (part.length() > 1) {
                assertThat(part).doesNotStartWith("0");
            }
            assertThat(Integer.parseInt(part)).isBetween(0, 255);
        }
    }
}
