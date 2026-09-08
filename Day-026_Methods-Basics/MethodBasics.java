/**
 * Day 26 — writing your first method.
 *
 * Every earlier day used methods to organise its own output; this is the day
 * they become the subject. A method is a named, reusable block with a declared
 * input and output:
 *
 *     public static int square(int n) { return n * n; }
 *     ──┬───  ──┬─── ─┬─ ──┬───  ─┬──   ────┬─────
 *   modifier  static  │   name  parameter  body
 *                  return type
 *
 * @author  Divesh Agarwal
 * @since   2026-09-08
 */
public class MethodBasics {

    /** An instance field, to make the static/instance distinction concrete. */
    private int callCount = 0;

    public static void main(String[] args) {
        System.out.println("Day 26 - Writing your first method");
        System.out.println();

        anatomy();
        returnTypes();
        staticVersusInstance();
        parametersAndScope();
        theCompilerChecksYourReturns();
        whyMethodsAtAll();
    }

    // ── the methods being demonstrated ─────────────────────────────────────

    /** Takes an int, returns an int. The simplest useful shape. */
    static int square(int n) {
        return n * n;
    }

    /** Two parameters. Arguments are matched by POSITION, not by name. */
    static int power(int base, int exponent) {
        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }

    /** void: does something, returns nothing. `return;` may still be used to exit early. */
    static void describe(int n) {
        if (n < 0) {
            System.out.println("    " + n + " is negative");
            return;                          // bare return — exits, yields nothing
        }
        System.out.println("    " + n + " squared is " + square(n));
    }

    /** A boolean-returning method reads best when named as a question. */
    static boolean isEven(int n) {
        return n % 2 == 0;                   // the condition IS the answer (Day 10)
    }

    /** An INSTANCE method: needs an object, and can touch instance fields. */
    int recordCall() {
        callCount++;                         // no `static` — this belongs to an object
        return callCount;
    }

    // ── the walkthrough ────────────────────────────────────────────────────

    private static void anatomy() {
        System.out.println("-- Anatomy --");
        System.out.println("  static int square(int n) { return n * n; }");
        System.out.println("   ──┬──  ─┬─ ──┬──  ─┬──     ────┬─────");
        System.out.println("  modifier │  name  parameter    body");
        System.out.println("        return type");
        System.out.println();
        System.out.println("  square(5)      = " + square(5));
        System.out.println("  power(2, 10)   = " + power(2, 10));
        System.out.println("  isEven(7)      = " + isEven(7));
        System.out.println();
        System.out.println("  PARAMETER is the variable in the declaration (int n).");
        System.out.println("  ARGUMENT is the value passed at the call site (5).");
        System.out.println("  They match by POSITION — power(2, 10) is 2^10, not 10^2.");
        System.out.println("    power(2, 10) = " + power(2, 10) + "     power(10, 2) = " + power(10, 2));
        System.out.println();
    }

    private static void returnTypes() {
        System.out.println("-- Return types --");
        System.out.print("  void method, negative input:");
        System.out.println();
        describe(-3);
        System.out.print("  void method, positive input:");
        System.out.println();
        describe(6);
        System.out.println();
        System.out.println("  A void method may still write `return;` with no value,");
        System.out.println("  to leave early. A guard clause like that often replaces");
        System.out.println("  a whole level of nesting.");
        System.out.println();
    }

    /**
     * The distinction that produces the most common first-week compile error.
     * main is static, so it can call other static methods directly — but an
     * instance method needs an object first.
     */
    private static void staticVersusInstance() {
        System.out.println("-- static vs instance --");
        System.out.println("  static   belongs to the CLASS  -> call as MethodBasics.square(5)");
        System.out.println("  instance belongs to an OBJECT  -> call as obj.recordCall()");
        System.out.println();

        MethodBasics instanceOne = new MethodBasics();
        MethodBasics instanceTwo = new MethodBasics();

        System.out.println("  two separate objects, separate state:");
        System.out.println("    instanceOne.recordCall() -> " + instanceOne.recordCall());
        System.out.println("    instanceOne.recordCall() -> " + instanceOne.recordCall());
        System.out.println("    instanceTwo.recordCall() -> " + instanceTwo.recordCall()
                + "   <- its own counter");
        System.out.println();
        System.out.println("  Calling recordCall() directly from main does NOT compile:");
        System.out.println("    error: non-static method recordCall() cannot be");
        System.out.println("           referenced from a static context");
        System.out.println();
        System.out.println("  The error means \"which object's callCount did you mean?\"");
        System.out.println("  There is no answer, because main is not running on one.");
        System.out.println();
    }

    /** Locals live only inside their method; parameters are locals too. */
    private static void parametersAndScope() {
        int local = 10;
        int result = shadowDemo(local);

        System.out.println("-- Scope --");
        System.out.println("  local before call: " + local);
        System.out.println("  shadowDemo(local) returned " + result);
        System.out.println("  local after call:  " + local + "   <- untouched");
        System.out.println();
        System.out.println("  A parameter is a fresh local variable initialised from the");
        System.out.println("  argument. Assigning to it changes only the copy — the");
        System.out.println("  subject of PassByValue.");
        System.out.println();
    }

    private static int shadowDemo(int value) {
        value = value * 100;                 // modifies the local copy only
        return value;
    }

    /** javac enforces four rules about returning, all as hard errors. */
    private static void theCompilerChecksYourReturns() {
        System.out.println("-- What javac enforces --");
        System.out.println("  1. every path of a non-void method must return");
        System.out.println("       static int f(int n) { if (n > 0) return 1; }");
        System.out.println("       error: missing return statement");
        System.out.println();
        System.out.println("  2. nothing may follow a return in the same block");
        System.out.println("       return 1; System.out.println(\"never\");");
        System.out.println("       error: unreachable statement");
        System.out.println();
        System.out.println("  3. a void method may not return a value");
        System.out.println("       static void f() { return 1; }");
        System.out.println("       error: incompatible types: unexpected return value");
        System.out.println();
        System.out.println("  4. an instance method needs an instance");
        System.out.println("       error: non-static method ... cannot be referenced");
        System.out.println("              from a static context");
        System.out.println();
        System.out.println("  All four are compile errors, not runtime surprises —");
        System.out.println("  unusually friendly, compared with most of Ch 6-9.");
        System.out.println();
    }

    private static void whyMethodsAtAll() {
        System.out.println("-- Why bother --");
        System.out.println("  A method gives a name to an idea, so the caller reads as");
        System.out.println("  intent rather than mechanism:");
        System.out.println();
        System.out.println("    if (n % 2 == 0) ...        mechanism");
        System.out.println("    if (isEven(n)) ...         intent");
        System.out.println();
        System.out.println("  It also gives you one place to fix a bug, and one place to");
        System.out.println("  test. Day 20's readIntInRange and Day 21's sort are both");
        System.out.println("  reusable precisely because they were extracted.");
    }
}
