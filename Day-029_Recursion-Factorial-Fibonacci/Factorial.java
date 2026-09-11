import java.math.BigInteger;

/**
 * Day 29 — recursion, starting with factorial.
 *
 * A recursive method is two things:
 *
 *   BASE CASE       an input it can answer without recursing
 *   RECURSIVE CASE  a step that moves strictly TOWARD the base case
 *
 * Miss the base case, or fail to move toward it, and you get
 * StackOverflowError rather than an infinite loop — recursion's version of
 * Day 13's runaway for loop.
 *
 * Factorial is the cleanest example: one base case, one step, one call.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-11
 */
public class Factorial {

    public static void main(String[] args) {
        System.out.println("Day 29 - Factorial by recursion");
        System.out.println();

        theShape();
        traceTheCalls();
        overflowArrivesEarly();
        iterativeEquivalent();
        stackDepthIsFinite();
        tailCallsAreNotOptimised();
    }

    /** The canonical form. n! = n * (n-1)!, with 0! = 1! = 1. */
    static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("factorial undefined for " + n);
        }
        if (n <= 1) {
            return 1;                          // BASE CASE
        }
        return n * factorial(n - 1);           // RECURSIVE CASE, n decreases
    }

    /** Same recursion returning int, to show where the narrower type gives up. */
    static int factorialInt(int n) {
        return n <= 1 ? 1 : n * factorialInt(n - 1);
    }

    /** Exact, at the cost of allocation — no overflow at any n. */
    static BigInteger factorialExact(int n) {
        return n <= 1 ? BigInteger.ONE
                      : BigInteger.valueOf(n).multiply(factorialExact(n - 1));
    }

    /** The same computation as a loop. */
    static long factorialIterative(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private static void theShape() {
        System.out.println("-- The shape --");
        System.out.println("  if (n <= 1) return 1;            BASE CASE");
        System.out.println("  return n * factorial(n - 1);     RECURSIVE CASE");
        System.out.println();
        System.out.println("  Two obligations, and both matter:");
        System.out.println("    1. a base case exists");
        System.out.println("    2. every recursive call moves TOWARD it");
        System.out.println();
        System.out.println("  factorial(n + 1) would satisfy the first and violate the");
        System.out.println("  second — it compiles, and it dies with StackOverflowError.");
        System.out.println();
    }

    /** Showing the unwinding makes the stack concrete. */
    private static void traceTheCalls() {
        System.out.println("-- Unwinding factorial(5) --");
        System.out.println("    factorial(5) = 5 * factorial(4)");
        System.out.println("                     factorial(4) = 4 * factorial(3)");
        System.out.println("                                      factorial(3) = 3 * factorial(2)");
        System.out.println("                                                       factorial(2) = 2 * factorial(1)");
        System.out.println("                                                                        factorial(1) = 1   <- base");
        System.out.println();
        System.out.println("  then it multiplies back up: 1, 2, 6, 24, 120");
        System.out.println("  factorial(5) = " + factorial(5));
        System.out.println();
        System.out.println("  Nothing is computed on the way DOWN. Five frames sit on");
        System.out.println("  the stack waiting, and the work happens on the way back.");
        System.out.println();
    }

    /**
     * Factorial grows so fast that the interesting limit is not the recursion
     * — it is the integer type. Verified exact crossover points.
     */
    private static void overflowArrivesEarly() {
        System.out.println("-- Overflow arrives long before the stack does --");
        System.out.printf("  %4s %14s %22s %26s%n", "n", "int result", "long result", "exact");

        for (int n : new int[] { 12, 13, 20, 21 }) {
            BigInteger exact = factorialExact(n);
            boolean fitsInt = exact.bitLength() <= 31;
            boolean fitsLong = exact.bitLength() <= 63;
            System.out.printf("  %4d %14d%s %22d%s %26s%n",
                    n,
                    factorialInt(n), fitsInt ? " " : "*",
                    factorial(n), fitsLong ? " " : "*",
                    exact);
        }
        System.out.println("                    * = wrong, silently");

        System.out.println();
        System.out.println("  int  holds up to 12!   13! overflows silently");
        System.out.println("  long holds up to 20!   21! overflows silently");
        System.out.println();
        System.out.println("  Both wrap without warning (Day 4). 21! even comes back");
        System.out.println("  NEGATIVE. The recursion is perfectly correct; the return");
        System.out.println("  type is what fails.");
        System.out.println();
        System.out.println("  BigInteger has no such limit:");
        System.out.println("    30! = " + factorialExact(30));
        System.out.println();
    }

    private static void iterativeEquivalent() {
        System.out.println("-- Recursive vs iterative --");
        System.out.println("  factorial(20)          = " + factorial(20));
        System.out.println("  factorialIterative(20) = " + factorialIterative(20));
        System.out.println();
        System.out.printf("  %-14s %-12s %-14s %s%n", "", "time", "extra space", "risk");
        System.out.printf("  %-14s %-12s %-14s %s%n", "recursive", "O(n)", "O(n) frames", "StackOverflowError");
        System.out.printf("  %-14s %-12s %-14s %s%n", "iterative", "O(n)", "O(1)", "none");
        System.out.println();
        System.out.println("  For factorial the loop is strictly better. Recursion earns");
        System.out.println("  its keep when the problem is genuinely branching — trees,");
        System.out.println("  parsing, backtracking — not when it is a countdown.");
        System.out.println();
    }

    /** The stack is finite, and much smaller than people expect. */
    private static void stackDepthIsFinite() {
        int depth = measureDepth();

        System.out.println("-- The stack is finite --");
        System.out.println("  plain recursion overflowed at depth ~" + depth);
        System.out.println();
        System.out.println("  So factorial(50000) dies even though the ARITHMETIC is");
        System.out.println("  fine with BigInteger. This number MOVES between runs —");
        System.out.println("  it depends on the JVM, the frame size and -Xss — so read");
        System.out.println("  it as an order of magnitude: tens of thousands, not");
        System.out.println("  millions.");
        System.out.println();
    }

    private static int counter;

    private static int measureDepth() {
        counter = 0;
        try {
            descend();
        } catch (StackOverflowError e) {
            return counter;
        }
        return counter;
    }

    private static void descend() {
        counter++;
        descend();
    }

    /**
     * A tail call is one where the recursive call is the LAST thing the method
     * does — nothing is pending when it returns. Many languages compile that
     * into a jump, reusing the frame. Java does not.
     */
    static long factorialTail(int n, long accumulator) {
        tailDepth++;
        if (n <= 1) {
            return accumulator;
        }
        return factorialTail(n - 1, n * accumulator);    // tail position
    }

    private static int tailDepth;

    private static void tailCallsAreNotOptimised() {
        tailDepth = 0;
        int reached = 0;
        try {
            factorialTail(1_000_000, 1);
        } catch (StackOverflowError e) {
            reached = tailDepth;
        }

        System.out.println("-- Java does not optimise tail calls --");
        System.out.println("  factorialTail puts the recursive call in TAIL POSITION,");
        System.out.println("  so nothing is pending when it returns:");
        System.out.println("    return factorialTail(n - 1, n * accumulator);");
        System.out.println();
        System.out.println("  In Scheme or Scala that compiles to a jump and runs in");
        System.out.println("  constant stack. In Java it still pushes a frame:");
        System.out.println("    overflowed at depth ~" + reached);
        System.out.println();
        System.out.println("  Note it dies EARLIER than the plain version above — the");
        System.out.println("  frame is bigger, holding two parameters instead of one.");
        System.out.println("  Tail position changes nothing; only frame size moved.");
        System.out.println();
        System.out.println("  Practical consequence: rewriting a recursion into tail");
        System.out.println("  form buys you nothing in Java. If depth is the problem,");
        System.out.println("  convert it to a loop.");
    }
}
