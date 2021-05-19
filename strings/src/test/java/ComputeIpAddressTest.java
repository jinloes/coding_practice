import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ComputeIpAddressTest {

    @Test
    void compute() {
        assertThat(ComputeIpAddress.compute("19216811"))
                .hasSize(9)
                .allSatisfy(ipAddress -> assertThat(ipAddress).matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"))
                .allSatisfy(ipAddress -> {
                            List<Integer> numParts = Arrays.stream(ipAddress.split("\\."))
                                    .map(Integer::valueOf)
                                    .collect(Collectors.toList());
                            assertThat(numParts).allSatisfy(num -> {
                                assertThat(num).isBetween(0, 255);
                            });
                        }
                );
    }
}