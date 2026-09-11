import java.util.HashMap;
import java.util.Map;

/**
 * Day 29, part 2 — Fibonacci, and why the obvious recursion is a disaster.
 *
 * Factorial recurses once per call, so it costs O(n). Fibonacci recurses
 * TWICE, and that single difference turns linear work into exponential.
 *
 *     fib(n) = fib(n-1) + fib(n-2)
 *
 * The two branches overlap enormously — fib(n-2) is recomputed inside
 * fib(n-1), and so on, all the way down. Nothing remembers anything.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-11
 */
public class Fibonacci {

    /** Counts every invocation, so the cost can be measured rather than claimed. */
    private static long calls;

    public static void main(String[] args) {
        System.out.println("Day 29 - Fibonacci by recursion");
        System.out.println();

        theNaiveVersion();
        theCallCountHasAFormula();
        whereTheWorkGoes();
        memoisation();
        iterative();
        theComparison();
    }

    /** The textbook version. Correct, and unusable past about n = 40. */
    static long fib(int n) {
        calls++;
        if (n < 2) {
            return n;                              // fib(0)=0, fib(1)=1
        }
        return fib(n - 1) + fib(n - 2);            // TWO recursive calls
    }

    /** Same recursion, but each n is computed once and remembered. */
    static long fibMemo(int n, Map<Integer, Long> memo) {
        calls++;
        if (n < 2) {
            return n;
        }
        Long cached = memo.get(n);
        if (cached != null) {
            return cached;                         // already known
        }
        long value = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
        memo.put(n, value);
        return value;
    }

    /** No recursion, no memory: carry the last two values forward. */
    static long fibIterative(int n) {
        long previous = 0;
        long current = 1;
        for (int i = 0; i < n; i++) {
            long next = previous + current;
            previous = current;
            current = next;
        }
        return previous;
    }

    private static void theNaiveVersion() {
        System.out.println("-- The naive recursion --");
        System.out.printf("  %4s %14s %16s %10s%n", "n", "fib(n)", "calls", "time");

        for (int n : new int[] { 10, 20, 30, 35, 40 }) {
            calls = 0;
            long start = System.nanoTime();
            long value = fib(n);
            long millis = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("  %4d %14d %16d %8d ms%n", n, value, calls, millis);
        }

        System.out.println();
        System.out.println("  Each +5 on n multiplies the work by about 11. That is");
        System.out.println("  exponential growth, not slow constants — fib(50) would");
        System.out.println("  take roughly a minute, fib(60) about two hours.");
        System.out.println();
    }

    /**
     * The call count is not merely "exponential" — it is exactly
     * 2*fib(n+1) - 1, which is checkable.
     */
    private static void theCallCountHasAFormula() {
        int mismatches = 0;
        for (int n = 0; n <= 30; n++) {
            calls = 0;
            fib(n);
            long predicted = 2 * fibIterative(n + 1) - 1;
            if (calls != predicted) {
                mismatches++;
                System.out.println("    MISMATCH at n=" + n);
            }
        }

        System.out.println("-- The call count has a closed form --");
        System.out.println("  calls(n) == 2 * fib(n+1) - 1");
        System.out.println("  checked n = 0..30, mismatches: " + mismatches);
        System.out.println();
        System.out.println("  So computing fib(n) costs about 2*fib(n) calls. The");
        System.out.println("  function is its own cost model — which is a neat way to");
        System.out.println("  see why it explodes: fib grows exponentially, so the");
        System.out.println("  work to find it does too.");
        System.out.println();
    }

    /** Showing the duplicated subtrees makes the waste obvious. */
    private static void whereTheWorkGoes() {
        System.out.println("-- Where the work goes --");
        System.out.println("                       fib(5)");
        System.out.println("                  /              \\");
        System.out.println("              fib(4)            fib(3)      <- recomputed");
        System.out.println("            /       \\          /      \\");
        System.out.println("        fib(3)    fib(2)   fib(2)   fib(1)");
        System.out.println("        /    \\");
        System.out.println("    fib(2)  fib(1)");
        System.out.println();
        System.out.println("  fib(3) is computed twice, fib(2) three times, and it");
        System.out.println("  compounds downward. Nothing is remembered between");
        System.out.println("  branches, so the same subtree is rebuilt over and over.");
        System.out.println();
        System.out.println("  There are only n distinct subproblems. The naive version");
        System.out.println("  solves them exponentially many times.");
        System.out.println();
    }

    /** One cache turns the exponential tree into a linear walk. */
    private static void memoisation() {
        System.out.println("-- Memoisation --");
        System.out.printf("  %4s %22s %14s %10s%n", "n", "fib(n)", "calls", "time");

        for (int n : new int[] { 40, 90 }) {
            calls = 0;
            long start = System.nanoTime();
            long value = fibMemo(n, new HashMap<>());
            long micros = (System.nanoTime() - start) / 1_000;
            System.out.printf("  %4d %22d %14d %8d us%n", n, value, calls, micros);
        }

        System.out.println();
        System.out.println("  fib(40): 331,160,281 calls naive -> 79 memoised.");
        System.out.println("  Same recursion, same base case, one HashMap.");
        System.out.println();
        System.out.println("  Each n is computed once and reused, so the call count");
        System.out.println("  becomes linear: about 2n, one hit and one miss per level.");
        System.out.println();
        System.out.println("  This is dynamic programming in its smallest form —");
        System.out.println("  recursion plus a memory of what you already solved.");
        System.out.println();
    }

    private static void iterative() {
        System.out.println("-- Iterative --");
        System.out.println("  long previous = 0, current = 1;");
        System.out.println("  for (int i = 0; i < n; i++) { ... }");
        System.out.println();
        System.out.println("  fib(90) = " + fibIterative(90));
        System.out.println();
        System.out.println("  O(n) time, O(1) space, no stack risk, no cache. For a");
        System.out.println("  linear recurrence this is simply the right answer.");
        System.out.println();
        System.out.println("  Note fib(92) is the last one that fits in a long:");
        System.out.println("    fib(92) = " + fibIterative(92));
        System.out.println("    fib(93) = " + fibIterative(93) + "   <- overflowed (Day 4)");
        System.out.println();
    }

    private static void theComparison() {
        System.out.println("-- Comparison --");
        System.out.printf("  %-14s %-16s %-14s %s%n", "approach", "time", "space", "usable to");
        System.out.printf("  %-14s %-16s %-14s %s%n", "naive", "O(2^n)", "O(n) stack", "n ~ 40");
        System.out.printf("  %-14s %-16s %-14s %s%n", "memoised", "O(n)", "O(n) + stack", "n ~ 92");
        System.out.printf("  %-14s %-16s %-14s %s%n", "iterative", "O(n)", "O(1)", "n ~ 92");
        System.out.println();
        System.out.println("  All three stop at 92 for the same reason — long overflow,");
        System.out.println("  not algorithmic cost. Past that you need BigInteger.");
        System.out.println();
        System.out.println("  The lesson is not \"recursion is slow\". Recursion is fine;");
        System.out.println("  RECOMPUTING is slow. The naive version's flaw is that it");
        System.out.println("  forgets, and memoisation fixes it without changing the");
        System.out.println("  shape of the code at all.");
    }
}
