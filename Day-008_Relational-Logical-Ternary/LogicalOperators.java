/**
 * Day 8, part 2 — logical operators: &amp;&amp; || ! and the non-short-circuit &amp; | ^
 *
 * The important distinction is not what they compute — && and & give the same
 * answer — but WHETHER THE RIGHT OPERAND IS EVALUATED AT ALL.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-21
 */
public class LogicalOperators {

    /** Counts how many times the guarded expression actually ran. */
    private static int calls = 0;

    private static boolean sideEffect() {
        calls++;
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Day 8 — Logical operators");
        System.out.println();

        truthTables();
        shortCircuitSkipsTheRightOperand();
        shortCircuitAsAGuard();
        exclusiveOr();
    }

    private static void truthTables() {
        System.out.println("── Truth tables ──");
        System.out.println("  a      b      a&&b   a||b   a^b");
        for (boolean a : new boolean[] { true, false }) {
            for (boolean b : new boolean[] { true, false }) {
                System.out.printf("  %-6s %-6s %-6s %-6s %-6s%n",
                        a, b, (a && b), (a || b), (a ^ b));
            }
        }
        System.out.println();
        System.out.println("! negates:  !true = " + (!true) + ", !false = " + (!false));
        System.out.println();
    }

    /**
     * && stops as soon as the left operand is false; || stops as soon as the
     * left is true. & and | always evaluate both sides. The call counter makes
     * the difference observable.
     */
    private static void shortCircuitSkipsTheRightOperand() {
        System.out.println("── Short-circuit vs full evaluation ──");

        calls = 0;
        boolean ignoredA = false && sideEffect();
        System.out.println("false && f()  -> f called " + calls + " time(s)   <- skipped");

        calls = 0;
        boolean ignoredB = false & sideEffect();
        System.out.println("false &  f()  -> f called " + calls + " time(s)   <- evaluated anyway");

        calls = 0;
        boolean ignoredC = true || sideEffect();
        System.out.println("true  || f()  -> f called " + calls + " time(s)   <- skipped");

        calls = 0;
        boolean ignoredD = true | sideEffect();
        System.out.println("true  |  f()  -> f called " + calls + " time(s)   <- evaluated anyway");

        System.out.println();
        System.out.println("Same boolean result either way — only the side effects differ.");
        System.out.println();
    }

    /**
     * Short-circuiting is what makes the standard null check work. With & the
     * same line would throw, because the right side runs regardless.
     */
    private static void shortCircuitAsAGuard() {
        String nothing = null;
        int[] empty = {};

        System.out.println("── Short-circuit as a guard ──");
        System.out.println("s != null && s.length() > 0  -> " + (nothing != null && nothing.length() > 0));
        System.out.println("   safe: the length() call never happens");
        System.out.println("   with & instead, this line throws NullPointerException");
        System.out.println();
        System.out.println("arr.length > 0 && arr[0] == 1 -> " + (empty.length > 0 && empty[0] == 1));
        System.out.println("   safe: no ArrayIndexOutOfBoundsException");
        System.out.println();
        System.out.println("Order matters — the guard must come FIRST.");
        System.out.println();
    }

    /**
     * ^ is exclusive-or: true when the operands differ. It has no
     * short-circuit form, because it cannot be decided from one operand.
     */
    private static void exclusiveOr() {
        System.out.println("── Exclusive OR ──");
        System.out.println("true  ^ true  = " + (true ^ true));
        System.out.println("true  ^ false = " + (true ^ false));
        System.out.println("false ^ false = " + (false ^ false));
        System.out.println();
        System.out.println("^ has no short-circuit version: both operands are always");
        System.out.println("needed, since neither alone determines the answer.");
        System.out.println();
        System.out.println("Reads well as \"exactly one of these\":");
        boolean hasEmail = true, hasPhone = false;
        System.out.println("exactly one contact method: " + (hasEmail ^ hasPhone));
    }
}
