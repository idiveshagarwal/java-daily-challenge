import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Day 24 — reversing a string.
 *
 * Four ways to do it, which look equivalent on ASCII and are not equivalent at
 * all once real text is involved. Strings are immutable (Day 23), so every one
 * of them builds a new String.
 *
 * The interesting question is not "how do I reverse a string" but "what is a
 * character?" — Java answers `char`, Unicode answers `code point`, and a human
 * answers `grapheme cluster`. All three differ.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-06
 */
public class StringReversal {

    public static void main(String[] args) {
        System.out.println("Day 24 - Reversing a string");
        System.out.println();

        theFourWays();
        whatIsACharacter();
        unicodeCorrectness();
        whichToUse();
    }

    /** The idiomatic one-liner. */
    public static String withStringBuilder(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    /** Manual two-pointer swap on a char array - O(n), no per-step allocation. */
    public static String withCharArray(String s) {
        char[] chars = s.toCharArray();
        for (int i = 0, j = chars.length - 1; i < j; i++, j--) {
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    /**
     * The version to avoid: quadratic, because each += copies the whole string
     * built so far (Day 23).
     */
    public static String withConcatenation(String s) {
        String result = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            result += s.charAt(i);
        }
        return result;
    }

    /** Recursion - elegant, and a StackOverflowError waiting to happen. */
    public static String withRecursion(String s) {
        if (s.length() <= 1) {
            return s;
        }
        return withRecursion(s.substring(1)) + s.charAt(0);
    }

    /**
     * Reverses by GRAPHEME CLUSTER - what a reader would call a character.
     * BreakIterator knows that a base letter plus its combining marks, or a
     * pair of regional indicators forming a flag, is one unit.
     */
    public static String withGraphemes(String s) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(s);

        List<String> clusters = new ArrayList<>();
        int start = it.first();
        for (int end = it.next(); end != BreakIterator.DONE; start = end, end = it.next()) {
            clusters.add(s.substring(start, end));
        }

        Collections.reverse(clusters);
        return String.join("", clusters);
    }

    private static void theFourWays() {
        String s = "Java";

        System.out.println("-- Four ways, on ASCII --");
        System.out.println("  StringBuilder  " + withStringBuilder(s));
        System.out.println("  char[] swap    " + withCharArray(s));
        System.out.println("  concatenation  " + withConcatenation(s) + "   <- quadratic (Day 23)");
        System.out.println("  recursion      " + withRecursion(s) + "   <- O(n^2) and stack-limited");
        System.out.println();
        System.out.println("  All identical here. That agreement does not survive");
        System.out.println("  contact with non-ASCII text.");
        System.out.println();
    }

    /** Three different answers to "how long is this string?". */
    private static void whatIsACharacter() {
        System.out.println("-- What counts as a character? --");
        System.out.printf("  %-14s %8s %12s %10s%n", "string", "chars", "codepoints", "graphemes");
        report("ab+emoji", EMOJI);
        report("cafe+accent", ACCENT);
        report("a+flag+z", FLAG);

        System.out.println();
        System.out.println("  char       a UTF-16 unit; astral characters take TWO");
        System.out.println("  codepoint  one Unicode value; a combining mark is its own");
        System.out.println("  grapheme   what a reader sees as one character");
        System.out.println();
    }

    private static void report(String label, String s) {
        System.out.printf("  %-14s %8d %12d %10d%n",
                label, s.length(), s.codePointCount(0, s.length()), graphemeCount(s));
    }

    private static int graphemeCount(String s) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(s);
        int n = 0;
        while (it.next() != BreakIterator.DONE) {
            n++;
        }
        return n;
    }

    /** 'a', 'b', then one emoji stored as a surrogate PAIR. */
    private static final String EMOJI = "ab😀";

    /** "cafe" then a COMBINING ACUTE ACCENT that attaches to the preceding e. */
    private static final String ACCENT = "café";

    /** 'a', two regional indicators that render as one flag, then 'z'. */
    private static final String FLAG = "a🇦🇧z";

    /**
     * The result worth knowing. StringBuilder.reverse() is DOCUMENTED to keep
     * surrogate pairs intact, so it beats a hand-written char swap. It still
     * knows nothing about combining marks or emoji sequences.
     */
    private static void unicodeCorrectness() {
        System.out.println("-- Reversing non-ASCII --");
        show("ab+emoji", EMOJI);
        show("cafe+accent", ACCENT);
        show("a+flag+z", FLAG);

        System.out.println("  char[] swap splits the surrogate pair into two halves,");
        System.out.println("  neither of which is a character - hence the replacement");
        System.out.println("  glyphs.");
        System.out.println();
        System.out.println("  StringBuilder.reverse() handles surrogate pairs (this is");
        System.out.println("  in its javadoc) but still moves a combining accent onto");
        System.out.println("  the wrong letter, and reverses the two regional-indicator");
        System.out.println("  symbols of a flag - producing a DIFFERENT flag.");
        System.out.println();
    }

    private static void show(String label, String s) {
        System.out.println("  " + label);
        System.out.println("    input         " + s);
        System.out.println("    char[] swap   " + withCharArray(s));
        System.out.println("    StringBuilder " + withStringBuilder(s));
        System.out.println("    by grapheme   " + withGraphemes(s));
        System.out.println();
    }

    private static void whichToUse() {
        System.out.println("-- Which to use --");
        System.out.println("  ASCII or codepoint-safe work : new StringBuilder(s).reverse()");
        System.out.println("  user-visible text            : reverse by grapheme cluster");
        System.out.println("  never                        : += in a loop, or recursion");
        System.out.println();
        System.out.println("  Reversing user-facing text is rare in practice. When it");
        System.out.println("  comes up in an exercise, StringBuilder is the expected");
        System.out.println("  answer - but knowing WHY it is not fully correct is the");
        System.out.println("  more useful thing.");
    }
}
