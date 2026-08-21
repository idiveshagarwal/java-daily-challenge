/**
 * Day 8, part 3 — the conditional operator ?: (Java's only ternary operator).
 *
 *     condition ? valueIfTrue : valueIfFalse
 *
 * It is an EXPRESSION, not a statement — it produces a value, which is what
 * separates it from an if/else. That also means it has a TYPE, and the rules
 * for working that type out produce two genuine surprises.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-21
 */
public class TernaryOperator {

    public static void main(String[] args) {
        System.out.println("Day 8 — The ternary operator");
        System.out.println();

        basics();
        itIsAnExpressionNotAStatement();
        bothBranchesDecideTheType();
        unboxingCanThrow();
        nestingAndReadability();
    }

    private static void basics() {
        int age = 20;
        String status = age >= 18 ? "adult" : "minor";

        System.out.println("── Basics ──");
        System.out.println("age = " + age + " -> " + status);
        System.out.println("Only ONE branch is evaluated — like && and ||,");
        System.out.println("the untaken side never runs.");
        System.out.println();
    }

    /**
     * Because it yields a value, it can go where a statement cannot: inside a
     * method argument, a field initialiser, or a return expression.
     */
    private static void itIsAnExpressionNotAStatement() {
        int count = 1;

        System.out.println("── It is an expression ──");
        System.out.println("Inline in a string: " + count + " item" + (count == 1 ? "" : "s"));
        System.out.println("As an argument:     " + Math.abs(count > 0 ? -5 : 5));
        System.out.println();
        System.out.println("An if/else could not appear in either position.");
        System.out.println();
    }

    /**
     * SURPRISE 1. The type of the whole expression is computed from BOTH
     * branches before either is chosen. If they are different numeric types,
     * binary numeric promotion applies (Day 5) — so the untaken branch changes
     * the result of the taken one.
     */
    private static void bothBranchesDecideTheType() {
        Object result = true ? 1 : 2.0;          // condition is TRUE, so "1"...
        Object boxed = true ? Integer.valueOf(1) : Double.valueOf(2.0);

        System.out.println("── Both branches decide the type ──");
        System.out.println("true ? 1 : 2.0                     -> " + result
                + "   (" + result.getClass().getSimpleName() + ")");
        System.out.println("   the taken branch is the int 1, yet the result is a double");
        System.out.println("   because int and double promote to double.");
        System.out.println();
        System.out.println("true ? Integer(1) : Double(2.0)    -> " + boxed
                + "   (" + boxed.getClass().getSimpleName() + ")");
        System.out.println("   the wrappers are UNBOXED, promoted, and re-boxed.");
        System.out.println();
        System.out.println("Keep both branches the same type to avoid this entirely.");
        System.out.println();
    }

    /**
     * SURPRISE 2. If one branch is a wrapper and the other a primitive, the
     * expression's type is the primitive — so the wrapper is unboxed. When the
     * wrapper is null, that unboxing throws, even though the null branch was
     * never selected as a value.
     */
    private static void unboxingCanThrow() {
        Integer maybeNull = null;

        System.out.println("── Unboxing NPE ──");
        System.out.println("Integer maybeNull = null;");
        System.out.println("int v = (1 > 0) ? maybeNull : 0;");

        try {
            int v = (1 > 0) ? maybeNull : 0;
            System.out.println("got " + v);
        } catch (NullPointerException e) {
            System.out.println("   -> NullPointerException");
        }

        System.out.println();
        System.out.println("The mixed Integer/int branches force the type to int,");
        System.out.println("so the null Integer is unboxed. Make BOTH branches");
        System.out.println("Integer and it returns null harmlessly:");

        Integer safe = (1 > 0) ? maybeNull : Integer.valueOf(0);
        System.out.println("   Integer safe = ... -> " + safe);
        System.out.println();
    }

    private static void nestingAndReadability() {
        int score = 73;

        // Right-associative: reads as a ? : (b ? : (c ? : ))
        String grade = score >= 90 ? "A"
                     : score >= 80 ? "B"
                     : score >= 70 ? "C"
                     : "F";

        System.out.println("── Nesting ──");
        System.out.println("score " + score + " -> grade " + grade);
        System.out.println("?: is right-associative, so chains read top to bottom.");
        System.out.println("Two levels is usually the limit before if/else or");
        System.out.println("a switch expression is clearer.");
    }
}
