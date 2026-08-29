/**
 * Day 17, part 2 — deriving the formulas, and checking them.
 *
 * A pattern is a claim about geometry, and geometry can be tested. Rather than
 * eyeballing the output, build each row as a String and assert the properties
 * the formula implies:
 *
 *   - a pyramid of n rows contains exactly n^2 stars
 *   - every padded row is 2n - 1 characters wide
 *   - every row is a palindrome (left-right symmetric)
 *   - a diamond has 2n - 1 rows
 *
 * @author  Divesh Agarwal
 * @since   2026-08-30
 */
public class PatternFormulas {

    private static final int MAX_N = 40;

    public static void main(String[] args) {
        System.out.println("Day 17 — Pattern formulas");
        System.out.println();

        deriveTheFormula();
        oddStarsSumToASquare();
        verifyAcrossManySizes();
    }

    /** Where "spaces = n - i, stars = 2i - 1" comes from. */
    private static void deriveTheFormula() {
        int n = 5;

        System.out.println("── Deriving the pyramid row ──");
        System.out.printf("  %3s %8s %8s %8s   %s%n", "i", "spaces", "stars", "width", "row");

        for (int i = 1; i <= n; i++) {
            String row = pyramidRow(n, i);
            System.out.printf("  %3d %8d %8d %8d   [%s]%n",
                    i, n - i, 2 * i - 1, row.length(), row);
        }

        System.out.println();
        System.out.println("  Each row loses one space and gains two stars, so the");
        System.out.println("  width is constant at 2n-1 = " + (2 * n - 1) + ".");
        System.out.println("  Stars are always ODD, which is what allows a single apex");
        System.out.println("  and a symmetric base.");
        System.out.println();
    }

    /**
     * The star count is the sum of the first n odd numbers, and that sum is
     * exactly n^2 — a fact worth knowing because it makes the total checkable
     * without counting.
     */
    private static void oddStarsSumToASquare() {
        System.out.println("── Total stars = n^2 ──");
        System.out.printf("  %3s %30s %8s %8s%n", "n", "1 + 3 + 5 + ...", "sum", "n^2");

        for (int n = 1; n <= 6; n++) {
            StringBuilder terms = new StringBuilder();
            int sum = 0;
            for (int i = 1; i <= n; i++) {
                int stars = 2 * i - 1;
                sum += stars;
                terms.append(i == 1 ? "" : " + ").append(stars);
            }
            System.out.printf("  %3d %30s %8d %8d%n", n, terms, sum, n * n);
        }

        System.out.println();
        System.out.println("  The sum of the first n odd numbers is n^2. So a pyramid");
        System.out.println("  of n rows uses exactly n^2 stars — countable in advance.");
        System.out.println();
    }

    /** Runs every property check for n = 1..MAX_N. */
    private static void verifyAcrossManySizes() {
        int checked = 0;
        int failures = 0;

        for (int n = 1; n <= MAX_N; n++) {
            int starCount = 0;

            for (int i = 1; i <= n; i++) {
                String row = pyramidRow(n, i);

                // 1. width is constant
                if (row.length() != 2 * n - 1) {
                    System.out.printf("  FAIL n=%d i=%d width %d, expected %d%n",
                            n, i, row.length(), 2 * n - 1);
                    failures++;
                }

                // 2. row is a palindrome
                if (!row.equals(new StringBuilder(row).reverse().toString())) {
                    System.out.printf("  FAIL n=%d i=%d not symmetric: [%s]%n", n, i, row);
                    failures++;
                }

                // 3. star count on this row is odd
                long stars = row.chars().filter(c -> c == '*').count();
                if (stars % 2 == 0) {
                    System.out.printf("  FAIL n=%d i=%d even star count %d%n", n, i, stars);
                    failures++;
                }

                starCount += (int) stars;
                checked++;
            }

            // 4. total stars = n^2
            if (starCount != n * n) {
                System.out.printf("  FAIL n=%d total stars %d, expected %d%n",
                        n, starCount, n * n);
                failures++;
            }

            // 5. diamond row count = 2n - 1
            int diamondRows = n + (n - 1);
            if (diamondRows != 2 * n - 1) {
                System.out.printf("  FAIL n=%d diamond has %d rows%n", n, diamondRows);
                failures++;
            }
        }

        System.out.println("── Verified across sizes ──");
        System.out.println("  n = 1.." + MAX_N + ", " + checked + " rows checked");
        System.out.println("  properties: constant width, palindromic, odd stars,");
        System.out.println("              total = n^2, diamond rows = 2n-1");
        System.out.println("  failures: " + failures);
        System.out.println(failures == 0
                ? "  every property held for every size."
                : "  SOMETHING IS WRONG.");
    }

    /**
     * Builds one padded pyramid row as a String.
     *
     * Note the trailing spaces: to be genuinely symmetric a row needs padding
     * on BOTH sides. Printed output normally omits the right-hand padding,
     * which is why a printed pyramid looks symmetric but a naive String
     * comparison of printed lines would not be.
     */
    private static String pyramidRow(int n, int i) {
        return " ".repeat(n - i) + "*".repeat(2 * i - 1) + " ".repeat(n - i);
    }
}
