/**
 * Day 13, part 3 — four ways a for loop misbehaves.
 *
 * Each is capped or guarded so the file terminates; the comments say what the
 * uncapped version would do.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-26
 */
public class LoopTraps {

    public static void main(String[] args) {
        System.out.println("Day 13 — for loop traps");
        System.out.println();

        trap1_straySemicolon();
        trap2_offByOne();
        trap3_floatingPointCounter();
        trap4_overflowInCondition();
    }

    /**
     * TRAP 1 — a semicolon after the header makes the body empty, exactly as
     * with `if` (Day 10). The loop still runs; it just does nothing, and the
     * indented block below runs ONCE afterwards.
     *
     * WORSE THAN THE `if` VERSION: javac's [empty] lint covers `if` only.
     * Verified on 25.0.4 — -Xlint:all warns about `if (x);` and says nothing
     * at all about `for (...);` in the same file.
     */
    private static void trap1_straySemicolon() {
        int total = 0;

        System.out.println("── Trap 1: stray semicolon ──");
        System.out.println("  for (int i = 1; i <= 5; i++);");
        System.out.println("      total += i;      // runs once, not five times");
        System.out.println();

        for (int i = 1; i <= 5; i++);      // <-- empty body
        {
            total += 100;                  // a bare block, not the loop body
        }

        System.out.println("  total = " + total + "   (100, not 500 — the block ran once)");
        System.out.println();
        System.out.println("  And javac will NOT help here: the [empty] lint covers");
        System.out.println("  `if` only. -Xlint:all warns about `if (x);` but stays");
        System.out.println("  silent about `for (...);` — this trap is fully invisible.");
        System.out.println();
    }

    /** TRAP 2 — < versus <=. One character, one iteration. */
    private static void trap2_offByOne() {
        int lessThan = 0;
        int lessOrEqual = 0;

        for (int i = 1; i < 10; i++) {
            lessThan++;
        }
        for (int i = 1; i <= 10; i++) {
            lessOrEqual++;
        }

        System.out.println("── Trap 2: off by one ──");
        System.out.println("  i = 1; i <  10  ->  " + lessThan + " iterations (1..9)");
        System.out.println("  i = 1; i <= 10  ->  " + lessOrEqual + " iterations (1..10)");
        System.out.println();
        System.out.println("  Convention: start at 0 with <  (array indexing)");
        System.out.println("              start at 1 with <= (counting things)");
        System.out.println("  Mixing them is where off-by-one bugs come from.");
        System.out.println();
    }

    /**
     * TRAP 3 — never use a floating-point loop counter with an equality test.
     *
     * 0.1 has no exact binary representation (Day 4), so adding it ten times
     * does not produce 1.0. A `!=` condition therefore never becomes false and
     * the loop runs forever.
     */
    private static void trap3_floatingPointCounter() {
        System.out.println("── Trap 3: floating-point counter ──");
        System.out.println("  for (double d = 0; d != 1.0; d += 0.1)   // NEVER ENDS");
        System.out.println();

        double d = 0;
        for (int step = 1; step <= 11; step++) {
            d += 0.1;
            if (step >= 9) {
                System.out.printf("    step %2d: %.17f%s%n",
                        step, d, step == 10 ? "   <- should be exactly 1.0" : "");
            }
        }

        System.out.println();
        System.out.println("  At step 10 the value is 0.99999999999999990, so `d != 1.0`");
        System.out.println("  stays true and the loop steps straight past its target.");
        System.out.println();
        System.out.println("  Fix: count with an int and derive the double.");
        System.out.print("    ");
        for (int i = 0; i <= 10; i++) {
            System.out.printf("%.1f ", i / 10.0);
        }
        System.out.println();
        System.out.println();
    }

    /**
     * TRAP 4 — `i <= Integer.MAX_VALUE` can never be false. When i is at
     * MAX_VALUE, i++ overflows to Integer.MIN_VALUE (Day 4), which is still
     * <= MAX_VALUE. The loop wraps around forever.
     */
    private static void trap4_overflowInCondition() {
        System.out.println("── Trap 4: overflow in the condition ──");
        System.out.println("  for (int i = 0; i <= Integer.MAX_VALUE; i++)   // NEVER ENDS");
        System.out.println();

        int iterations = 0;
        for (int i = Integer.MAX_VALUE - 2; i <= Integer.MAX_VALUE; i++) {
            iterations++;
            if (iterations > 4) {
                System.out.println("    i has wrapped to " + i + "  (negative!)");
                break;                     // without this we would spin forever
            }
        }

        System.out.println("    started 2 below MAX_VALUE, still looping after "
                + iterations + " steps");
        System.out.println();
        System.out.println("  i++ at MAX_VALUE overflows to MIN_VALUE, which is");
        System.out.println("  still <= MAX_VALUE. The condition can never fail.");
        System.out.println("  Fix: use a long counter, or test i < MAX_VALUE.");
    }
}
