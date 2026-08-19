/**
 * Day 5 — type casting between primitives.
 *
 * Two directions:
 *   widening  (small -> large)  happens implicitly, no cast written
 *   narrowing (large -> small)  requires an explicit (cast), because data
 *                               can be lost and javac refuses to guess
 *
 * @author  Divesh Agarwal
 * @since   2026-08-18
 */
public class TypeCasting {

    public static void main(String[] args) {
        System.out.println("Day 5 — Type casting: implicit vs explicit");
        System.out.println();

        implicitWidening();
        explicitNarrowing();
        wideningCanStillLoseData();
        promotionInExpressions();
        compoundAssignmentHidesACast();
        edgeCases();
    }

    /**
     * The widening ladder: byte -> short -> int -> long -> float -> double.
     * char slots in as char -> int. No cast needed; the compiler inserts the
     * conversion silently because the target can always hold the value.
     */
    private static void implicitWidening() {
        byte b = 42;
        short s = b;      // byte  -> short
        int i = s;        // short -> int
        long l = i;       // int   -> long
        float f = l;      // long  -> float
        double d = f;     // float -> double

        System.out.println("── Implicit (widening) ──");
        System.out.println("byte 42 climbs the ladder -> double " + d);
        System.out.println("char 'A' -> int " + (int) 'A' + "  (char widens to int)");
        System.out.println();
    }

    /**
     * Narrowing throws away the high-order bits. Java does not clamp and does
     * not warn at runtime — it simply truncates, which is why the cast must be
     * written by hand as an acknowledgement.
     */
    private static void explicitNarrowing() {
        int n = 200;
        byte overflowed = (byte) n;   // 200 does not fit in -128..127

        double pos = 3.99;
        double neg = -3.99;

        System.out.println("── Explicit (narrowing) ──");
        System.out.println("(byte) 200   = " + overflowed + "   <- wrapped, not clamped");
        System.out.println("(int) 3.99   = " + (int) pos + "      <- truncates toward zero");
        System.out.println("(int) -3.99  = " + (int) neg + "     <- NOT rounded to -4");
        System.out.println("(char) 65    = " + (char) 65);
        System.out.println();
    }

    /**
     * The counter-intuitive part: widening is implicit because it is always
     * *allowed*, not because it is always exact. int -> float and long ->
     * double both widen, yet both can lose precision, because floats trade
     * mantissa bits for range.
     */
    private static void wideningCanStillLoseData() {
        int i = 16_777_217;           // 2^24 + 1
        float f = i;                  // implicit widening — no cast, no warning

        long l = 9_007_199_254_740_993L;  // 2^53 + 1
        double d = l;                     // implicit widening

        System.out.println("── Widening that still loses data ──");
        System.out.println("int  " + i + " -> float -> int  = " + (int) f);
        System.out.println("long " + l + " -> double -> long = " + (long) d);
        System.out.println("Both lost 1. float has 24 bits of mantissa, double 53.");
        System.out.println();
    }

    /**
     * Binary numeric promotion: operands smaller than int are promoted to int
     * before arithmetic, and the wider operand wins. This is why 5 / 2 is 2 —
     * both are ints, so the division is integer division, and the result is
     * truncated *before* anything is assigned.
     */
    private static void promotionInExpressions() {
        System.out.println("── Promotion in expressions ──");
        System.out.println("5 / 2        = " + (5 / 2) + "     <- int division, truncated");
        System.out.println("5 / 2.0      = " + (5 / 2.0) + "   <- one double promotes both");
        System.out.println("(double)5/2  = " + ((double) 5 / 2) + "   <- cast binds to 5 only");
        System.out.println("'A' + 1      = " + ('A' + 1) + "    <- char promoted to int");
        System.out.println("(char)('A'+1)= " + (char) ('A' + 1) + "     <- cast back to see a letter");
        System.out.println();
    }

    /**
     * Compound assignment operators (+=, -=, *=, /=) contain a hidden narrowing
     * cast. This is the single most surprising rule in the topic:
     *
     *     b += 5;      compiles
     *     b = b + 5;   does NOT compile
     *
     * because b + 5 promotes to int, and assigning int to byte needs a cast —
     * but the spec defines b += 5 as b = (byte)(b + 5).
     */
    private static void compoundAssignmentHidesACast() {
        byte b = 10;
        b += 5;           // legal — implicit (byte) cast supplied by the spec

        byte wraps = 120;
        wraps += 10;      // 130 does not fit; wraps silently

        System.out.println("── Compound assignment ──");
        System.out.println("byte b = 10; b += 5   -> " + b);
        System.out.println("byte w = 120; w += 10 -> " + wraps + "   <- silent overflow");

        // byte bad = 10; bad = bad + 5;
        //   error: incompatible types: possible lossy conversion from int to byte
        System.out.println();
    }

    /** Boundary behaviour that catches people out in tests. */
    private static void edgeCases() {
        System.out.println("── Edge cases ──");
        System.out.println("(int) Double.NaN      = " + (int) Double.NaN + "            <- NaN becomes 0");
        System.out.println("(int) 1e20            = " + (int) 1e20 + "   <- clamps to Integer.MAX_VALUE");
        System.out.println("0.1 + 0.2             = " + (0.1 + 0.2) + " <- binary float, not a cast bug");
        System.out.println();
        System.out.println("Never castable: boolean <-> any numeric type.");
        System.out.println("  error: incompatible types: boolean cannot be converted to int");
    }
}
