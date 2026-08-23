/**
 * Day 10, part 2 — the else-if ladder.
 *
 * There is no `elseif` keyword in Java. An "else-if ladder" is just an `if`
 * nested in the `else` of the previous one; the flat formatting is a
 * convention, not a language construct.
 *
 * The one rule that matters: THE FIRST MATCHING BRANCH WINS, and the rest are
 * never tested. That makes the ORDER of the conditions part of the logic.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-23
 */
public class ElseIfLadder {

    public static void main(String[] args) {
        System.out.println("Day 10 — else-if ladder");
        System.out.println();

        firstMatchWins();
        orderIsPartOfTheLogic();
        theFinalElseIsYourSafetyNet();
        whenToUseSwitchInstead();
    }

    /**
     * Because the first match wins, later conditions do not need to re-exclude
     * earlier ranges. `score >= 80` already implies `score < 90` by the time it
     * is reached.
     */
    private static String gradeFor(int score) {
        if (score >= 90) {
            return "A";
        } else if (score >= 80) {     // no need to write "&& score < 90"
            return "B";
        } else if (score >= 70) {
            return "C";
        } else if (score >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    private static void firstMatchWins() {
        System.out.println("── First match wins ──");
        for (int score : new int[] { 95, 85, 73, 61, 40 }) {
            System.out.printf("  score %3d -> %s%n", score, gradeFor(score));
        }
        System.out.println();
        System.out.println("Once a branch matches, the remaining tests are SKIPPED.");
        System.out.println("So `score >= 80` needs no `&& score < 90` — reaching it");
        System.out.println("already proves score < 90.");
        System.out.println();
    }

    /** The same conditions in the wrong order collapse to a single answer. */
    private static String brokenGrade(int score) {
        if (score >= 60) {
            return "D";               // catches EVERYTHING from 60 up
        } else if (score >= 70) {
            return "C";               // unreachable in practice
        } else if (score >= 80) {
            return "B";               // unreachable in practice
        } else if (score >= 90) {
            return "A";               // unreachable in practice
        }
        return "F";
    }

    private static void orderIsPartOfTheLogic() {
        System.out.println("── Order is part of the logic ──");
        System.out.println("Same four conditions, loosest first:");
        for (int score : new int[] { 95, 85, 73, 61 }) {
            System.out.printf("  score %3d -> %s   (correct: %s)%n",
                    score, brokenGrade(score), gradeFor(score));
        }
        System.out.println();
        System.out.println("Everything >= 60 hits the first branch, so the later");
        System.out.println("ones never run. javac does NOT flag this — the branches");
        System.out.println("are reachable in principle, just never in practice.");
        System.out.println();
        System.out.println("Rule: order overlapping ranges from most specific to least.");
        System.out.println();
    }

    /**
     * A ladder without a final `else` silently does nothing when no branch
     * matches. For a method that returns a value javac forces the issue; for a
     * void ladder it does not.
     */
    private static void theFinalElseIsYourSafetyNet() {
        int unexpected = -1;
        String status;

        if (unexpected == 0) {
            status = "zero";
        } else if (unexpected == 1) {
            status = "one";
        } else {
            status = "unhandled: " + unexpected;    // catches everything else
        }

        System.out.println("── Always end with else ──");
        System.out.println("  input " + unexpected + " -> " + status);
        System.out.println("Without the final else, `status` would be unassigned and");
        System.out.println("javac would reject the read — one case where the compiler");
        System.out.println("catches the omission for you.");
        System.out.println();
    }

    private static void whenToUseSwitchInstead() {
        System.out.println("── Ladder or switch? ──");
        System.out.println("Ladder  : RANGES and arbitrary boolean tests");
        System.out.println("          (score >= 90, a > b && c, x.isEmpty())");
        System.out.println("switch  : one variable against DISCRETE constant values");
        System.out.println("          (day == MONDAY, code == 404)");
        System.out.println();
        System.out.println("The grading example must be a ladder — you cannot switch");
        System.out.println("on a range. Ch 5 continues into switch next.");
    }
}
