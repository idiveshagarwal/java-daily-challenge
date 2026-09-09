/**
 * Day 27 — method overloading.
 *
 * Overloading is several methods sharing one name, distinguished by their
 * PARAMETER LIST. The compiler picks which one you meant from the arguments at
 * the call site.
 *
 * What counts as "different" is narrower than most people expect: the number,
 * the types, and the order of parameters — and nothing else.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-09
 */
public class Overloading {

    public static void main(String[] args) {
        System.out.println("Day 27 - Method overloading");
        System.out.println();

        whatCountsAsDifferent();
        whatDoesNot();
        aRealisticFamily();
        chainingToOneImplementation();
        varargs();
    }

    // ── a family that differs by number, type and order ─────────────────────

    static int add(int a, int b) {
        return a + b;
    }

    static int add(int a, int b, int c) {                 // different NUMBER
        return a + b + c;
    }

    static double add(double a, double b) {               // different TYPE
        return a + b;
    }

    static String describe(String label, int value) {     // one order
        return label + " = " + value;
    }

    static String describe(int value, String label) {     // different ORDER
        return value + " is " + label;
    }

    private static void whatCountsAsDifferent() {
        System.out.println("-- What makes an overload --");
        System.out.println("  different NUMBER of parameters:");
        System.out.println("    add(2, 3)        = " + add(2, 3));
        System.out.println("    add(2, 3, 4)     = " + add(2, 3, 4));
        System.out.println();
        System.out.println("  different TYPES:");
        System.out.println("    add(2, 3)        = " + add(2, 3) + "     (int version)");
        System.out.println("    add(2.5, 3.5)    = " + add(2.5, 3.5) + "   (double version)");
        System.out.println();
        System.out.println("  different ORDER of types:");
        System.out.println("    describe(\"x\", 5)  = " + describe("x", 5));
        System.out.println("    describe(5, \"odd\")= " + describe(5, "odd"));
        System.out.println();
    }

    /**
     * The two things that look like they should distinguish methods and do not.
     * Both produce "method f(int) is already defined".
     */
    private static void whatDoesNot() {
        System.out.println("-- What does NOT make an overload --");
        System.out.println("  RETURN TYPE alone:");
        System.out.println("    static int    f(int n)");
        System.out.println("    static double f(int n)");
        System.out.println("    error: method f(int) is already defined");
        System.out.println();
        System.out.println("  PARAMETER NAME alone:");
        System.out.println("    static void f(int count)");
        System.out.println("    static void f(int total)");
        System.out.println("    error: method f(int) is already defined");
        System.out.println();
        System.out.println("  Both make sense once you see why: at the call site");
        System.out.println("  `f(5)` names neither the return type nor the parameter");
        System.out.println("  name, so neither could tell the compiler which you meant.");
        System.out.println();
        System.out.println("  The SIGNATURE is the name plus the parameter types. That");
        System.out.println("  is all the compiler has to match against.");
        System.out.println();
    }

    // ── a realistic overload family ────────────────────────────────────────

    /** Full control. */
    static String format(double value, int decimals, boolean showSign) {
        String body = String.format("%." + decimals + "f", Math.abs(value));
        String sign = value < 0 ? "-" : (showSign ? "+" : "");
        return sign + body;
    }

    /** Common case: no forced sign. Delegates. */
    static String format(double value, int decimals) {
        return format(value, decimals, false);
    }

    /** Simplest case: two decimals. Delegates. */
    static String format(double value) {
        return format(value, 2, false);
    }

    private static void aRealisticFamily() {
        System.out.println("-- Overloading as default arguments --");
        System.out.println("  format(3.14159)           = " + format(3.14159));
        System.out.println("  format(3.14159, 4)        = " + format(3.14159, 4));
        System.out.println("  format(3.14159, 1, true)  = " + format(3.14159, 1, true));
        System.out.println("  format(-3.14159, 1, true) = " + format(-3.14159, 1, true));
        System.out.println();
        System.out.println("  Java has no default parameter values, so an overload");
        System.out.println("  family is how the gap is filled. This is exactly why");
        System.out.println("  println has ten overloads.");
        System.out.println();
    }

    private static void chainingToOneImplementation() {
        System.out.println("-- One real implementation --");
        System.out.println("  format(double)              -> format(value, 2, false)");
        System.out.println("  format(double, int)         -> format(value, decimals, false)");
        System.out.println("  format(double, int, boolean)-> the actual work");
        System.out.println();
        System.out.println("  Every convenience overload delegates to the fullest one,");
        System.out.println("  so the logic exists once. Duplicating the body across");
        System.out.println("  overloads is how they drift apart.");
        System.out.println();
    }

    // ── varargs ────────────────────────────────────────────────────────────

    /** Varargs is really an array parameter with nicer call syntax. */
    static int sum(int... values) {
        int total = 0;
        for (int v : values) {                            // values IS an int[]
            total += v;
        }
        return total;
    }

    private static void varargs() {
        System.out.println("-- varargs --");
        System.out.println("  sum()            = " + sum() + "    (empty array, not null)");
        System.out.println("  sum(1, 2, 3)     = " + sum(1, 2, 3));
        System.out.println("  sum(new int[]{4,5}) = " + sum(new int[] { 4, 5 }) + "    (an array works too)");
        System.out.println();
        System.out.println("  `int...` is an int[] with call-site sugar. It must be the");
        System.out.println("  LAST parameter, and there can be only one.");
        System.out.println();
        System.out.println("  A varargs method is the compiler's LAST resort when");
        System.out.println("  resolving an overload - see OverloadResolution.");
    }
}
