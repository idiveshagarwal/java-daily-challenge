import java.util.Arrays;

/**
 * Day 26, part 2 — how arguments are passed.
 *
 * Java is ALWAYS pass by value. There is no exception for objects.
 *
 * The confusion arises because for an object the VALUE being copied is a
 * reference. So the method gets its own copy of the arrow pointing at the
 * object — which means it can follow the arrow and change what it finds, but
 * repointing its own copy leaves the caller's arrow exactly where it was.
 *
 *     caller:  arr ──────┐
 *                        ▼
 *                    [1, 2, 3]      mutating through either arrow is visible
 *                        ▲
 *     method:  arr ──────┘          repointing this one changes nothing above
 *
 * @author  Divesh Agarwal
 * @since   2026-09-08
 */
public class PassByValue {

    public static void main(String[] args) {
        System.out.println("Day 26 - Pass by value");
        System.out.println();

        primitivesAreCopied();
        objectsShareTheObject();
        butNotTheVariable();
        immutabilityMakesItMoot();
        theRule();
    }

    // ── primitives ─────────────────────────────────────────────────────────

    /** The classic failed swap: a and b are copies, so nothing escapes. */
    static void swap(int a, int b) {
        int temp = a;
        a = b;
        b = temp;
    }

    private static void primitivesAreCopied() {
        int a = 1;
        int b = 2;
        swap(a, b);

        System.out.println("-- Primitives --");
        System.out.println("  before swap(a, b): a=1 b=2");
        System.out.println("  after  swap(a, b): a=" + a + " b=" + b + "   <- unchanged");
        System.out.println();
        System.out.println("  swap received COPIES. It swapped them perfectly, then");
        System.out.println("  they went out of scope. You cannot write a swap method");
        System.out.println("  for two ints in Java — return them, or pass an array.");
        System.out.println();
    }

    // ── objects: mutation works ────────────────────────────────────────────

    static void mutateArray(int[] values) {
        values[0] = 99;                      // follows the arrow, edits the object
    }

    static void mutateBuilder(StringBuilder sb) {
        sb.append(" changed");               // same idea (Day 25)
    }

    private static void objectsShareTheObject() {
        int[] numbers = { 1, 2, 3 };
        mutateArray(numbers);

        StringBuilder text = new StringBuilder("original");
        mutateBuilder(text);

        System.out.println("-- Objects: mutation IS visible --");
        System.out.println("  mutateArray(numbers)  -> " + Arrays.toString(numbers) + "   <- CHANGED");
        System.out.println("  mutateBuilder(text)   -> [" + text + "]   <- CHANGED");
        System.out.println();
        System.out.println("  The copied reference points at the SAME object, so");
        System.out.println("  changes made through it are changes to the caller's");
        System.out.println("  object. This is the aliasing of Day 19 and Day 22.");
        System.out.println();
    }

    // ── objects: reassignment does not ─────────────────────────────────────

    static void reassignArray(int[] values) {
        values = new int[] { 7, 7, 7 };      // repoints the LOCAL copy only
        values[0] = 100;                     // edits the new array, not the caller's
    }

    static void reassignBuilder(StringBuilder sb) {
        sb = new StringBuilder("brand new");
        sb.append("!");
    }

    private static void butNotTheVariable() {
        int[] numbers = { 1, 2, 3 };
        reassignArray(numbers);

        StringBuilder text = new StringBuilder("original");
        reassignBuilder(text);

        System.out.println("-- Objects: reassignment is NOT visible --");
        System.out.println("  reassignArray(numbers) -> " + Arrays.toString(numbers) + "   <- unchanged");
        System.out.println("  reassignBuilder(text)  -> [" + text + "]   <- unchanged");
        System.out.println();
        System.out.println("  This is the proof that Java is not pass by reference. If");
        System.out.println("  it were, reassigning the parameter would repoint the");
        System.out.println("  caller's variable too. It does not.");
        System.out.println();
        System.out.println("  A method cannot make your variable refer to a different");
        System.out.println("  object. It can only change the object you both share.");
        System.out.println();
    }

    // ── immutable objects ──────────────────────────────────────────────────

    static void tryToChange(String s) {
        s = s + " changed";                  // builds a new String, rebinds the copy
        s.toUpperCase();                     // discarded anyway (Day 23)
    }

    private static void immutabilityMakesItMoot() {
        String text = "original";
        tryToChange(text);

        System.out.println("-- Immutable objects --");
        System.out.println("  tryToChange(text) -> [" + text + "]   <- unchanged");
        System.out.println();
        System.out.println("  Two independent reasons here. Reassigning the parameter");
        System.out.println("  cannot escape (above), AND String has no mutating method");
        System.out.println("  to call in the first place (Day 23).");
        System.out.println();
        System.out.println("  That is why passing a String is always safe, and passing");
        System.out.println("  an array or a StringBuilder is not: the callee can edit");
        System.out.println("  what you handed it. Pass a copy if that matters.");
        System.out.println();
    }

    private static void theRule() {
        System.out.println("-- The rule --");
        System.out.println();
        System.out.printf("  %-22s %-16s %-16s%n", "argument type", "mutate it?", "reassign it?");
        System.out.printf("  %-22s %-16s %-16s%n", "primitive", "n/a", "no effect");
        System.out.printf("  %-22s %-16s %-16s%n", "mutable object", "VISIBLE", "no effect");
        System.out.printf("  %-22s %-16s %-16s%n", "immutable object", "impossible", "no effect");
        System.out.println();
        System.out.println("  In one sentence: a method can change what the object");
        System.out.println("  CONTAINS, never which object your variable POINTS AT.");
        System.out.println();
        System.out.println("  \"Java passes objects by reference\" is the most common");
        System.out.println("  wrong statement about the language. It passes references");
        System.out.println("  BY VALUE — which is a different thing, and the middle row");
        System.out.println("  of that table is where the difference shows.");
    }
}
