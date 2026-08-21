/**
 * Day 8, part 1 — relational operators: &lt; &lt;= &gt; &gt;= == !=
 *
 * All six produce a boolean. The ordering four work only on numbers and char;
 * == and != also work on references and booleans. Two families of edge case
 * make this less obvious than it looks: NaN, and negative zero.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-21
 */
public class RelationalOperators {

    public static void main(String[] args) {
        System.out.println("Day 8 — Relational operators");
        System.out.println();

        theSixOperators();
        nanFailsEveryComparison();
        negativeZeroIsInconsistent();
        equalityOnReferences();
    }

    private static void theSixOperators() {
        int a = 7, b = 3;

        System.out.println("── The six ──");
        System.out.println("7 <  3 = " + (a < b));
        System.out.println("7 <= 3 = " + (a <= b));
        System.out.println("7 >  3 = " + (a > b));
        System.out.println("7 >= 3 = " + (a >= b));
        System.out.println("7 == 3 = " + (a == b));
        System.out.println("7 != 3 = " + (a != b));
        System.out.println();
        System.out.println("< <= > >= work on numbers and char only.");
        System.out.println("'a' < 'b' = " + ('a' < 'b') + "   <- char compares by code point");
        System.out.println("boolean and objects support only == and !=.");
        System.out.println();
    }

    /**
     * NaN is unordered: every comparison against it is false, including
     * NaN == NaN. Only != returns true, and it does so for NaN against
     * absolutely anything — including itself.
     */
    private static void nanFailsEveryComparison() {
        double nan = 0.0 / 0.0;

        System.out.println("── NaN is unordered ──");
        System.out.println("NaN <  1   = " + (nan < 1));
        System.out.println("NaN >  1   = " + (nan > 1));
        System.out.println("NaN <= 1   = " + (nan <= 1));
        System.out.println("NaN == NaN = " + (nan == nan) + "   <- not reflexive");
        System.out.println("NaN != NaN = " + (nan != nan) + "    <- the only true one");
        System.out.println();
        System.out.println("Consequence: `if (x < 1) ... else ...` sends NaN down the");
        System.out.println("ELSE branch, because the test is false, not because x >= 1.");
        System.out.println("Correct test: Double.isNaN(x) = " + Double.isNaN(nan));
        System.out.println();
    }

    /**
     * IEEE-754 has two zeros. The == operator treats them as equal, but
     * Double.compare and Double.equals do not — three ways to compare, two
     * different answers.
     */
    private static void negativeZeroIsInconsistent() {
        System.out.println("── Negative zero ──");
        System.out.println("-0.0 == 0.0                        = " + (-0.0 == 0.0) + "   <- operator says equal");
        System.out.println("Double.compare(-0.0, 0.0)          = " + Double.compare(-0.0, 0.0) + "     <- says LESS THAN");
        System.out.println("Double.valueOf(-0.0).equals(0.0)   = " + Double.valueOf(-0.0).equals(0.0) + "  <- says not equal");
        System.out.println();
        System.out.println("This matters when sorting or using doubles as map keys:");
        System.out.println("a TreeMap uses compare(), a HashMap uses equals().");
        System.out.println();
    }

    /**
     * On references, == asks "same object?", never "same contents?". Day 4
     * covers wrapper caching and Day 6 covers the String pool; the rule itself
     * is a property of the operator.
     */
    private static void equalityOnReferences() {
        String x = "java";
        String y = new String("java");

        Integer small1 = 127, small2 = 127;     // served from the Integer cache
        Integer big1 = 128, big2 = 128;         // outside the cache

        System.out.println("── == on references ──");
        System.out.println("\"java\" == new String(\"java\") : " + (x == y) + "   <- different objects");
        System.out.println("\"java\".equals(new String(...)) : " + x.equals(y) + "    <- same contents");
        System.out.println();
        System.out.println("Integer 127 == 127 : " + (small1 == small2) + "   <- cached (-128..127)");
        System.out.println("Integer 128 == 128 : " + (big1 == big2) + "  <- outside the cache");
        System.out.println("Same code, different answer by value. See Day 4.");
        System.out.println();
        System.out.println("Rule: == for primitives, .equals() for objects.");
    }
}
