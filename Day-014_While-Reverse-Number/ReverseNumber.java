import java.util.Scanner;

/**
 * Day 14 — the while loop, via reversing a number.
 *
 * This is the natural while-loop exercise because the iteration count is not
 * known in advance: you loop until the number runs out of digits, not for a
 * fixed number of steps. That is exactly the case a for loop fits badly.
 *
 * Run:  printf '12345\n' | java ReverseNumber
 *       printf '-123\n'  | java ReverseNumber
 *
 * @author  Divesh Agarwal
 * @since   2026-08-27
 */
public class ReverseNumber {

    public static void main(String[] args) {
        System.out.println("Day 14 — Reverse a number (while loop)");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter an integer: ");
            int n = sc.nextInt();

            System.out.println();
            System.out.println("  input           : " + n);
            System.out.println("  reversed (int)  : " + describe(n));
            System.out.println("  reversed (long) : " + reverseAsLong(n));
            System.out.println();
            traceIt(n);
        }
    }

    /**
     * The algorithm, three lines of it.
     *
     *     rev = rev * 10 + n % 10;    // shift rev left, append last digit of n
     *     n /= 10;                    // drop that digit from n
     *
     * `% 10` takes the last digit; `/ 10` removes it. Integer division
     * truncates (Day 4), which is what makes the second line work.
     *
     * The condition is `n != 0`, NOT `n > 0` — see ReverseEdgeCases.
     *
     * @param n the number to reverse
     * @return the reversed value, which may have silently overflowed
     */
    public static int reverse(int n) {
        int rev = 0;

        while (n != 0) {
            rev = rev * 10 + n % 10;
            n /= 10;
        }

        return rev;
    }

    /**
     * The same algorithm with overflow detection. Math.multiplyExact and
     * Math.addExact throw rather than wrapping, turning a silent wrong answer
     * into a loud one.
     *
     * @return the reversed value, or null if it will not fit in an int
     */
    public static Integer reverseExact(int n) {
        int rev = 0;

        while (n != 0) {
            try {
                rev = Math.addExact(Math.multiplyExact(rev, 10), n % 10);
            } catch (ArithmeticException overflow) {
                return null;
            }
            n /= 10;
        }

        return rev;
    }

    /** Widening to long removes the problem: a reversed int always fits. */
    public static long reverseAsLong(int n) {
        long rev = 0;
        long value = n;

        while (value != 0) {
            rev = rev * 10 + value % 10;
            value /= 10;
        }

        return rev;
    }

    private static String describe(int n) {
        Integer safe = reverseExact(n);
        return safe == null
                ? reverse(n) + "   <- OVERFLOWED, this value is wrong"
                : String.valueOf(safe);
    }

    /** Shows the two variables at each step. */
    private static void traceIt(int n) {
        System.out.println("── Step by step ──");
        System.out.printf("  %12s %12s %10s%n", "n", "n % 10", "rev");

        int rev = 0;
        while (n != 0) {
            int digit = n % 10;
            rev = rev * 10 + digit;
            System.out.printf("  %12d %12d %10d%n", n, digit, rev);
            n /= 10;
        }

        System.out.printf("  %12d %12s %10d   <- n hit 0, loop ends%n", n, "-", rev);
    }
}
