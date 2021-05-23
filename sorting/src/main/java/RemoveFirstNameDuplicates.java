import io.vavr.Tuple2;

import java.util.Comparator;
import java.util.List;

/**
 * Design an efficient algorithm for removing all first-name duplicates from an array.
 */
public class RemoveFirstNameDuplicates {
    public static void eliminateDuplicate(List<Tuple2<String, String>> names) {
        names.sort(Comparator.comparing(Tuple2::_1));

        int writeIdx = 1;

        for (int i = 1; i < names.size(); i++) {
            if (!names.get(i)._1().equals(names.get(i - 1)._1())) {
                names.set(writeIdx, names.get(i));
                writeIdx++;
            }
        }

        names.subList(writeIdx, names.size()).clear();
    }
}
