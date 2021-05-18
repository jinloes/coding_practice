import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Write a program that takes an integer argument and returns all the primes between 1 and that integer.
 * For example,if theinputis18,youshould return(2,3,5,7,11,13,17).
 */
public class EnumeratePrimes {

    public static List<Integer> enumerate(int upperBound) {
        List<Integer> primes = new ArrayList<>();

        Set<Integer> notPrime = new HashSet<>();

        for (int i = 2; i <= upperBound; i++) {
            if (!notPrime.contains(i) && isPrime(i)) {
                primes.add(i);
            } else {
                for (int j = i; j <= upperBound; j += i) {
                    notPrime.add(j);
                }
            }
        }

        return primes;

    }

    private static boolean isPrime(int n) {

        if (n <= 1) {
            return false;
        } else if (n == 2) {
            return true;
        } else if (n % 2 == 0) {
            return false;
        }

        // If not, then just check the odds
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
