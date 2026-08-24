/**
 * Day 11, part 2 — proving the four approaches agree, and finding where they
 * do not.
 *
 * "It worked on my three test values" is not evidence. For a small int range
 * every combination can simply be enumerated, which turns a claim into a
 * proof — and then the same test on doubles shows the nested-if version is
 * genuinely broken.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-24
 */
public class LargestVerifier {

    private static final int LOW = -4;
    private static final int HIGH = 4;

    public static void main(String[] args) {
        System.out.println("Day 11 — Verifying largest-of-three");
        System.out.println();

        exhaustiveIntCheck();
        tieCases();
        whereDoublesDiverge();
    }

    // ── the four approaches, on int ────────────────────────────────────────

    static int byNestedIf(int a, int b, int c) {
        if (a >= b) {
            return a >= c ? a : c;
        } else {
            return b >= c ? b : c;
        }
    }

    static int byLadder(int a, int b, int c) {
        if (a >= b && a >= c) return a;
        else if (b >= a && b >= c) return b;
        else return c;
    }

    static int byMathMax(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    static int byTernary(int a, int b, int c) {
        return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
    }

    /**
     * Enumerates every (a,b,c) in [-4,4] — 9³ = 729 cases — and checks all four
     * against a reference answer computed by sorting.
     */
    private static void exhaustiveIntCheck() {
        int cases = 0;
        int mismatches = 0;

        for (int a = LOW; a <= HIGH; a++) {
            for (int b = LOW; b <= HIGH; b++) {
                for (int c = LOW; c <= HIGH; c++) {
                    cases++;

                    // Reference: independent of all four implementations.
                    int[] sorted = { a, b, c };
                    java.util.Arrays.sort(sorted);
                    int expected = sorted[2];

                    if (byNestedIf(a, b, c) != expected
                            || byLadder(a, b, c) != expected
                            || byMathMax(a, b, c) != expected
                            || byTernary(a, b, c) != expected) {
                        mismatches++;
                        System.out.printf("  MISMATCH a=%d b=%d c=%d expected=%d%n",
                                a, b, c, expected);
                    }
                }
            }
        }

        System.out.println("── Exhaustive int check ──");
        System.out.printf("  range [%d..%d], %d combinations tested%n", LOW, HIGH, cases);
        System.out.println("  mismatches: " + mismatches);
        System.out.println(mismatches == 0
                ? "  all four approaches agree on every case."
                : "  SOMETHING IS WRONG.");
        System.out.println();
    }

    /** Ties are where off-by-one comparison mistakes usually surface. */
    private static void tieCases() {
        int[][] ties = {
                { 5, 5, 3 },   // a == b, both larger
                { 5, 3, 5 },   // a == c
                { 3, 5, 5 },   // b == c
                { 7, 7, 7 },   // all equal
                { -2, -9, -4 } // all negative
        };

        System.out.println("── Tie and negative cases ──");
        for (int[] t : ties) {
            System.out.printf("  (%3d,%3d,%3d) -> nested=%d ladder=%d max=%d ternary=%d%n",
                    t[0], t[1], t[2],
                    byNestedIf(t[0], t[1], t[2]),
                    byLadder(t[0], t[1], t[2]),
                    byMathMax(t[0], t[1], t[2]),
                    byTernary(t[0], t[1], t[2]));
        }
        System.out.println("  With a tie, either equal value is a correct answer.");
        System.out.println();
    }

    // ── the double versions ────────────────────────────────────────────────

    static double nestedIfD(double a, double b, double c) {
        if (a >= b) {
            return a >= c ? a : c;
        } else {
            return b >= c ? b : c;
        }
    }

    static double mathMaxD(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    /**
     * Every comparison involving NaN is false (Day 8). A hand-written nested if
     * therefore takes the "b is bigger" branch whenever a is compared against
     * NaN — and can return a value that is not the largest at all.
     */
    private static void whereDoublesDiverge() {
        double nan = 0.0 / 0.0;

        System.out.println("── Where doubles diverge ──");
        System.out.printf("  (NaN, 5, 3)   nested=%.1f   Math.max=%s%n",
                nestedIfD(nan, 5, 3), mathMaxD(nan, 5, 3));
        System.out.printf("  (5, NaN, 3)   nested=%.1f   Math.max=%s%n",
                nestedIfD(5, nan, 3), mathMaxD(5, nan, 3));
        System.out.println();
        System.out.println("  (5, NaN, 3) returns 3.0 from the nested if — it skipped");
        System.out.println("  the 5 entirely. Trace it:");
        System.out.println("    a >= b  ->  5 >= NaN  ->  " + (5 >= nan) + "  -> take else branch");
        System.out.println("    b >= c  ->  NaN >= 3  ->  " + (nan >= 3) + "  -> return c = 3");
        System.out.println();
        System.out.println("  Math.max propagates NaN instead, which is the documented");
        System.out.println("  IEEE-754 behaviour: an unknown value makes the max unknown.");
        System.out.println();
        System.out.printf("  (-0.0, 0.0, -1) nested=%.1f   Math.max=%.1f%n",
                nestedIfD(-0.0, 0.0, -1), mathMaxD(-0.0, 0.0, -1));
        System.out.println("  -0.0 >= 0.0 is true, so the nested if keeps -0.0;");
        System.out.println("  Math.max deliberately treats 0.0 as the larger (Day 8).");
        System.out.println();
        System.out.println("  Conclusion: for doubles, use Math.max. Hand-rolled");
        System.out.println("  comparisons are correct only if NaN cannot occur.");
    }
}
