import java.util.Scanner;

/**
 * Day 18 — number patterns and Floyd's triangle.
 *
 * Day 17's star patterns were STATELESS: row i was computed from i and n
 * alone, so any row could be printed in isolation.
 *
 * Floyd's triangle is the first pattern that is not. It carries a running
 * counter ACROSS rows — the value printed depends on everything already
 * printed. That single change is what this day is really about; the rest
 * follows from it.
 *
 * Run:  printf '5\n' | java NumberPatterns
 *
 * @author  Divesh Agarwal
 * @since   2026-08-31
 */
public class NumberPatterns {

    public static void main(String[] args) {
        System.out.println("Day 18 — Number & Floyd's triangle patterns");

        int n;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\nRows: ");
            n = sc.hasNextInt() ? sc.nextInt() : 5;
        }

        if (n < 1) {
            System.out.println("Rows must be at least 1.");
            return;
        }

        floydsTriangle(n);
        rowNumberRepeated(n);
        countUpEachRow(n);
        numberPyramid(n);
        palindromicPyramid(n);
        pascalsTriangle(n);
    }

    /**
     * Floyd's triangle: the natural numbers laid into a right triangle.
     *
     *   1
     *   2  3
     *   4  5  6
     *   7  8  9 10
     *
     * The counter is declared OUTSIDE both loops and never resets. Row i holds
     * i values, so row i starts at i(i-1)/2 + 1 and ends at i(i+1)/2 — the
     * triangular numbers (verified in PatternMath).
     *
     * Alignment matters here in a way it never did for stars: the values grow
     * to several digits, so the field width has to be computed from the
     * largest value, not guessed.
     */
    private static void floydsTriangle(int n) {
        int last = n * (n + 1) / 2;                  // biggest value we will print
        int width = String.valueOf(last).length();

        heading("Floyd's triangle", "a counter that never resets");

        int counter = 1;
        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.printf("%" + (width + 1) + "d", counter);
                counter++;
            }
            System.out.println();
        }
    }

    /** Row i prints the value i, i times. Stateless — depends only on i. */
    private static void rowNumberRepeated(int n) {
        heading("Row number repeated", "print i, i times");

        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.print(row + " ");
            }
            System.out.println();
        }
    }

    /** Row i counts 1..i. The counter RESETS each row — contrast with Floyd. */
    private static void countUpEachRow(int n) {
        heading("Count up each row", "1..i, counter resets per row");

        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }

    /** Day 17's pyramid spacing, with numbers instead of stars. */
    private static void numberPyramid(int n) {
        heading("Number pyramid", "pyramid spacing, counting 1..i");

        for (int row = 1; row <= n; row++) {
            for (int sp = 1; sp <= n - row; sp++) {
                System.out.print("  ");
            }
            for (int col = 1; col <= row; col++) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }

    /**
     * Counts up to the row number and back down: 1, 1 2 1, 1 2 3 2 1.
     *
     * Row i has 2i - 1 values — the same odd count as Day 17's star pyramid,
     * which is why it forms the same triangular silhouette.
     */
    private static void palindromicPyramid(int n) {
        heading("Palindromic pyramid", "up then down, 2i-1 values per row");

        for (int row = 1; row <= n; row++) {
            for (int sp = 1; sp <= n - row; sp++) {
                System.out.print("  ");
            }
            for (int col = 1; col <= row; col++) {          // up
                System.out.print(col + " ");
            }
            for (int col = row - 1; col >= 1; col--) {      // and back down
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }

    /**
     * Pascal's triangle: each entry is the sum of the two above it.
     *
     * Built by updating one row in place, right to left. Going left to right
     * would overwrite row[k-1] before the next entry needs to read it.
     *
     * Uses long, not int: row 34 exceeds int range (see PatternMath).
     */
    private static void pascalsTriangle(int n) {
        heading("Pascal's triangle", "each entry = sum of the two above");

        long[] row = new long[n];

        for (int i = 0; i < n; i++) {
            for (int k = i; k > 0; k--) {        // RIGHT to left, in place
                row[k] = row[k] + row[k - 1];
            }
            row[0] = 1;

            for (int sp = 0; sp < n - i - 1; sp++) {
                System.out.print("   ");
            }
            for (int k = 0; k <= i; k++) {
                System.out.printf("%6d", row[k]);
            }
            System.out.println();
        }
    }

    private static void heading(String name, String note) {
        System.out.println();
        System.out.println("── " + name + " ──   (" + note + ")");
    }
}
