/**
 * Day 16, part 2 — what break and continue actually jump to.
 *
 *   break     leaves the loop entirely; execution resumes after it
 *   continue  abandons THIS iteration and moves to the next
 *
 * The second is where the subtlety is: "the next iteration" means different
 * things in a for loop and a while loop, and getting that wrong produces an
 * infinite loop.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-29
 */
public class BreakAndContinue {

    public static void main(String[] args) {
        System.out.println("Day 16 — break and continue");
        System.out.println();

        breakLeavesTheLoop();
        continueSkipsOneIteration();
        theWhileContinueTrap();
        breakOnlyLeavesOneLevel();
        labelledJumps();
        breakInSwitchIsDifferent();
    }

    private static void breakLeavesTheLoop() {
        System.out.println("── break ──");
        System.out.print("  ");

        for (int i = 1; i <= 10; i++) {
            if (i > 5) {
                break;
            }
            System.out.print(i + " ");
        }

        System.out.println("  <- stopped at 5, the loop is over");
        System.out.println();
    }

    private static void continueSkipsOneIteration() {
        System.out.println("── continue ──");
        System.out.print("  ");

        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;               // skip evens
            }
            System.out.print(i + " ");
        }

        System.out.println("  <- evens skipped, the loop still finished");
        System.out.println();
    }

    /**
     * THE TRAP. In a for loop, `continue` jumps to the UPDATE, so i++ still
     * runs. In a while loop it jumps straight back to the CONDITION — and any
     * increment sitting at the bottom of the body is skipped.
     *
     * The loop below is capped; uncapped it never terminates.
     */
    private static void theWhileContinueTrap() {
        System.out.println("── continue in a while loop ──");
        System.out.println("  for   : continue -> jumps to the UPDATE  (i++ runs)");
        System.out.println("  while : continue -> jumps to the CONDITION (i++ skipped)");
        System.out.println();

        int i = 1;
        int spins = 0;

        while (i <= 5) {
            spins++;
            if (spins > 1000) {
                System.out.println("  aborted after 1000 spins, i still stuck at " + i);
                break;
            }
            if (i % 2 == 0) {
                continue;               // i++ below is never reached
            }
            i++;
        }

        System.out.println();
        System.out.println("  i advanced 1 -> 2, then stuck: every later pass hits");
        System.out.println("  the continue before reaching i++.");
        System.out.println("  Fix: increment BEFORE the continue, or use a for loop");
        System.out.println("  where the update cannot be skipped.");
        System.out.println();
    }

    /** Both keywords act on the innermost enclosing loop only. */
    private static void breakOnlyLeavesOneLevel() {
        System.out.println("── break leaves ONE level ──");

        int bodyRuns = 0;
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (col == 2) {
                    break;              // leaves the INNER loop only
                }
                bodyRuns++;
            }
        }

        System.out.println("  inner body ran " + bodyRuns + " times (3 rows x 1 col)");
        System.out.println("  The outer loop completed all 3 rows — break did not");
        System.out.println("  touch it.");
        System.out.println();
    }

    /**
     * A label lets break or continue target an OUTER loop. This is the only
     * place Java has anything resembling goto, and it is restricted to loops.
     */
    private static void labelledJumps() {
        System.out.println("── Labelled break and continue ──");

        int[][] grid = { { 1, 2, 3 }, { 4, -1, 6 }, { 7, 8, 9 } };

        int found = 0;
        search:
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] < 0) {
                    found = grid[row][col];
                    break search;       // leaves BOTH loops
                }
            }
        }
        System.out.println("  labelled break: found " + found + ", exited both loops at once");

        StringBuilder kept = new StringBuilder();
        rows:
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] < 0) {
                    continue rows;      // abandon this ROW, start the next
                }
            }
            kept.append(row).append(" ");
        }
        System.out.println("  labelled continue: rows with no negative = " + kept.toString().trim());

        System.out.println();
        System.out.println("  Without a label you would need a flag checked by the");
        System.out.println("  outer condition — more code, and easy to get wrong.");
        System.out.println();
    }

    /**
     * `break` means something different inside a switch: it ends the switch,
     * not the enclosing loop (Day 12). A switch inside a loop therefore cannot
     * be exited with a plain break — the loop keeps going.
     */
    private static void breakInSwitchIsDifferent() {
        System.out.println("── break inside switch ──");

        int loops = 0;
        for (int i = 1; i <= 3; i++) {
            loops++;
            switch (i) {
                case 2:
                    break;              // ends the SWITCH, not the for loop
                default:
                    break;
            }
        }

        System.out.println("  loop ran " + loops + " times despite the break at i=2");
        System.out.println("  Inside a switch, break ends the switch. To leave the");
        System.out.println("  loop as well you need a label, or the arrow form which");
        System.out.println("  needs no break at all.");
    }
}
