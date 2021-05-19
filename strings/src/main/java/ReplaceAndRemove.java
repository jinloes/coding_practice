/**
 * Write a program which takes as input an array of characters, and removes each 'b' and replaces each 'a' by two 'd's.
 * Specifically, along with the array, you are provided an integer-valued size. Size denotes the number of entries
 * of the array that the operation is to be applied to. You do not have to worry preserving about subsequent entries.
 */
public class ReplaceAndRemove {

    public static int replaceRemove(char[] chars, int size) {
        int writeIdx = 0;
        int numAs = 0;

        for (int i = 0; i < size; i++) {
            if (chars[i] != 'b') {
                chars[writeIdx] = chars[i];
                writeIdx++;
            }
            if (chars[i] == 'a') {
                numAs++;
            }
        }

        int currentIdx = writeIdx - 1;
        writeIdx = writeIdx + numAs - 1;

        int finalSize = writeIdx + 1;

        while (currentIdx >= 0) {
            if (chars[currentIdx] == 'a') {
                chars[writeIdx--] = 'd';
                chars[writeIdx--] = 'd';
            } else {
                chars[writeIdx] = chars[currentIdx];
            }
            currentIdx--;
        }

        return finalSize;
    }
}
