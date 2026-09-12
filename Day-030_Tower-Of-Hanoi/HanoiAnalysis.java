import java.util.HashSet;
import java.util.Set;

/**
 * Day 30, part 2 — Hanoi's cost, and the recursion chapter's closing question.
 *
 * Day 29 showed naive Fibonacci making 2^n calls to produce one number, and
 * memoisation collapsing that to linear. Hanoi also costs 2^n. So the obvious
 * question is: can the same fix apply?
 *
 * The answer is no, and the REASON is the most useful idea in Ch 11.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-12
 */
public class HanoiAnalysis {

    private static long calls;
    private static final Set<String> distinctStates = new HashSet<>();

    private static long fibCalls;
    private static final Set<Integer> fibStates = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Day 30 - Hanoi analysis");
        System.out.println();

        depthIsLinearMovesAreNot();
        doSubproblemsRepeat();
        whyMemoisationCannotHelp();
        countingIsCheapListingIsNot();
        chapterWrapUp();
    }

    static void hanoi(int n, int from, int to, int via) {
        if (n == 0) {
            return;
        }
        calls++;
        distinctStates.add(n + ":" + from + ":" + to);
        hanoi(n - 1, from, via, to);
        hanoi(n - 1, via, to, from);
    }

    static long fib(int n) {
        fibCalls++;
        fibStates.add(n);
        return n < 2 ? n : fib(n - 1) + fib(n - 2);
    }

    /**
     * A crucial distinction that Day 29's factorial hid: recursion DEPTH and
     * recursion WORK are different quantities.
     */
    private static void depthIsLinearMovesAreNot() {
        System.out.println("-- Depth is linear; the work is not --");
        System.out.printf("  %6s %16s %14s%n", "n", "moves (2^n - 1)", "max depth");
        for (int n : new int[] { 5, 10, 20, 30 }) {
            System.out.printf("  %6d %16d %14d%n", n, (1L << n) - 1, n);
        }
        System.out.println();
        System.out.println("  30 disks is a billion moves but only 30 stack frames.");
        System.out.println("  Hanoi will never throw StackOverflowError for any n you");
        System.out.println("  could wait for — it will simply never finish.");
        System.out.println();
        System.out.println("  Naive Fibonacci is the same shape: depth n, work 2^n.");
        System.out.println("  Depth bounds the MEMORY; the call count bounds the TIME.");
        System.out.println();
    }

    /**
     * The measurement that overturns the easy answer. It is tempting to say
     * "Hanoi has no repeated subproblems, so memoisation is inapplicable".
     * That is simply false — Hanoi repeats MORE than Fibonacci does.
     */
    private static void doSubproblemsRepeat() {
        System.out.println("-- Do subproblems repeat? --");
        System.out.printf("  %-8s %6s %14s %16s %12s%n",
                "problem", "n", "total calls", "distinct states", "repetition");

        for (int n : new int[] { 10, 15, 20 }) {
            calls = 0;
            distinctStates.clear();
            hanoi(n, 0, 2, 1);
            System.out.printf("  %-8s %6d %14d %16d %11.0fx%n",
                    "hanoi", n, calls, distinctStates.size(),
                    (double) calls / distinctStates.size());
        }
        for (int n : new int[] { 10, 15, 20 }) {
            fibCalls = 0;
            fibStates.clear();
            fib(n);
            System.out.printf("  %-8s %6d %14d %16d %11.0fx%n",
                    "fib", n, fibCalls, fibStates.size(),
                    (double) fibCalls / fibStates.size());
        }

        System.out.println();
        System.out.println("  Hanoi at n=20: 1,048,575 calls over just 57 distinct");
        System.out.println("  (n, from, to) states — about 18,000x repetition, an order");
        System.out.println("  of magnitude MORE than Fibonacci's.");
        System.out.println();
        System.out.println("  So \"no repeated subproblems\" is the wrong explanation.");
        System.out.println("  The repetition is there. Caching still cannot help.");
        System.out.println();
    }

    /**
     * The real reason, and the point of the whole chapter.
     *
     * Memoisation removes work that is WASTED. It cannot remove work that IS
     * the answer.
     */
    private static void whyMemoisationCannotHelp() {
        System.out.println("-- Why caching cannot help --");
        System.out.println();
        System.out.printf("  %-12s %-22s %-18s %s%n",
                "", "what it returns", "size of answer", "2^n work is...");
        System.out.printf("  %-12s %-22s %-18s %s%n",
                "fib(n)", "one number", "O(1)", "WASTE - removable");
        System.out.printf("  %-12s %-22s %-18s %s%n",
                "hanoi(n)", "a sequence of moves", "2^n - 1 moves", "the ANSWER - not removable");
        System.out.println();
        System.out.println("  fib(40) does 331 million calls to produce ONE integer.");
        System.out.println("  Almost all of it is recomputation, so a cache deletes it.");
        System.out.println();
        System.out.println("  hanoi(20) does a million calls to produce A MILLION MOVES.");
        System.out.println("  You could cache \"the move list for hanoi(19, A->C)\" — but");
        System.out.println("  that list has 524,287 entries in it. Storing it does not");
        System.out.println("  make emitting it cheaper.");
        System.out.println();
        System.out.println("  The output size is a LOWER BOUND on the running time. No");
        System.out.println("  algorithm can print 2^n - 1 moves in less than 2^n - 1");
        System.out.println("  steps, however cleverly it is written.");
        System.out.println();
    }

    /** But if you only want the COUNT, the exponential vanishes entirely. */
    private static void countingIsCheapListingIsNot() {
        System.out.println("-- Counting vs listing --");
        System.out.println("  How many moves does hanoi(60) need?");
        System.out.println("    (1L << 60) - 1 = " + (((1L) << 60) - 1));
        System.out.println("    computed in O(1) — one shift and one subtraction.");
        System.out.println();
        System.out.println("  What ARE those moves?");
        System.out.println("    1,152,921,504,606,846,975 of them. Not happening.");
        System.out.println();
        System.out.println("  Same puzzle, same n. Asking for a summary is trivial;");
        System.out.println("  asking for the sequence is impossible. The exponential");
        System.out.println("  lives in the QUESTION, not in the algorithm.");
        System.out.println();
    }

    private static void chapterWrapUp() {
        System.out.println("-- Ch 11 wrap-up: when is recursion right? --");
        System.out.println();
        System.out.printf("  %-14s %-16s %s%n", "problem", "recursion is", "because");
        System.out.printf("  %-14s %-16s %s%n", "factorial", "unnecessary", "one call per level = a loop (Day 29)");
        System.out.printf("  %-14s %-16s %s%n", "fibonacci", "a trap", "two calls, overlapping - memoise or iterate");
        System.out.printf("  %-14s %-16s %s%n", "hanoi", "the right tool", "two calls, genuinely branching");
        System.out.println();
        System.out.println("  The test is not \"can this be recursive\" but \"does the");
        System.out.println("  problem BRANCH\". A countdown does not; a tree does.");
        System.out.println();
        System.out.println("  Three things to carry out of the chapter:");
        System.out.println("    1. base case + progress toward it, or StackOverflowError");
        System.out.println("    2. depth bounds memory, call count bounds time — they");
        System.out.println("       are different, and Hanoi separates them cleanly");
        System.out.println("    3. memoisation removes repeated WORK, never a large");
        System.out.println("       ANSWER. Ask which one your exponential is.");
    }
}
