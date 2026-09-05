import java.util.Arrays;

/**
 * Day 23, part 2 — four places String behaves unlike its name suggests.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-05
 */
public class StringPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 23 — String pitfalls");
        System.out.println();

        splitTakesARegex();
        splitDropsTrailingEmpties();
        trimIsNotStrip();
        concatenationInALoop();
    }

    /**
     * TRAP 1 — split's argument is a REGULAR EXPRESSION. The characters people
     * most want to split on — . | $ ^ * + ? ( ) [ ] — are all regex
     * metacharacters, so the obvious call does something else entirely.
     */
    private static void splitTakesARegex() {
        System.out.println("── Trap 1: split takes a regex ──");

        String dotted = "a.b.c";
        System.out.println("  \"a.b.c\".split(\".\")    -> " + Arrays.toString(dotted.split("."))
                + "   length " + dotted.split(".").length);
        System.out.println("     `.` matches ANY character, so every character is a");
        System.out.println("     separator and every field is empty — then the trailing");
        System.out.println("     empties are dropped, leaving nothing at all.");
        System.out.println();
        System.out.println("  \"a.b.c\".split(\"\\.\")  -> " + Arrays.toString(dotted.split("\\."))
                + "   <- escaped, correct");
        System.out.println("  Pattern.quote(\".\")     -> "
                + Arrays.toString(dotted.split(java.util.regex.Pattern.quote("."))));
        System.out.println();

        String piped = "a|b";
        System.out.println("  \"a|b\".split(\"|\")      -> " + Arrays.toString(piped.split("|")));
        System.out.println("     `|` is alternation between two EMPTY patterns, which");
        System.out.println("     matches at every position — splitting into characters.");
        System.out.println();
        System.out.println("  Rule: escape the separator, or wrap it in Pattern.quote.");
        System.out.println("  `replace` takes a literal; `replaceAll` takes a regex.");
        System.out.println();
    }

    /**
     * TRAP 2 — split silently discards trailing empty fields, but keeps
     * leading ones. Asymmetric, and it corrupts fixed-width record parsing.
     */
    private static void splitDropsTrailingEmpties() {
        String row = "a,b,,c,,";

        System.out.println("── Trap 2: trailing empty fields vanish ──");
        System.out.println("  \"a,b,,c,,\"");
        System.out.println("    split(\",\")      -> " + Arrays.toString(row.split(","))
                + "     length " + row.split(",").length);
        System.out.println("    split(\",\", -1)  -> " + Arrays.toString(row.split(",", -1))
                + "  length " + row.split(",", -1).length);
        System.out.println();
        System.out.println("  Two fields disappeared. A CSV row with empty trailing");
        System.out.println("  columns comes back SHORT, and the array index you expect");
        System.out.println("  to hold column 6 is out of bounds.");
        System.out.println();

        String leading = ",,a";
        System.out.println("  But LEADING empties are kept:");
        System.out.println("    \",,a\".split(\",\")   -> " + Arrays.toString(leading.split(","))
                + "   length " + leading.split(",").length);
        System.out.println();
        System.out.println("  Always pass limit -1 when the field count matters.");
        System.out.println();
    }

    /**
     * TRAP 3 — trim() predates Unicode awareness: it removes characters <=
     * U+0020. strip() (Java 11+) uses Character.isWhitespace instead.
     *
     * But isWhitespace deliberately EXCLUDES non-breaking spaces, so U+00A0 —
     * the one that arrives from copy-pasted web content — survives both.
     */
    private static void trimIsNotStrip() {
        String em = "\u2003abc\u2003";     // EM SPACE, Unicode whitespace
        String nbsp = "\u00A0abc\u00A0";   // NO-BREAK SPACE

        System.out.println("── Trap 3: trim() is not strip() ──");
        System.out.println("  Both test strings are length " + em.length() + ".");
        System.out.println();
        System.out.println("  EM SPACE U+2003   isWhitespace = " + Character.isWhitespace('\u2003'));
        System.out.println("    trim()  -> length " + em.trim().length() + "   <- NOT removed (trim only strips <= U+0020)");
        System.out.println("    strip() -> length " + em.strip().length() + "   <- removed");
        System.out.println();
        System.out.println("  NBSP     U+00A0   isWhitespace = " + Character.isWhitespace('\u00A0'));
        System.out.println("    trim()  -> length " + nbsp.trim().length());
        System.out.println("    strip() -> length " + nbsp.strip().length() + "   <- removed by NEITHER");
        System.out.println();
        System.out.println("  strip() is not \"removes anything that looks blank\". It");
        System.out.println("  removes what Character.isWhitespace accepts, and that");
        System.out.println("  excludes non-breaking spaces by design. NBSP pasted from");
        System.out.println("  a web page survives both methods.");
        System.out.println();
    }

    /**
     * TRAP 4 — s += x inside a loop is quadratic. Because Strings are
     * immutable, each += allocates a new String and copies everything so far.
     */
    private static void concatenationInALoop() {
        System.out.println("── Trap 4: += in a loop is quadratic ──");
        System.out.printf("  %8s %14s %14s %10s%n", "n", "s += c", "StringBuilder", "ratio");

        for (int n : new int[] { 10_000, 20_000, 40_000 }) {
            long start = System.nanoTime();
            String s = "";
            for (int i = 0; i < n; i++) {
                s += 'x';                          // allocates and copies EVERY time
            }
            long concat = System.nanoTime() - start;

            start = System.nanoTime();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append('x');                    // amortised O(1)
            }
            String built = sb.toString();
            long builder = System.nanoTime() - start;

            System.out.printf("  %8d %11.1f ms %11.3f ms %9.0fx%n",
                    n, concat / 1e6, builder / 1e6, (double) concat / builder);
        }

        System.out.println();
        System.out.println("  Doubling n roughly QUADRUPLES the concat time while the");
        System.out.println("  builder stays flat — the signature of O(n^2) versus O(n).");
        System.out.println();
        System.out.println("  javac optimises `a + b + c` in a single expression into a");
        System.out.println("  StringBuilder for you. It cannot do that across loop");
        System.out.println("  iterations, because each one is a separate statement.");
    }
}
