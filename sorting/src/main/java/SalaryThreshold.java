import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Design an algorithm for computing the salary cap, given existing salaries and the target payroll.
 * <p>
 * For example, if there were five employees with salaries last year were $90,$30,$100,$40, and $20, and the target
 * payroll this year is $210, then 60 is a suitable salary cap, since 60 + 30 + 60 + 40 + 20 = 210.
 */
public class SalaryThreshold {
    public static double findSalaryCap(double targetPayroll, List<Double> currentSalaries) {
        currentSalaries.sort(Comparator.naturalOrder());

        List<Double> capSalaries = new ArrayList<>();

        // O(n)
        for (Double salaryCap : currentSalaries) {
            double total = 0;
            // O(n)
            for (Double salary : currentSalaries) {
                if (salary < salaryCap) {
                    total += salary;
                } else {
                    total += salaryCap;
                }
            }
            capSalaries.add(total);
        }

        // O(nlogn)
        int end = Math.abs(Collections.binarySearch(capSalaries, targetPayroll)) - 1;

        if (end > currentSalaries.size() - 1) {
            return -1.0;
        }

        double endRange = currentSalaries.get(end);
        int numAboveEnd = 0;
        double total = 0;

        // O(n)
        for (int i = 0; i < currentSalaries.size(); i++) {
            double currentSalary = currentSalaries.get(i);
            if (currentSalary < endRange) {
                total += currentSalary;
            } else {
                numAboveEnd++;
            }
        }

        return (targetPayroll - total) / numAboveEnd;
    }

    // turns out this can be simplified to a crazy math problem
    public static double findSalaryCapSimplified(double targetPayroll, List<Double> currentSalaries) {
        Collections.sort(currentSalaries);
        double unadjustedSalarySum = 0;
        for (int i = 0; i < currentSalaries.size(); ++i) {
            final double adjustedSalarySum
                    = currentSalaries.get(i) * (currentSalaries.size() - i);
            if (unadjustedSalarySum + adjustedSalarySum >= targetPayroll) {
                return (targetPayroll - unadjustedSalarySum) / (currentSalaries.size() - i);
            }
            unadjustedSalarySum += currentSalaries.get(i);
        }
        // No solution, since targetPayroll > existing payroll. return -1.®;
        return -1.0;
    }
}
