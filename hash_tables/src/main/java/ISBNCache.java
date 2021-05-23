import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create a cache for looking up prices of books identified by their ISBN. You implement lookup, insert, and remove
 * methods. Use the Least Recently Used (LRU) policy for cache eviction. If an ISBN is already present, insert
 * should not change the price, but it should update that entry to be the most recently used entry.
 * Lookup should also update that entry to be the most recently used entry.
 */
public class ISBNCache {
    private final LinkedHashMap<String, Integer> cache;
    private final int maxSize;

    public ISBNCache() {
        maxSize = 5;
        cache = new LinkedHashMap<>(maxSize, 1.0f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> entry) {
                return size() > maxSize;
            }
        };
    }

    public Integer lookup(String isbn) {
        return cache.get(isbn);
    }

    public void insert(String isbn, int price) {
        Integer currentVal = cache.get(isbn);
        if (!cache.containsKey(isbn)) {
            cache.put(isbn, price);
        }
    }

    public void remove(String isbn) {
        cache.remove(isbn);
    }
}
