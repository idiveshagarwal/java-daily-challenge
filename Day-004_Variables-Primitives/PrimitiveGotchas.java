/**
 * Day 4, companion file — the ways primitive arithmetic surprises you.
 *
 * Every case below is correct Java behaving exactly as specified. They are
 * collected here because each one is a bug waiting to happen in real code.
 *
 * @author  Divesh Agarwal
 * @version 1.0
 * @since   2026-08-17
 */
public class PrimitiveGotchas {

    public static void main(String[] args) {
        silentOverflow();
        floatingPointIsBinary();
        integerDivision();
        compoundAssignmentHidesACast();
        wrapperIdentity();
    }

    /** Integer arithmetic wraps around instead of throwing. */
    private static void silentOverflow() {
        int max = Integer.MAX_VALUE;

        System.out.println("1. Overflow wraps silently");
        System.out.println("   Integer.MAX_VALUE     = " + max);
        System.out.println("   Integer.MAX_VALUE + 1 = " + (max + 1) + "   <- now negative");

        // The classic: both operands are int, so the multiplication overflows
        // BEFORE the widening to long ever happens.
        long wrong = 1_000_000 * 1_000_000;
        long right = 1_000_000L * 1_000_000;
        System.out.println("   1_000_000 * 1_000_000       = " + wrong + "   <- int maths");
        System.out.println("   1_000_000L * 1_000_000      = " + right + "   <- long maths");

        try {
            Math.addExact(max, 1);
        } catch (ArithmeticException e) {
            System.out.println("   Math.addExact throws instead: " + e.getMessage());
        }
        System.out.println();
    }

    /** Binary floating point cannot represent most decimal fractions exactly. */
    private static void floatingPointIsBinary() {
        double sum = 0.1 + 0.2;

        System.out.println("2. Floating point is binary, not decimal");
        System.out.println("   0.1 + 0.2            = " + sum);
        System.out.println("   0.1 + 0.2 == 0.3     ? " + (sum == 0.3));
        System.out.println("   difference           = " + Math.abs(sum - 0.3));

        System.out.println("   compare with a tolerance instead:");
        System.out.println("     |sum - 0.3| < 1e-9 ? " + (Math.abs(sum - 0.3) < 1e-9));
        System.out.println("   or use BigDecimal for money — never double.");

        System.out.println("   special values: " + (1.0 / 0) + ", " + (-1.0 / 0) + ", " + (0.0 / 0));
        System.out.println("   NaN == NaN           ? " + (Double.NaN == Double.NaN)
                + "   <- use Double.isNaN()");
        System.out.println();
    }

    /** Division between two ints is integer division, and /0 is fatal. */
    private static void integerDivision() {
        System.out.println("3. Integer division truncates");
        System.out.println("   7 / 2       = " + (7 / 2) + "     <- both operands int");
        System.out.println("   7 / 2.0     = " + (7 / 2.0) + "   <- one double promotes both");
        System.out.println("   7 % 2       = " + (7 % 2));
        System.out.println("   -7 / 2      = " + (-7 / 2) + "    <- truncates toward zero");
        System.out.println("   -7 % 2      = " + (-7 % 2) + "    <- sign follows the dividend");

        try {
            System.out.println(1 / 0);
        } catch (ArithmeticException e) {
            System.out.println("   1 / 0       throws " + e);
        }
        System.out.println("   1.0 / 0     = " + (1.0 / 0) + "   <- floating point does NOT throw");
        System.out.println();
    }

    /** Compound operators perform an implicit narrowing cast. */
    private static void compoundAssignmentHidesACast() {
        byte b = 10;
        // b = b + 300;   // error: incompatible types, possible lossy conversion
        b += 300;         // compiles: b = (byte) (b + 300)

        short s = 1;
        s *= 100_000;     // same hidden cast, same silent truncation

        System.out.println("4. Compound assignment hides a narrowing cast");
        System.out.println("   byte b = 10; b += 300   -> " + b + "   (b = (byte)(b + 300))");
        System.out.println("   short s = 1; s *= 100000 -> " + s);
        System.out.println("   b = b + 300 does NOT compile — the shorthand is not equivalent.");
        System.out.println();
    }

    /** Wrapper caching makes == on boxed values inconsistent. */
    private static void wrapperIdentity() {
        Integer a = 127, b = 127;      // served from the Integer cache
        Integer c = 128, d = 128;      // outside the cache: two distinct objects

        System.out.println("5. == on wrappers compares references");
        System.out.println("   Integer 127 == 127   ? " + (a == b) + "   <- cached -128..127");
        System.out.println("   Integer 128 == 128   ? " + (c == d) + "  <- new objects");
        System.out.println("   c.equals(d)          ? " + c.equals(d) + "   <- always use equals()");

        int unboxed = c;
        System.out.println("   int 128 == Integer 128 ? " + (unboxed == d)
                + "   <- unboxes, so value compare");

        Integer nothing = null;
        try {
            int boom = nothing;
            System.out.println(boom);
        } catch (NullPointerException e) {
            System.out.println("   unboxing null throws NullPointerException");
        }
        System.out.println();
    }
}
