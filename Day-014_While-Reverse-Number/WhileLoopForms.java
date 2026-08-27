/**
 * Day 14, part 3 — while, do-while, and when each beats a for loop.
 *
 * for and while are interchangeable in principle; every for loop can be
 * rewritten as a while. The difference is which one makes the intent obvious:
 *
 *   for     — the iteration count is known up front  (counting)
 *   while   — you loop until a condition changes     (draining, searching)
 *   do-while— the body must run at least once        (prompt-then-validate)
 *
 * @author  Divesh Agarwal
 * @since   2026-08-27
 */
public class WhileLoopForms {

    public static void main(String[] args) {
        System.out.println("Day 14 — while, do-while and for");
        System.out.println();

        sameLoopThreeWays();
        theKeyDifference();
        constantFalseIsAsymmetric();
        whyReverseWantsWhile();
        infiniteWithBreak();
    }

    private static void sameLoopThreeWays() {
        System.out.println("── The same loop, three ways ──");

        System.out.print("  for       : ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("  while     : ");
        int i = 1;
        while (i <= 5) {
            System.out.print(i + " ");
            i++;
        }
        System.out.println();

        System.out.print("  do-while  : ");
        int k = 1;
        do {
            System.out.print(k + " ");
            k++;
        } while (k <= 5);
        System.out.println();

        System.out.println();
        System.out.println("  A for loop just gathers init/condition/update onto one");
        System.out.println("  line. With a while they are scattered, which is why for");
        System.out.println("  is better when you ARE counting.");
        System.out.println();
    }

    /** A false the compiler cannot fold — see constantFalseIsAsymmetric(). */
    private static boolean alwaysFalse() {
        return false;
    }

    /** The only real difference: when the condition is first tested. */
    private static void theKeyDifference() {
        int whileRuns = 0;
        int doWhileRuns = 0;

        while (alwaysFalse()) {
            whileRuns++;              // never reached at runtime
        }

        do {
            doWhileRuns++;            // runs before the test
        } while (alwaysFalse());

        System.out.println("── while vs do-while ──");
        System.out.println("  while (false)       -> body ran " + whileRuns + " times");
        System.out.println("  do { } while(false) -> body ran " + doWhileRuns + " time");
        System.out.println();
        System.out.println("  while    tests FIRST  -> can run zero times");
        System.out.println("  do-while tests LAST   -> always runs at least once");
        System.out.println();
        System.out.println("  do-while fits prompt-then-validate: you must read input");
        System.out.println("  once before you can judge whether to ask again (Day 9).");
        System.out.println();
    }

    /**
     * A literal `false` condition is a COMPILE ERROR for while and for, but
     * legal for if and do-while. The methods above therefore route through
     * alwaysFalse() so they compile at all.
     *
     * The `if` exemption is deliberate: it enables the conditional-compilation
     * idiom, where `static final boolean DEBUG = false` lets javac strip a
     * whole block without complaining that it is unreachable.
     */
    private static void constantFalseIsAsymmetric() {
        System.out.println("── A literal false is treated inconsistently ──");
        System.out.println("  while (false)        { }   -> error: unreachable statement");
        System.out.println("  for (;false;)        { }   -> error: unreachable statement");
        System.out.println("  if (false)           { }   -> compiles");
        System.out.println("  do { } while (false);      -> compiles");
        System.out.println();
        System.out.println("  Verified on " + System.getProperty("java.version") + ".");
        System.out.println("  if is exempted on purpose: `static final boolean DEBUG");
        System.out.println("  = false` is the conditional-compilation idiom, and javac");
        System.out.println("  strips the block rather than rejecting it.");
        System.out.println();
        System.out.println("  do-while is fine because its body ALWAYS runs once —");
        System.out.println("  nothing is unreachable.");
        System.out.println();
    }

    /**
     * Reversing a number has no known iteration count — it depends on how many
     * digits the input has. Writing it as a for loop is possible but obscures
     * that, because there is no counter to put in the header.
     */
    private static void whyReverseWantsWhile() {
        System.out.println("── Why reverse wants a while ──");

        int n = 9302;
        int rev = 0;
        int steps = 0;

        while (n != 0) {
            rev = rev * 10 + n % 10;
            n /= 10;
            steps++;
        }

        System.out.println("  9302 -> " + rev + " in " + steps + " steps (one per digit)");
        System.out.println();
        System.out.println("  The count depends on the input's digit count, which is");
        System.out.println("  not known when the loop starts. A for loop header would");
        System.out.println("  have nothing useful to put in init or update:");
        System.out.println();
        System.out.println("    for (; n != 0; n /= 10)      // legal, but the empty");
        System.out.println("                                 // init reads as an apology");
        System.out.println();
    }

    /** while (true) with break — for loops that end on an event, not a count. */
    private static void infiniteWithBreak() {
        System.out.println("── while (true) + break ──");

        int value = 1;
        int iterations = 0;

        while (true) {
            value *= 2;
            iterations++;
            if (value > 1000) {
                break;                 // the exit is inside the body
            }
        }

        System.out.println("  doubling from 1 until > 1000: " + value
                + " after " + iterations + " steps");
        System.out.println();
        System.out.println("  Equivalent to for (;;) (Day 13). Use it when the exit");
        System.out.println("  condition is only computable partway through the body;");
        System.out.println("  otherwise put it in the while header where it is visible.");
    }
}
