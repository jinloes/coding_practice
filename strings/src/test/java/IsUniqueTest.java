
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IsUniqueTest {
    private IsUnique isUnique;

    @BeforeEach
    public void setUp() throws Exception {
        isUnique = new IsUnique();
    }

    @Test
    public void isUnique() {
        assertThat(isUnique.isUnique("abc")).isTrue();
    }

    @Test
    public void isUniqueSingle() {
        assertThat(isUnique.isUnique("a")).isTrue();
    }

    @Test
    public void isUniqueEmpty() {
        assertThat(isUnique.isUnique("")).isTrue();
    }

    @Test
    public void isNotUnique() {
        assertThat(isUnique.isUnique("abb")).isFalse();
    }

    @Test
    public void isUniqueNoAdditional() {
        assertThat(isUnique.isUniqueNoAdditional("abc")).isTrue();
    }

    @Test
    public void isUniqueSingleNoAdditional() {
        assertThat(isUnique.isUniqueNoAdditional("a")).isTrue();
    }

    @Test
    public void isUniqueEmptyNoAdditional() {
        assertThat(isUnique.isUniqueNoAdditional("")).isTrue();
    }

    @Test
    public void isNotUniqueNoAdditional() {
        assertThat(isUnique.isUniqueNoAdditional("abb")).isFalse();
    }
}