/**
 * Day 10, part 3 — four ways an `if` silently does the wrong thing.
 *
 * None of these are compile errors. Three produce no warning at all; only the
 * first is caught, and only if you pass -Xlint:all (Day 3).
 *
 * Compile with warnings on to see the difference:
 *     javac -Xlint:all ConditionalTraps.java
 *
 * @author  Divesh Agarwal
 * @since   2026-08-23
 */
public class ConditionalTraps {

    public static void main(String[] args) {
        System.out.println("Day 10 — Conditional traps");
        System.out.println();

        trap1_straySemicolon();
        trap2_missingBraces();
        trap3_danglingElse();
        trap4_assignmentInCondition();
    }

    /**
     * TRAP 1 — a semicolon straight after the condition IS the body: an empty
     * statement. Whatever follows is unguarded.
     *
     * The only one javac warns about: "[empty] empty statement after if".
     */
    private static void trap1_straySemicolon() {
        int x = -5;

        System.out.println("── Trap 1: stray semicolon ──");
        System.out.println("  if (x > 0);");
        System.out.println("      System.out.println(...);   // not guarded");
        System.out.println();

        if (x > 0);                     // <-- empty body
            System.out.println("  x = " + x + " and this printed anyway");

        System.out.println("  javac -Xlint:all reports: [empty] empty statement after if");
        System.out.println();
    }

    /**
     * TRAP 2 — without braces only the FIRST statement belongs to the if.
     * Indentation is not syntax. This is the shape of Apple's 2014 "goto fail"
     * TLS vulnerability.
     */
    private static void trap2_missingBraces() {
        boolean authorised = false;

        System.out.println("── Trap 2: missing braces ──");
        System.out.println("  if (authorised)");
        System.out.println("      log(\"granted\");");
        System.out.println("      grantAccess();     // runs ALWAYS");
        System.out.println();

        if (authorised)
            System.out.println("  log: granted");
            System.out.println("  ACCESS GRANTED  <- ran with authorised = " + authorised);

        System.out.println("  No warning from javac. Braces always.");
        System.out.println();
    }

    /**
     * TRAP 3 — an `else` binds to the NEAREST unmatched `if`, regardless of how
     * it is indented.
     */
    private static void trap3_danglingElse() {
        int p = 5, q = -1;

        System.out.println("── Trap 3: dangling else ──");
        System.out.println("  if (p > 0)");
        System.out.println("      if (q > 0) ...");
        System.out.println("  else ...            // looks like it pairs with the OUTER if");
        System.out.println();

        if (p > 0)
            if (q > 0) {
                System.out.println("  both positive");
            } else {
                System.out.println("  else bound to the INNER if (q > 0), not the outer");
            }

        System.out.println("  p = " + p + " (positive) yet the else branch ran —");
        System.out.println("  proof it belongs to the inner if.");
        System.out.println();
    }

    /**
     * TRAP 4 — `=` instead of `==`. Java rejects this for numeric types, but a
     * boolean assignment is a valid boolean expression, so it compiles.
     */
    private static void trap4_assignmentInCondition() {
        boolean loggedIn = false;

        System.out.println("── Trap 4: = instead of == ──");
        System.out.println("  int:     if (x = 1)     -> compile error, Java protects you");
        System.out.println("  boolean: if (f = true)  -> COMPILES, assigns, always true");
        System.out.println();

        if (loggedIn = true) {          // assignment, not comparison
            System.out.println("  \"logged in\" — but loggedIn started as false");
        }
        System.out.println("  loggedIn is now " + loggedIn + " — the check CHANGED it");
        System.out.println();
        System.out.println("  Prefer `if (flag)` over `if (flag == true)`; there is");
        System.out.println("  then no `=` to mistype.");
    }
}
