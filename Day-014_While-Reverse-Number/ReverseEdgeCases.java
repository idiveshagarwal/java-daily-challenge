/**
 * Day 14, part 2 — four inputs that break the textbook reverse.
 *
 * The three-line algorithm is correct for the case everyone tests (a small
 * positive number). Everything interesting is at the edges.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-27
 */
public class ReverseEdgeCases {

    /** The version most tutorials print: guards with n > 0. */
    static int withGreaterThanZero(int n) {
        int rev = 0;
        while (n > 0) {
            rev = rev * 10 + n % 10;
            n /= 10;
        }
        return rev;
    }

    /** The same loop, guarded with n != 0. */
    static int withNotEqualZero(int n) {
        int rev = 0;
        while (n != 0) {
            rev = rev * 10 + n % 10;
            n /= 10;
        }
        return rev;
    }

    public static void main(String[] args) {
        System.out.println("Day 14 — Reverse: edge cases");
        System.out.println();

        comparison();
        negativesWorkBecauseOfModulo();
        trailingZeros();
        overflow();
    }

    private static void comparison() {
        int[] tests = { 123, -123, 0, 1200, 5, 1534236469 };

        System.out.println("── while (n > 0) vs while (n != 0) ──");
        System.out.printf("  %12s %14s %14s%n", "input", "n > 0", "n != 0");
        for (int t : tests) {
            System.out.printf("  %12d %14d %14d%n",
                    t, withGreaterThanZero(t), withNotEqualZero(t));
        }
        System.out.println();
        System.out.println("  Only -123 differs: n > 0 is false immediately, so the");
        System.out.println("  loop never runs and it returns the initial rev = 0.");
        System.out.println();
    }

    /**
     * The negative case works with `!= 0` for a reason worth stating: in Java
     * the result of % takes the sign of the DIVIDEND (Day 7).
     *
     * -123 % 10 is -3, not 7. So every digit extracted is negative, rev
     * accumulates negatively, and the sign survives for free.
     */
    private static void negativesWorkBecauseOfModulo() {
        System.out.println("── Why negatives work at all ──");
        System.out.println("  -123 % 10 = " + (-123 % 10) + "    (sign follows the dividend, Day 7)");
        System.out.println("  -123 / 10 = " + (-123 / 10) + "   (truncates toward zero)");
        System.out.println();

        int n = -123;
        int rev = 0;
        while (n != 0) {
            rev = rev * 10 + n % 10;
            n /= 10;
        }
        System.out.println("  -123 reversed = " + rev);
        System.out.println();
        System.out.println("  In a language where % follows the divisor instead,");
        System.out.println("  this same code would produce nonsense. It is correct");
        System.out.println("  by virtue of a language rule, not by design.");
        System.out.println();
    }

    /** Not a bug — but the operation is not reversible. */
    private static void trailingZeros() {
        System.out.println("── Trailing zeros are lost ──");
        for (int n : new int[] { 1200, 120, 100 }) {
            System.out.printf("  %6d -> %d%n", n, withNotEqualZero(n));
        }
        System.out.println();
        System.out.println("  1200 -> 21, and 21 reversed is 12, not 1200.");
        System.out.println("  Leading zeros cannot be represented in an int, so");
        System.out.println("  reverse(reverse(n)) != n whenever n ends in 0.");
        System.out.println("  Return a String if the digits must round-trip.");
        System.out.println();
    }

    /**
     * The serious one. A 10-digit int reversed often exceeds int range, and
     * Java wraps silently (Day 4) — the method returns a plausible-looking
     * number that is simply wrong.
     */
    private static void overflow() {
        System.out.println("── Silent overflow ──");
        System.out.println("  Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println();

        int[] tests = { 1534236469, 2147483647 };
        System.out.printf("  %13s %14s %16s %14s%n", "input", "naive int", "correct (long)", "exact()");
        for (int t : tests) {
            Integer exact = ReverseNumber.reverseExact(t);
            System.out.printf("  %13d %14d %16d %14s%n",
                    t, withNotEqualZero(t), ReverseNumber.reverseAsLong(t),
                    exact == null ? "OVERFLOW" : exact.toString());
        }

        System.out.println();
        System.out.println("  1534236469 reversed is 9646324351, which needs 34 bits.");
        System.out.println("  The int version returns 1056389759 — no exception, no");
        System.out.println("  warning, just a wrong answer that looks reasonable.");
        System.out.println();
        System.out.println("  Two fixes:");
        System.out.println("    1. accumulate in a long   -> always fits");
        System.out.println("    2. Math.multiplyExact/addExact -> throws instead of wrapping");
    }
}
