/**
 * Day 6, part 2 — literals: the values you write directly in source.
 *
 * Every literal has a type before anything is assigned. An integer literal is
 * an `int` and a floating-point literal is a `double` unless a suffix says
 * otherwise, which is the source of most literal-related compile errors.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-19
 */
public class Literals {

    public static void main(String[] args) {
        System.out.println("Day 6 — Literals");
        System.out.println();

        integerBases();
        suffixesAndDefaults();
        charAndStringLiterals();
        stringInterning();
    }

    /** Four ways to write an integer, plus underscores for readability. */
    private static void integerBases() {
        int decimal = 31;
        int hex = 0x1F;          // 0x or 0X
        int binary = 0b1_1111;   // 0b or 0B, since Java 7
        int octal = 013;         // LEADING ZERO = octal. Not 13.

        System.out.println("── Integer literal bases ──");
        System.out.println("31      (decimal) = " + decimal);
        System.out.println("0x1F    (hex)     = " + hex);
        System.out.println("0b11111 (binary)  = " + binary);
        System.out.println("013     (octal)   = " + octal + "    <- TRAP: not 13");
        System.out.println("1_000_000         = " + 1_000_000 + "  <- underscores are ignored");
        System.out.println();
    }

    /**
     * Defaults: integer literal -> int, floating literal -> double. A literal
     * too large for its default type, or narrower than its default, needs a
     * suffix — not a cast.
     */
    private static void suffixesAndDefaults() {
        long big = 10_000_000_000L;   // without L: "error: integer number too large"
        float f = 3.14f;              // without f: "possible lossy conversion from double to float"
        double d = 3.14;              // already the default
        double sci = 1.5e3;           // scientific notation

        System.out.println("── Suffixes ──");
        System.out.println("10_000_000_000L = " + big + "   <- L required, literal is int by default");
        System.out.println("3.14f           = " + f + "        <- f required, literal is double by default");
        System.out.println("3.14            = " + d);
        System.out.println("1.5e3           = " + sci);
        System.out.println();
        System.out.println("Use uppercase L — lowercase l is easily misread as digit 1.");
        System.out.println();
    }

    /** char literals are single quotes; String literals are double quotes. */
    private static void charAndStringLiterals() {
        char letter = 'A';
        char unicode = '\u0041';   // same char via Unicode escape (see Day 3:
                                    // javac resolves these before lexing)
        char tab = '\t';
        char quote = '\'';

        String textBlock = """
                Text blocks (Java 15+) keep line breaks
                without \\n, and are still just String.""";

        System.out.println("── char and String literals ──");
        System.out.println("'A'          = " + letter);
        System.out.println("'\\u0041'     = " + unicode + "   <- identical to 'A'");
        System.out.println("escapes: \\t \\n \\\\ \\' \\\" and tab is [" + tab + "]");
        System.out.println("quote char   = " + quote);
        System.out.println("text block:");
        System.out.println(textBlock);
        System.out.println();
    }

    /**
     * String literals live in the string pool, so identical literals are the
     * SAME object. Crucially, a concatenation of compile-time constants is
     * folded by javac into a literal — so whether `==` succeeds depends on
     * whether the parts were final.
     */
    private static void stringInterning() {
        String a = "hello";
        String b = "hello";                 // same pooled object as a
        String c = new String("hello");     // explicit new -> different object

        final String CONST_PART = "hel";    // compile-time constant
        String folded = CONST_PART + "lo";  // folded to "hello" at compile time

        String varPart = "hel";             // not final -> not a constant
        String notFolded = varPart + "lo";  // built at runtime

        System.out.println("── String pool ──");
        System.out.println("\"hello\" == \"hello\"           : " + (a == b) + "   <- same pooled literal");
        System.out.println("\"hello\" == new String(...)   : " + (a == c) + "  <- new always allocates");
        System.out.println("\"hello\" == final \"hel\"+\"lo\"  : " + (a == folded) + "   <- constant-folded");
        System.out.println("\"hello\" == var \"hel\"+\"lo\"    : " + (a == notFolded) + "  <- built at runtime");
        System.out.println();
        System.out.println("Always compare Strings with .equals() — == compares identity.");
        System.out.println("a.equals(c) = " + a.equals(c));
    }
}
