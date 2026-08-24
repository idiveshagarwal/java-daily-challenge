import java.util.Scanner;

/**
 * Day 11 — nested if: the largest of three numbers.
 *
 * The classic exercise, done four ways. All four agree on every int (proved
 * exhaustively in LargestVerifier), so the interesting question is not "does it
 * work" but "which one is hardest to get wrong".
 *
 * Run:  printf '12 45 23\n' | java LargestOfThree
 *
 * @author  Divesh Agarwal
 * @since   2026-08-24
 */
public class LargestOfThree {

    public static void main(String[] args) {
        System.out.println("Day 11 — Largest of three numbers");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter three integers: ");

            int a = sc.nextInt();
            int b = sc.nextInt();
            int c = sc.nextInt();

            System.out.println();
            System.out.println("a = " + a + ", b = " + b + ", c = " + c);
            System.out.println();
            System.out.println("nested if   -> " + byNestedIf(a, b, c));
            System.out.println("else-if     -> " + byLadder(a, b, c));
            System.out.println("Math.max    -> " + byMathMax(a, b, c));
            System.out.println("ternary     -> " + byTernary(a, b, c));
        }

        System.out.println();
        compareTheApproaches();
    }

    /**
     * Approach 1 — nested if. This is the chapter's method.
     *
     * The outer test picks the larger of a and b; the inner test compares that
     * winner against c. Only two comparisons run, never three.
     *
     * Note >= rather than >. With three-way ties any branch is correct, but >=
     * keeps the intent explicit: "a is at least as large as b".
     */
    private static int byNestedIf(int a, int b, int c) {
        if (a >= b) {
            if (a >= c) {
                return a;        // a beat b and c
            } else {
                return c;        // a beat b, but c beat a
            }
        } else {
            if (b >= c) {
                return b;        // b beat a and c
            } else {
                return c;        // b beat a, but c beat b
            }
        }
    }

    /**
     * Approach 2 — else-if ladder (Day 10). Each branch states a complete
     * condition, so it reads as a claim rather than a path.
     *
     * Costs up to four comparisons instead of two, but nothing is implied by
     * position: you can check any single line in isolation.
     */
    private static int byLadder(int a, int b, int c) {
        if (a >= b && a >= c) {
            return a;
        } else if (b >= a && b >= c) {
            return b;
        } else {
            return c;
        }
    }

    /** Approach 3 — the library already solves the two-value case. */
    private static int byMathMax(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    /** Approach 4 — ternary (Day 8). Compact; harder to read when nested. */
    private static int byTernary(int a, int b, int c) {
        return a >= b ? (a >= c ? a : c)
                      : (b >= c ? b : c);
    }

    private static void compareTheApproaches() {
        System.out.println("── Which to use ──");
        System.out.println("nested if : 2 comparisons, but correctness depends on");
        System.out.println("            the path taken — every branch must be traced.");
        System.out.println("ladder    : up to 4 comparisons; each branch is a complete,");
        System.out.println("            independently checkable claim.");
        System.out.println("Math.max  : clearest, and handles NaN correctly for doubles.");
        System.out.println("ternary   : compact, but nesting hurts readability fast.");
        System.out.println();
        System.out.println("For ints all four agree — verified exhaustively.");
        System.out.println("For doubles they do NOT. See LargestVerifier.");
    }
}
