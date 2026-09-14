/**
 * Day 32, part 2 — using the constructors, and the order things happen in.
 *
 * Compile with the class:
 *     javac Rectangle.java ConstructorDemo.java && java ConstructorDemo
 *
 * @author  Divesh Agarwal
 * @since   2026-09-14
 */
public class ConstructorDemo {

    public static void main(String[] args) {
        System.out.println("Day 32 - Constructors");
        System.out.println();

        defaultAndParameterised();
        chainingRunsTheBodyOnce();
        copyConstructor();
        validationLivesInOnePlace();
        initialisationOrder();
    }

    private static void defaultAndParameterised() {
        Rectangle unit = new Rectangle();
        Rectangle square = new Rectangle(3);
        Rectangle wide = new Rectangle(4, 2);
        Rectangle door = new Rectangle(4, 2.5, "door");

        System.out.println("-- Default and parameterised --");
        System.out.println("  new Rectangle()                -> " + unit + "   area " + unit.area());
        System.out.println("  new Rectangle(3)               -> " + square + "   area " + square.area());
        System.out.println("  new Rectangle(4, 2)            -> " + wide + "   area " + wide.area());
        System.out.println("  new Rectangle(4, 2.5, \"door\")  -> " + door + "   area " + door.area());
        System.out.println();
        System.out.println("  Four constructors, one class. They are OVERLOADS (Day 27),");
        System.out.println("  chosen by the arguments at the `new` expression.");
        System.out.println();
    }

    /**
     * new Rectangle() passes through three constructors, yet `created` rises
     * by exactly one — the counting line only exists in the final one.
     */
    private static void chainingRunsTheBodyOnce() {
        int before = Rectangle.getCreated();
        new Rectangle();
        int after = Rectangle.getCreated();

        System.out.println("-- Chaining with this(...) --");
        System.out.println("  new Rectangle()");
        System.out.println("    -> this(1.0, 1.0)");
        System.out.println("      -> this(1.0, 1.0, \"rect\")   <- assigns and validates");
        System.out.println();
        System.out.println("  objects created before: " + before + ", after: " + after
                + "   (+" + (after - before) + ")");
        System.out.println();
        System.out.println("  Three constructors ran, one object was made, and the");
        System.out.println("  counting line ran once. Put side effects and validation");
        System.out.println("  in ONE constructor and chain the rest to it; copying the");
        System.out.println("  body into each is how overloads drift apart (Day 27).");
        System.out.println();
    }

    private static void copyConstructor() {
        Rectangle original = new Rectangle(5, 3, "panel");
        Rectangle copy = new Rectangle(original);

        System.out.println("-- Copy constructor --");
        System.out.println("  original = " + original);
        System.out.println("  copy     = " + copy);
        System.out.println("  copy == original : " + (copy == original) + "   <- a separate object");
        System.out.println();
        System.out.println("  Contrast with `Rectangle alias = original;`, which copies");
        System.out.println("  only the reference (Day 31). A copy constructor makes a");
        System.out.println("  new object — and because it chains, the copy is validated");
        System.out.println("  exactly like an original.");
        System.out.println();
    }

    private static void validationLivesInOnePlace() {
        System.out.println("-- Validation, reached from every entry point --");
        tryToBuild("new Rectangle(-2)", () -> new Rectangle(-2));
        tryToBuild("new Rectangle(4, 0)", () -> new Rectangle(4, 0));
        tryToBuild("new Rectangle(4, 2, \"  \")", () -> new Rectangle(4, 2, "  "));
        System.out.println();
        System.out.println("  Three different constructors, one set of checks. No");
        System.out.println("  invalid Rectangle can exist, whichever way in you use.");
        System.out.println();
    }

    private static void tryToBuild(String label, Runnable attempt) {
        try {
            attempt.run();
            System.out.println("  " + label + " -> built (unexpected)");
        } catch (IllegalArgumentException e) {
            System.out.println("  " + label + " -> " + e.getMessage());
        }
    }

    /** A class whose every initialisation step announces itself. */
    static class Widget {

        static {
            System.out.println("    1. static block            (once, when the class loads)");
        }

        private int a = log("    2. field initialiser a", 1);

        {
            System.out.println("    3. instance initialiser block");
        }

        private int b = log("    4. field initialiser b", 2);

        Widget() {
            System.out.println("    5. constructor body         a=" + a + " b=" + b);
        }

        private static int log(String message, int value) {
            System.out.println(message);
            return value;
        }
    }

    /**
     * The constructor body is the LAST thing to run. Field initialisers and
     * instance blocks run before it, in the order they are written.
     */
    private static void initialisationOrder() {
        System.out.println("-- Initialisation order --");
        System.out.println("  first new Widget():");
        new Widget();
        System.out.println("  second new Widget():");
        new Widget();
        System.out.println();
        System.out.println("  The static block ran once, for the class. For each object:");
        System.out.println("  field initialisers and instance blocks run in TEXTUAL order");
        System.out.println("  (b's initialiser comes after the block, so it runs after),");
        System.out.println("  and only then the constructor body — which is why the body");
        System.out.println("  can already see a=1 and b=2.");
    }
}
