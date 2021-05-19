import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PalindromePermutationTest {
    private PalindromePermutation palindromePermutation;

    @BeforeEach
    public void setUp() throws Exception {
        palindromePermutation = new PalindromePermutation();
    }

    @Test
    public void isPalindromePermutation() {
        assertThat(palindromePermutation.isPalindromePermutation("Tact Coa")).isTrue();
    }

    @Test
    public void isPalindromePermutationLonger() {
        // Original Palindrome: Ulu day evil died deid live yad Ulu
        assertThat(palindromePermutation.isPalindromePermutation("Ulu evil yad Ulu live day died deid"))
                .isTrue();
    }

    @Test
    public void isPalindromePermutationLongerFalse() {
        assertThat(palindromePermutation.isPalindromePermutation("Ulu evil yad Ulu live day died deix"))
                .isFalse();
    }

    @Test
    public void isPalindromeShorter() {
        assertThat(palindromePermutation.isPalindromePermutation("rwtwodrrdoot ss o"))
                .isTrue();
    }

}