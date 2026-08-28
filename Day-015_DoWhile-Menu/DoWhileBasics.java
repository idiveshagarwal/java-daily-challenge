/**
 * Day 15, part 2 — do-while: syntax, guarantees, and why menus want it.
 *
 *     do {
 *         body
 *     } while (condition);      <- the semicolon is REQUIRED
 *
 * One property defines it: the body runs BEFORE the condition is ever tested,
 * so it always executes at least once.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-28
 */
public class DoWhileBasics {

    public static void main(String[] args) {
        System.out.println("Day 15 — do-while");
        System.out.println();

        atLeastOnce();
        theRequiredSemicolon();
        menuWithWhileVsDoWhile();
        sentinelControlled();
    }

    /** A false condition cannot stop the first iteration. */
    private static void atLeastOnce() {
        int whileRuns = 0;
        int doWhileRuns = 0;

        while (neverTrue()) {
            whileRuns++;
        }

        do {
            doWhileRuns++;
        } while (neverTrue());

        System.out.println("── The one guarantee ──");
        System.out.println("  while    -> body ran " + whileRuns + " times");
        System.out.println("  do-while -> body ran " + doWhileRuns + " time");
        System.out.println();
        System.out.println("  Test-first vs test-last. Everything else about the two");
        System.out.println("  loops is identical.");
        System.out.println();
    }

    /** Routed through a method: a literal `false` here is a compile error (Day 14). */
    private static boolean neverTrue() {
        return false;
    }

    private static void theRequiredSemicolon() {
        System.out.println("── The semicolon ──");
        System.out.println("  do { ... } while (cond);");
        System.out.println("                          ^ required");
        System.out.println();
        System.out.println("  Omitting it is a compile error, not a silent bug:");
        System.out.println("    error: ';' expected");
        System.out.println();
        System.out.println("  Unusual for Java — no other block construct ends in one.");
        System.out.println("  The reason: `while (cond)` could otherwise be read as the");
        System.out.println("  start of a NEW while loop, so the parser needs the marker.");
        System.out.println();
    }

    /**
     * The same menu written both ways. With `while` you must either duplicate
     * the prompt before the loop, or seed the control variable with a value
     * chosen purely to make the first test pass — both are worse than saying
     * "run this, then decide".
     */
    private static void menuWithWhileVsDoWhile() {
        System.out.println("── Menu: while vs do-while ──");
        System.out.println();
        System.out.println("  while — needs a primed variable:");
        System.out.println("    int choice = -1;          // a lie, just to enter the loop");
        System.out.println("    while (choice != 0) {");
        System.out.println("        showMenu();");
        System.out.println("        choice = read();");
        System.out.println("    }");
        System.out.println();
        System.out.println("  do-while — says what it means:");
        System.out.println("    int choice;               // no initial value needed");
        System.out.println("    do {");
        System.out.println("        showMenu();");
        System.out.println("        choice = read();");
        System.out.println("    } while (choice != 0);");
        System.out.println();
        System.out.println("  The second needs no sentinel value invented purely to");
        System.out.println("  satisfy a test that has not got any real data yet.");
        System.out.println();
    }

    /** A loop that ends on a value in the data, not on a count. */
    private static void sentinelControlled() {
        int[] readings = { 12, 7, 40, -1, 99 };     // -1 is the sentinel
        int index = 0;
        int sum = 0;
        int count = 0;
        int value;

        do {
            value = readings[index++];
            if (value != -1) {
                sum += value;
                count++;
            }
        } while (value != -1 && index < readings.length);

        System.out.println("── Sentinel-controlled loop ──");
        System.out.println("  data: 12, 7, 40, -1, 99   (-1 ends input)");
        System.out.println("  summed " + count + " values -> " + sum);
        System.out.println("  the 99 after the sentinel is never read");
        System.out.println();
        System.out.println("  Note the second guard, index < readings.length: without");
        System.out.println("  it, data with no sentinel runs off the end of the array.");
    }
}
