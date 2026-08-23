/**
 * Day 10 — if and if-else.
 *
 * The structure is familiar from any C-family language, with one strict
 * difference: the condition must be a boolean. Java does not treat 0 as false
 * or non-zero as true, and that restriction removes a whole class of C bugs.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-23
 */
public class IfBasics {

    public static void main(String[] args) {
        System.out.println("Day 10 — if / if-else");
        System.out.println();

        theConditionMustBeBoolean();
        ifElse();
        alwaysUseBraces();
        conditionsOnObjects();
        simplifyingBooleanReturns();
    }

    /**
     * `if (x)` where x is an int does not compile:
     *     error: incompatible types: int cannot be converted to boolean
     *
     * This also kills the classic C typo `if (x = 1)` — for numeric types.
     * It does NOT protect booleans: `if (flag = true)` compiles, assigns, and
     * is always true (Day 7).
     */
    private static void theConditionMustBeBoolean() {
        int count = 3;

        System.out.println("── The condition must be boolean ──");
        // if (count) { }              // error: int cannot be converted to boolean
        if (count != 0) {              // say what you mean
            System.out.println("count != 0 -> " + count);
        }
        System.out.println("`if (count)` and `if (count = 1)` are BOTH compile errors.");
        System.out.println("`if (flag = true)` compiles though — booleans stay vulnerable.");
        System.out.println();
    }

    private static void ifElse() {
        int temperature = 31;

        System.out.println("── if / else ──");
        if (temperature > 30) {
            System.out.println(temperature + "°C — hot");
        } else {
            System.out.println(temperature + "°C — not hot");
        }
        System.out.println("Exactly one branch runs. else is optional.");
        System.out.println();
    }

    /**
     * Braces are optional for a single statement, and omitting them is how the
     * "goto fail" class of bug happens: indentation suggests a block, but only
     * the first statement is actually guarded.
     */
    private static void alwaysUseBraces() {
        boolean authorised = false;

        System.out.println("── Always use braces ──");
        System.out.println("Without braces, only the FIRST statement is guarded:");
        System.out.println();
        System.out.println("  if (authorised)");
        System.out.println("      log(\"granted\");");
        System.out.println("      grantAccess();     // <- runs ALWAYS");
        System.out.println();

        if (authorised) {
            System.out.println("  granted");
        }
        System.out.println("  authorised = " + authorised + " -> correctly denied (braces present)");
        System.out.println();
    }

    /** Conditions on references: null checks and correct String comparison. */
    private static void conditionsOnObjects() {
        String input = null;

        System.out.println("── Conditions on objects ──");
        System.out.println("null-safe check   : " + (input != null && input.equals("yes")));
        System.out.println("  the && guard stops equals() being called on null (Day 8)");
        System.out.println();
        System.out.println("Yoda form is null-safe without a guard:");
        System.out.println("  \"yes\".equals(input) -> " + "yes".equals(input));
        System.out.println();
        System.out.println("Never use == on Strings in a condition — it compares");
        System.out.println("identity, not contents (Day 6).");
        System.out.println();
    }

    /**
     * A condition whose only job is to return true or false is redundant —
     * the condition IS the answer.
     */
    private static boolean isAdultVerbose(int age) {
        if (age >= 18) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isAdult(int age) {
        return age >= 18;          // same thing, one line
    }

    private static void simplifyingBooleanReturns() {
        System.out.println("── Simplify boolean returns ──");
        System.out.println("  if (age >= 18) return true; else return false;");
        System.out.println("  return age >= 18;                 <- identical, clearer");
        System.out.println();
        System.out.println("isAdultVerbose(20) = " + isAdultVerbose(20)
                + "   isAdult(20) = " + isAdult(20));
    }
}
