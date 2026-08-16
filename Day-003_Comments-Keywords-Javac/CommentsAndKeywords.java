/**
 * Day 3 — the three comment forms, reserved vs contextual keywords, and what
 * javac actually does with each.
 *
 * This is a Javadoc comment: a block comment whose opening delimiter has an
 * extra asterisk. The `javadoc` tool reads these to generate HTML docs; the
 * tags below are structured data for that tool.
 *
 * @author  Divesh Agarwal
 * @version 1.0
 * @since   2026-08-16
 */
public class CommentsAndKeywords {

    // ─── Form 1: line comment ───────────────────────────────────────────────
    // Runs from // to the end of the line. Cannot be nested inside itself
    // because there is nothing to nest — the line ending always closes it.

    /*
     * Form 2: block comment. Spans lines, ends at the first closing delimiter.
     * Block comments do NOT nest: a second opener inside this one is just text,
     * and the first close would end the whole thing. That is why commenting out
     * a region that already contains a block comment breaks the build.
     */

    /** Form 3: Javadoc — same as a block comment, but tooling reads it. */
    private static final String LESSON = "Comments, keywords & javac";

    public static void main(String[] args) {
        System.out.println("Day 3 — " + LESSON);
        System.out.println();

        stringsThatLookLikeComments();
        contextualKeywords();
        literalsAreNotKeywords();
    }

    /**
     * The lexer recognises comments only outside string literals. Inside a
     * string, comment delimiters are ordinary characters.
     */
    private static void stringsThatLookLikeComments() {
        String notAComment = "// this is text, not a comment";
        String alsoNot = "/* also just text */";

        System.out.println("Inside a string literal:");
        System.out.println("  " + notAComment);
        System.out.println("  " + alsoNot);
        System.out.println();
    }

    /**
     * Java has 50 reserved keywords that can never be identifiers. A handful of
     * newer words are *contextual*: they have meaning only in certain positions,
     * so they remain legal identifiers everywhere else.
     *
     * Every declaration below compiles. Try renaming one to `class` or `int`
     * and javac rejects it immediately.
     */
    private static void contextualKeywords() {
        int var = 5;         // `var` is a reserved TYPE NAME, not a keyword
        int record = 10;     // `record` only means a type in a declaration
        int sealed = 20;     // `sealed` / `permits` only bind in class headers
        int permits = 30;
        int yield = 40;      // `yield` only binds inside a switch expression

        System.out.println("Contextual keywords used as variable names:");
        System.out.println("  var + record + sealed + permits + yield = "
                + (var + record + sealed + permits + yield));
        System.out.println();
    }

    /**
     * A common exam trap: true, false and null look like keywords and are
     * reserved, but the language spec classifies them as literals.
     */
    private static void literalsAreNotKeywords() {
        boolean flag = true;
        String nothing = null;

        System.out.println("true / false / null are LITERALS, not keywords:");
        System.out.println("  flag = " + flag + ", nothing = " + nothing);

        // `goto` and `const` are reserved but unused — Java claims the words so
        // they cannot be identifiers, yet gives them no meaning. Writing
        // `int goto = 1;` is a compile error even though goto does nothing.
    }
}
