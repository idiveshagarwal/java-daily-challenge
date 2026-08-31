/**
 * Day 18, part 2 — the arithmetic behind the patterns, checked.
 *
 * Floyd's triangle and Pascal's triangle both make claims that can be tested
 * against closed-form formulas rather than eyeballed:
 *
 *   Floyd  row i starts at i(i-1)/2 + 1 and ends at i(i+1)/2
 *          n rows contain n(n+1)/2 values in total
 *   Pascal row i sums to 2^i, is symmetric, and entry (i,k) = C(i,k)
 *
 * @author  Divesh Agarwal
 * @since   2026-08-31
 */
public class PatternMath {

    public static void main(String[] args) {
        System.out.println("Day 18 — Pattern arithmetic");
        System.out.println();

        floydRowBoundaries();
        verifyFloyd(200);
        pascalProperties();
        pascalOverflowsInt();
    }

    /**
     * Row i of Floyd's triangle holds i values, so the last value in row i is
     * 1 + 2 + ... + i — the i-th triangular number, i(i+1)/2.
     */
    private static void floydRowBoundaries() {
        System.out.println("── Floyd row boundaries ──");
        System.out.printf("  %4s %8s %8s %8s%n", "row", "count", "starts", "ends");

        for (int i = 1; i <= 6; i++) {
            System.out.printf("  %4d %8d %8d %8d%n",
                    i, i, i * (i - 1) / 2 + 1, i * (i + 1) / 2);
        }

        System.out.println();
        System.out.println("  ends = 1+2+...+i = i(i+1)/2, the triangular numbers.");
        System.out.println("  starts = previous row's end + 1.");
        System.out.println();
    }

    /** Walks the real counter and compares it to the formula at every row. */
    private static void verifyFloyd(int maxRows) {
        int counter = 1;
        int failures = 0;
        int valuesChecked = 0;

        for (int row = 1; row <= maxRows; row++) {
            int expectedStart = row * (row - 1) / 2 + 1;
            if (counter != expectedStart) {
                System.out.printf("  FAIL row %d starts at %d, formula says %d%n",
                        row, counter, expectedStart);
                failures++;
            }

            for (int col = 1; col <= row; col++) {
                counter++;
                valuesChecked++;
            }

            int expectedEnd = row * (row + 1) / 2;
            if (counter - 1 != expectedEnd) {
                System.out.printf("  FAIL row %d ends at %d, formula says %d%n",
                        row, counter - 1, expectedEnd);
                failures++;
            }
        }

        int expectedTotal = maxRows * (maxRows + 1) / 2;

        System.out.println("── Floyd verified ──");
        System.out.println("  rows 1.." + maxRows + ", " + valuesChecked + " values emitted");
        System.out.println("  formula predicts n(n+1)/2 = " + expectedTotal);
        System.out.println("  failures: " + failures);
        System.out.println(failures == 0 && valuesChecked == expectedTotal
                ? "  counter matched the closed form at every row boundary."
                : "  SOMETHING IS WRONG.");
        System.out.println();
    }

    /**
     * Three checkable properties of Pascal's triangle:
     *   row i sums to 2^i
     *   row i is symmetric
     *   entry (i,k) equals the binomial coefficient C(i,k)
     */
    private static void pascalProperties() {
        int rows = 20;
        long[] row = new long[rows];
        int failures = 0;

        for (int i = 0; i < rows; i++) {
            for (int k = i; k > 0; k--) {
                row[k] = row[k] + row[k - 1];
            }
            row[0] = 1;

            // sum = 2^i
            long sum = 0;
            for (int k = 0; k <= i; k++) {
                sum += row[k];
            }
            if (sum != (1L << i)) {
                System.out.printf("  FAIL row %d sums to %d, expected %d%n", i, sum, 1L << i);
                failures++;
            }

            // symmetry
            for (int k = 0; k <= i / 2; k++) {
                if (row[k] != row[i - k]) {
                    System.out.printf("  FAIL row %d not symmetric at k=%d%n", i, k);
                    failures++;
                }
            }

            // matches C(i,k) computed independently
            for (int k = 0; k <= i; k++) {
                if (row[k] != binomial(i, k)) {
                    System.out.printf("  FAIL row %d k=%d is %d, C(%d,%d)=%d%n",
                            i, k, row[k], i, k, binomial(i, k));
                    failures++;
                }
            }
        }

        System.out.println("── Pascal verified ──");
        System.out.println("  rows 0.." + (rows - 1) + " checked for:");
        System.out.println("    row sum = 2^i, symmetry, and entry = C(i,k)");
        System.out.println("  failures: " + failures);
        System.out.println(failures == 0
                ? "  all three properties held on every row."
                : "  SOMETHING IS WRONG.");
        System.out.println();
    }

    /**
     * C(n,k) computed multiplicatively, dividing as it goes so intermediate
     * values stay small. Computing n! first would overflow by n = 21.
     */
    private static long binomial(int n, int k) {
        if (k > n - k) {
            k = n - k;                       // C(n,k) == C(n,n-k), take the cheaper
        }
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }

    /**
     * Pascal's entries grow fast. Row 34 is the first whose largest entry
     * exceeds Integer.MAX_VALUE, so an int-based triangle silently wraps
     * (Day 4) from that row on — the same failure as Day 14's reverse.
     */
    private static void pascalOverflowsInt() {
        long[] row = new long[40];
        int firstBadRow = -1;
        long firstBadValue = 0;

        for (int i = 0; i < 40; i++) {
            for (int k = i; k > 0; k--) {
                row[k] = row[k] + row[k - 1];
            }
            row[0] = 1;

            long max = 0;
            for (int k = 0; k <= i; k++) {
                max = Math.max(max, row[k]);
            }
            if (max > Integer.MAX_VALUE) {
                firstBadRow = i;
                firstBadValue = max;
                break;
            }
        }

        System.out.println("── Pascal outgrows int ──");
        System.out.println("  Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("  first row exceeding it: row " + firstBadRow
                + ", largest entry " + firstBadValue);
        System.out.println();
        System.out.println("  An int[] triangle wraps silently from row " + firstBadRow
                + " on —");
        System.out.println("  no exception, just wrong numbers (Day 4, Day 14).");
        System.out.println();
        System.out.println("  long pushes the limit to row 67 (largest entry there is");
        System.out.println("  14226520737620288370, past Long.MAX_VALUE = "
                + Long.MAX_VALUE + ").");
        System.out.println("  Doubling the bit width buys about 33 more rows, not");
        System.out.println("  twice as many — the entries grow exponentially.");
        System.out.println("  BigInteger is the only real answer for deep triangles.");
    }
}
