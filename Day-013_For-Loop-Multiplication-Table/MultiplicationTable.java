import java.util.Scanner;

/**
 * Day 13 — the for loop, via multiplication tables.
 *
 * A single table is one loop. The full grid is a nested loop, and it is the
 * clearest small example of the rule that matters: the INNER loop runs to
 * completion for every single step of the outer one.
 *
 * Run:  printf '7\n' | java MultiplicationTable
 *
 * @author  Divesh Agarwal
 * @since   2026-08-26
 */
public class MultiplicationTable {

    private static final int UP_TO = 10;

    public static void main(String[] args) {
        System.out.println("Day 13 — Multiplication tables (for loop)");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Which table? ");
            int n = sc.nextInt();

            System.out.println();
            singleTable(n);
            System.out.println();
            fullGrid();
        }
    }

    /**
     * One table — a single loop.
     *
     * The counter starts at 1, not 0: the loop bound should express the
     * problem, not a habit. A times table has no zeroth row.
     */
    private static void singleTable(int n) {
        System.out.println("── Table of " + n + " ──");

        for (int i = 1; i <= UP_TO; i++) {
            System.out.printf("  %2d x %2d = %3d%n", n, i, n * i);
        }
    }

    /**
     * The full grid — a nested loop.
     *
     * Outer picks the row; inner walks that row's columns. The inner loop runs
     * UP_TO times for EACH outer step, so the body executes 10 x 10 = 100
     * times. That multiplication is where nested-loop cost comes from.
     *
     * printf does the alignment: %4d right-pads each cell to a fixed width so
     * the columns line up regardless of digit count.
     */
    private static void fullGrid() {
        System.out.println("── Full grid (nested loop) ──");

        // Header row.
        System.out.print("     ");
        for (int col = 1; col <= UP_TO; col++) {
            System.out.printf("%4d", col);
        }
        System.out.println();
        System.out.print("     ");
        for (int col = 1; col <= UP_TO; col++) {
            System.out.print("----");
        }
        System.out.println();

        int bodyRuns = 0;

        for (int row = 1; row <= UP_TO; row++) {          // outer: 10 times
            System.out.printf("%3d |", row);

            for (int col = 1; col <= UP_TO; col++) {      // inner: 10 times EACH
                System.out.printf("%4d", row * col);
                bodyRuns++;
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("  inner body executed " + bodyRuns + " times (10 x 10)");
        System.out.println("  Nested loops multiply: two 10-step loops = 100 steps,");
        System.out.println("  two 1000-step loops = 1,000,000.");
    }
}
