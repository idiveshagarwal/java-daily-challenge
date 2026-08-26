/**
 * Day 13, part 2 — what the three parts of a for loop actually do.
 *
 *     for (init ; condition ; update) body
 *      │      │        │         │
 *      │      │        │         └─ runs AFTER each body execution
 *      │      │        └─ tested BEFORE each body execution
 *      │      └─ runs ONCE, before anything else
 *      └─ all three parts are optional
 *
 * Execution order: init, then (condition, body, update) repeatedly. The
 * condition is checked first, so a body can run zero times.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-26
 */
public class ForLoopAnatomy {

    public static void main(String[] args) {
        System.out.println("Day 13 — Anatomy of a for loop");
        System.out.println();

        executionOrder();
        zeroIterations();
        allPartsAreOptional();
        multipleVariables();
        counterScope();
        countingDown();
    }

    /** Prints the order the three parts actually fire in. */
    private static void executionOrder() {
        System.out.println("── Execution order ──");

        for (int i = init(); condition(i); i = update(i)) {
            System.out.println("    body  i=" + i);
        }

        System.out.println("  init runs once; then condition, body, update repeat.");
        System.out.println();
    }

    private static int init() {
        System.out.println("    init");
        return 1;
    }

    private static boolean condition(int i) {
        System.out.println("  condition i=" + i + " -> " + (i <= 3));
        return i <= 3;
    }

    private static int update(int i) {
        System.out.println("    update " + i + " -> " + (i + 1));
        return i + 1;
    }

    /** The condition is tested first, so the body can run zero times. */
    private static void zeroIterations() {
        int runs = 0;

        for (int i = 5; i <= 3; i++) {
            runs++;
        }

        System.out.println("── Zero iterations ──");
        System.out.println("  for (int i = 5; i <= 3; i++)  ran " + runs + " times");
        System.out.println("  The condition is checked BEFORE the first body run,");
        System.out.println("  which is what separates for/while from do-while.");
        System.out.println();
    }

    /** Any of the three parts may be empty. */
    private static void allPartsAreOptional() {
        System.out.println("── All three parts are optional ──");

        int i = 0;
        for (; i < 3; ) {          // no init, no update
            i++;
        }
        System.out.println("  for (; i < 3; )        -> i ended at " + i);

        int guard = 0;
        for (;;) {                 // no parts at all: infinite
            guard++;
            if (guard == 3) {
                break;             // the only way out
            }
        }
        System.out.println("  for (;;)               -> ran " + guard + " times, exited via break");
        System.out.println("  An empty condition is treated as TRUE.");
        System.out.println();
    }

    /** Comma-separated init and update — but only ONE condition. */
    private static void multipleVariables() {
        System.out.println("── Multiple loop variables ──");

        for (int lo = 0, hi = 10; lo < hi; lo++, hi--) {
            System.out.println("  lo=" + lo + " hi=" + hi);
        }

        System.out.println("  Commas allowed in init and update; the condition is");
        System.out.println("  a single boolean expression (use && to combine).");
        System.out.println();
    }

    /** A counter declared in init does not exist after the loop. */
    private static void counterScope() {
        for (int i = 0; i < 3; i++) {
            // i lives only here
        }
        // System.out.println(i);   // error: cannot find symbol

        int j;
        for (j = 0; j < 3; j++) {
            // declared outside, so it survives
        }

        System.out.println("── Counter scope ──");
        System.out.println("  declared in init  -> gone after the loop");
        System.out.println("  declared outside  -> j = " + j + " (one past the last value)");
        System.out.println("  Declare it inside unless you need the final value.");
        System.out.println();
    }

    private static void countingDown() {
        System.out.println("── Counting down ──");
        System.out.print("  ");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("  The update must move TOWARD the condition failing,");
        System.out.println("  or the loop never ends.");
    }
}
