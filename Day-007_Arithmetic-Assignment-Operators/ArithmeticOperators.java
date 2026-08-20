/**
 * Day 7 — arithmetic operators: + - * / % and the unary ++ / --.
 *
 * The operators themselves are obvious. What catches people out is the type
 * rules around them (Day 4, Day 5) and three behaviours that have no equivalent
 * in ordinary maths: the sign of %, division by zero differing between integer
 * and floating-point, and the return value of ++ depending on which side it is
 * written.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-20
 */
public class ArithmeticOperators {

    public static void main(String[] args) {
        System.out.println("Day 7 — Arithmetic operators");
        System.out.println();

        theFiveBinaryOperators();
        moduloTakesTheSignOfTheDividend();
        divisionByZeroDiffers();
        incrementAndDecrement();
        theClassicSelfAssignmentBug();
        plusIsOverloadedForStrings();
    }

    /** + - * / % — the only five. Everything else is a method on Math. */
    private static void theFiveBinaryOperators() {
        int a = 17, b = 5;

        System.out.println("── The five binary operators ──");
        System.out.println("17 + 5 = " + (a + b));
        System.out.println("17 - 5 = " + (a - b));
        System.out.println("17 * 5 = " + (a * b));
        System.out.println("17 / 5 = " + (a / b) + "     <- int division truncates (Day 4)");
        System.out.println("17 % 5 = " + (a % b) + "     <- remainder");
        System.out.println("5.5 % 2 = " + (5.5 % 2) + "  <- % works on doubles too");
        System.out.println();
    }

    /**
     * The rule most people guess wrong: in Java the result of % takes the sign
     * of the LEFT operand (the dividend), not the right. So -7 % 3 is -1, not 2.
     *
     * This makes `x % 2 == 1` an unreliable odd-number test for negatives —
     * -7 % 2 is -1, not 1. Use `x % 2 != 0`, or Math.floorMod.
     */
    private static void moduloTakesTheSignOfTheDividend() {
        System.out.println("── Sign of % follows the dividend ──");
        System.out.println(" 7 %  3 = " + (7 % 3));
        System.out.println("-7 %  3 = " + (-7 % 3) + "    <- negative, not 2");
        System.out.println(" 7 % -3 = " + (7 % -3) + "     <- positive; right operand's sign is ignored");
        System.out.println("-7 % -3 = " + (-7 % -3));
        System.out.println();
        System.out.println("Math.floorMod(-7, 3) = " + Math.floorMod(-7, 3) + "   <- takes the DIVISOR's sign");
        System.out.println("Use floorMod for wrap-around indexing (clocks, ring buffers).");
        System.out.println();
        System.out.println("Odd test:  -7 % 2 == 1  is " + (-7 % 2 == 1) + "   <- broken for negatives");
        System.out.println("           -7 % 2 != 0  is " + (-7 % 2 != 0) + "    <- correct");
        System.out.println();
    }

    /**
     * Integer and floating-point division disagree completely about zero.
     * Integer division throws; floating-point returns a special IEEE-754 value.
     */
    private static void divisionByZeroDiffers() {
        System.out.println("── Division by zero ──");

        try {
            System.out.println(1 / 0);
        } catch (ArithmeticException e) {
            System.out.println("int  1 / 0 -> " + e);
        }
        try {
            System.out.println(1 % 0);
        } catch (ArithmeticException e) {
            System.out.println("int  1 % 0 -> " + e + "   <- note: says \"/ by zero\"");
        }

        System.out.println("double  1.0 / 0   = " + (1.0 / 0));
        System.out.println("double -1.0 / 0   = " + (-1.0 / 0));
        System.out.println("double  0.0 / 0.0 = " + (0.0 / 0.0) + "        <- not an exception");
        System.out.println();
        System.out.println("NaN == NaN is " + (0.0 / 0.0 == 0.0 / 0.0)
                + "   <- NaN equals nothing, including itself");
        System.out.println("Test with Double.isNaN(x), never with ==.");
        System.out.println();
    }

    /**
     * ++ and -- each have two forms. Both change the variable by one; they
     * differ only in what the EXPRESSION evaluates to.
     */
    private static void incrementAndDecrement() {
        int i = 5;
        int postfix = i++;   // yields the OLD value, then increments

        int j = 5;
        int prefix = ++j;    // increments, then yields the NEW value

        System.out.println("── ++ postfix vs prefix ──");
        System.out.println("i = 5; i++ evaluates to " + postfix + ", i is now " + i);
        System.out.println("j = 5; ++j evaluates to " + prefix + ", j is now " + j);
        System.out.println("As a standalone statement they are identical — the");
        System.out.println("difference only matters when the value is used.");
        System.out.println();
    }

    /**
     * The classic interview question. `k = k++` leaves k unchanged, because:
     *   1. k++ is evaluated first, yielding 5 and setting k to 6
     *   2. the assignment then writes the saved 5 back over it
     * The increment is real, but immediately overwritten.
     */
    private static void theClassicSelfAssignmentBug() {
        int k = 5;
        k = k++;

        int m = 5;
        m = ++m;

        System.out.println("── k = k++ ──");
        System.out.println("k = 5; k = k++  ->  " + k + "   <- unchanged, the ++ is discarded");
        System.out.println("m = 5; m = ++m  ->  " + m + "   <- works, but still pointless");
        System.out.println("Never write either. Just use k++ on its own line.");
        System.out.println();
    }

    /**
     * + is the only overloaded operator in Java: numeric addition, or String
     * concatenation if either operand is a String. It is left-associative, so
     * position decides which meaning applies first.
     */
    private static void plusIsOverloadedForStrings() {
        System.out.println("── + is overloaded ──");
        System.out.println("1 + 2 + \"a\"   = " + (1 + 2 + "a") + "     <- 1+2 added FIRST, then concatenated");
        System.out.println("\"a\" + 1 + 2   = " + ("a" + 1 + 2) + "    <- concatenation starts at the left");
        System.out.println("'a' + 1       = " + ('a' + 1) + "     <- char promotes to int (Day 5)");
        System.out.println("\"\" + 'a' + 1  = " + ("" + 'a' + 1) + "     <- leading \"\" forces concatenation");
        System.out.println();
        System.out.println("Wrap arithmetic in parentheses inside a println:");
        System.out.println("  \"sum: \" + 1 + 2   -> sum: 12");
        System.out.println("  \"sum: \" + (1 + 2) -> sum: " + (1 + 2));
    }
}
