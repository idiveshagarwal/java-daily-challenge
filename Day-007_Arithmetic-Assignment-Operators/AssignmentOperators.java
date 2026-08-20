/**
 * Day 7, part 2 — assignment operators.
 *
 * `=` is not a statement in Java, it is an EXPRESSION that produces a value.
 * That single fact explains chained assignment, why `if (a = b)` compiles for
 * booleans, and why the compound forms can be used inside larger expressions.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-20
 */
public class AssignmentOperators {

    public static void main(String[] args) {
        System.out.println("Day 7 — Assignment operators");
        System.out.println();

        assignmentIsAnExpression();
        theCompoundFamily();
        compoundHidesACast();
        compoundOnStrings();
        evaluationOrderIsLeftToRight();
    }

    /**
     * `x = 5` evaluates to 5. Because `=` is right-associative, `x = y = 5`
     * parses as `x = (y = 5)` — the inner assignment runs first and its value
     * feeds the outer one.
     */
    private static void assignmentIsAnExpression() {
        int x, y;
        x = y = 5;

        int n = 10;

        System.out.println("── = is an expression ──");
        System.out.println("x = y = 5      -> x=" + x + ", y=" + y + "   (right-associative)");
        System.out.println("(n = 20) yields " + (n = 20) + "     <- the assignment has a value");
        System.out.println("n is now " + n);
        System.out.println();
        System.out.println("Hazard: `if (flag = true)` compiles for booleans and is");
        System.out.println("always true — it ASSIGNS rather than compares. Use ==.");
        System.out.println();
    }

    /** Every arithmetic operator has a compound form. */
    private static void theCompoundFamily() {
        int v = 20;

        System.out.println("── Compound assignment ──");
        System.out.print("start 20");
        v += 5;  System.out.print("  +=5 -> " + v);
        v -= 3;  System.out.print("  -=3 -> " + v);
        v *= 2;  System.out.print("  *=2 -> " + v);
        v /= 4;  System.out.print("  /=4 -> " + v);
        v %= 7;  System.out.println("  %=7 -> " + v);
        System.out.println("Also for bitwise: &= |= ^= <<= >>= >>>=");
        System.out.println();
    }

    /**
     * `a op= b` is defined as `a = (T)(a op b)` where T is a's type. The cast
     * is inserted by the compiler, which is why compound assignment compiles in
     * places plain assignment does not — covered in Day 4 and Day 5, repeated
     * here because it is an *operator* property, not a casting one.
     */
    private static void compoundHidesACast() {
        byte b = 10;
        b += 5;              // legal: b = (byte)(b + 5)
        // b = b + 5;        // error: possible lossy conversion from int to byte

        int i = 7;
        i /= 2;              // 3 — truncation still applies

        double d = 7;
        d /= 2;              // 3.5

        System.out.println("── The hidden cast ──");
        System.out.println("byte b = 10; b += 5  -> " + b + "   (b = (byte)(b + 5))");
        System.out.println("but  b = b + 5       -> compile error");
        System.out.println();
        System.out.println("int    i = 7; i /= 2 -> " + i + "     <- still integer division");
        System.out.println("double d = 7; d /= 2 -> " + d + "   <- type of the LEFT side decides");
        System.out.println();
    }

    /** += on a String is concatenation, and works with any right-hand type. */
    private static void compoundOnStrings() {
        String s = "Day";
        s += " ";
        s += 7;              // int is converted, not added
        s += '!';

        System.out.println("── += on String ──");
        System.out.println("built up: \"" + s + "\"");
        System.out.println("Each += allocates a new String — fine here, but use");
        System.out.println("StringBuilder inside loops.");
        System.out.println();
    }

    /**
     * Java fully specifies evaluation order: the left operand is evaluated
     * before the right, and operands before the operator is applied. Unlike C,
     * this is defined behaviour, not undefined — the result below is guaranteed.
     */
    private static void evaluationOrderIsLeftToRight() {
        int[] counter = { 0 };

        int result = next(counter) * 100 + next(counter) * 10 + next(counter);

        System.out.println("── Evaluation order ──");
        System.out.println("next() returns 1, then 2, then 3");
        System.out.println("next()*100 + next()*10 + next() = " + result);
        System.out.println("Left operand always evaluated first — guaranteed by the spec,");
        System.out.println("unlike C/C++ where this would be unspecified.");
    }

    private static int next(int[] counter) {
        return ++counter[0];
    }
}
