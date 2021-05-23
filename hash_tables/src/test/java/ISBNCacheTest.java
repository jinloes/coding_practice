import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ISBNCacheTest {
    private ISBNCache cache;

    @BeforeEach
    void setUp() {
        cache = new ISBNCache();
        cache.insert("abc", 1);
    }

    @Test
    void lookup() {
        assertThat(cache.lookup("abc")).isEqualTo(1);
    }

    @Test
    void insert() {
        cache.insert("abc2", 1);
        cache.insert("abc3", 1);
        cache.insert("abc4", 1);
        cache.insert("abc5", 1);
        cache.insert("abc6", 1);

        assertThat(cache.lookup("abc2")).isEqualTo(1);
        assertThat(cache.lookup("abc")).isNull();


        cache.insert("abc6", 2);
        assertThat(cache.lookup("abc6")).isEqualTo(1);
    }

    @Test
    void remove() {
        assertThat(cache.lookup("abc")).isEqualTo(1);

        cache.remove("abc");

        assertThat(cache.lookup("abc")).isNull();
    }
}