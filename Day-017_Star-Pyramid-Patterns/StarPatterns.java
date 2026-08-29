import java.util.Scanner;

/**
 * Day 17 — star pyramid patterns.
 *
 * Every pattern here is the same three-loop skeleton:
 *
 *     for (row = 1; row <= n; row++) {     // which line
 *         for (...) print(' ');            // leading spaces
 *         for (...) print('*');            // the stars
 *         println();                       // end the line
 *     }
 *
 * Only the two inner bounds change. Getting a pattern right is therefore not
 * about the loops at all — it is about writing the spaces and stars for row i
 * as a formula in i and n.
 *
 * Run:  printf '5\n' | java StarPatterns
 *
 * @author  Divesh Agarwal
 * @since   2026-08-30
 */
public class StarPatterns {

    public static void main(String[] args) {
        System.out.println("Day 17 — Star pyramid patterns");

        int n;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\nRows: ");
            n = sc.hasNextInt() ? sc.nextInt() : 5;
        }

        if (n < 1) {
            System.out.println("Rows must be at least 1.");
            return;
        }

        rightTriangle(n);
        invertedRightTriangle(n);
        pyramid(n);
        invertedPyramid(n);
        diamond(n);
        hollowPyramid(n);
    }

    /** spaces = 0, stars = i. The simplest case: no alignment to worry about. */
    private static void rightTriangle(int n) {
        heading("Right triangle", "stars = i");

        for (int i = 1; i <= n; i++) {
            for (int s = 1; s <= i; s++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    /** spaces = 0, stars = n - i + 1. Counting down instead of up. */
    private static void invertedRightTriangle(int n) {
        heading("Inverted right triangle", "stars = n - i + 1");

        for (int i = 1; i <= n; i++) {
            for (int s = 1; s <= n - i + 1; s++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    /**
     * The classic. spaces = n - i, stars = 2i - 1.
     *
     * The star count is ODD on every row (1, 3, 5, 7...) which is what gives
     * the pyramid a single apex and a symmetric base. Each row gains two stars
     * and loses one space, so the total width stays 2n - 1 throughout.
     */
    private static void pyramid(int n) {
        heading("Pyramid", "spaces = n - i, stars = 2i - 1");

        for (int i = 1; i <= n; i++) {
            for (int sp = 1; sp <= n - i; sp++) {
                System.out.print(" ");
            }
            for (int s = 1; s <= 2 * i - 1; s++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    /** The pyramid read bottom-up: spaces = i - 1, stars = 2(n - i) + 1. */
    private static void invertedPyramid(int n) {
        heading("Inverted pyramid", "spaces = i - 1, stars = 2(n - i) + 1");

        for (int i = 1; i <= n; i++) {
            for (int sp = 1; sp <= i - 1; sp++) {
                System.out.print(" ");
            }
            for (int s = 1; s <= 2 * (n - i) + 1; s++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    /**
     * A pyramid stacked on an inverted one.
     *
     * The lower half runs from n-1 down to 1, NOT from n — otherwise the widest
     * row prints twice and the diamond gains a flat middle.
     */
    private static void diamond(int n) {
        heading("Diamond", "pyramid + inverted, minus the repeated middle row");

        for (int i = 1; i <= n; i++) {              // upper half
            printRow(n - i, 2 * i - 1);
        }
        for (int i = n - 1; i >= 1; i--) {          // lower half, n-1 down to 1
            printRow(n - i, 2 * i - 1);
        }
    }

    /**
     * Same bounds as the pyramid, but a star is printed only on the EDGES —
     * unless this is the last row, which is solid.
     *
     * The condition is where the work moved to: the loops are unchanged, only
     * the decision inside them differs.
     */
    private static void hollowPyramid(int n) {
        heading("Hollow pyramid", "edges only, plus a solid base");

        for (int i = 1; i <= n; i++) {
            for (int sp = 1; sp <= n - i; sp++) {
                System.out.print(" ");
            }
            for (int s = 1; s <= 2 * i - 1; s++) {
                boolean edge = (s == 1 || s == 2 * i - 1);
                boolean lastRow = (i == n);
                System.out.print(edge || lastRow ? "*" : " ");
            }
            System.out.println();
        }
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private static void printRow(int spaces, int stars) {
        for (int sp = 1; sp <= spaces; sp++) {
            System.out.print(" ");
        }
        for (int s = 1; s <= stars; s++) {
            System.out.print("*");
        }
        System.out.println();
    }

    private static void heading(String name, String formula) {
        System.out.println();
        System.out.println("── " + name + " ──   (" + formula + ")");
    }
}
