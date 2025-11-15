package binary_search;

/**
 * Forward declaration of guess API.
 *
 * @param num your guess
 * @return -1 if num is higher than the picked number
 * 1 if num is lower than the picked number
 * otherwise return 0
 * int guess(int num);
 */

public class GuessGame {
    private int num;

    public GuessGame(int num) {
        this.num = num;
    }

    public int guess(int val) {
        if (val < num) {
            return 1;
        } else if (val > num) {
            return -1;
        } else {
            return 0;
        }
    }

    public static class GuessGameImpl extends GuessGame {
        public GuessGameImpl(int num) {
            super(num);
        }

        public int guessNumber(int n) {
            int start = 1;
            int end = n;

            int result = guess(n);

            while (result != 0) {
                int guessVal = start + (end - start) / 2;
                result = guess(guessVal);
                if (result == 0) {
                    return guessVal;
                } else if (result == 1) {
                    start = guessVal + 1;
                } else {
                    end = guessVal - 1;
                }
            }

            return n;
        }
    }
}