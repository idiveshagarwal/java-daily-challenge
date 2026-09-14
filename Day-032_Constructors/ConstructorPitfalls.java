/**
 * Day 32, part 3 — constructor traps.
 *
 * Deliberately compiles on JDK 21 as well as 25: the one Java 25 feature
 * discussed here is described rather than used.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-14
 */
public class ConstructorPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 32 - Constructor pitfalls");
        System.out.println();

        trap1_voidMakesItAMethod();
        trap2_thisMustComeFirstExceptOn25();
        trap3_cyclicChaining();
        trap4_finalOnEveryPath();
        trap5_overridableCallInConstructor();
    }

    /** A "constructor" with a return type. */
    static class Box {
        int size = -1;

        public void Box(int size) {       // `void` -> this is a METHOD named Box
            this.size = size;
        }
    }

    /**
     * TRAP 1. A constructor has no return type — not even void. Add one and it
     * silently becomes an ordinary method that happens to share the class name.
     */
    private static void trap1_voidMakesItAMethod() {
        Box b = new Box();                // still works: javac supplied Box()

        System.out.println("-- Trap 1: void turns a constructor into a method --");
        System.out.println("  public void Box(int size) { this.size = size; }");
        System.out.println();
        System.out.println("  new Box()   -> size " + b.size + "   <- the \"constructor\" never ran");
        b.Box(5);
        System.out.println("  b.Box(5)    -> size " + b.size + "    <- callable as a method, on an object");
        System.out.println();
        System.out.println("  It compiles, and -Xlint:all is silent. Because the class");
        System.out.println("  now declares no constructor, javac also supplies a default");
        System.out.println("  one — so new Box() works and new Box(5) does not.");
        System.out.println();
    }

    /**
     * TRAP 2 — and a rule that changed. For thirty years, this(...) or
     * super(...) had to be the very first statement. Java 25 finalised
     * "flexible constructor bodies", allowing statements before it.
     */
    private static void trap2_thisMustComeFirstExceptOn25() {
        System.out.println("-- Trap 2: this(...) must come first -- except on Java 25 --");
        System.out.println("  T() { System.out.println(\"hi\"); this(5); }");
        System.out.println();
        System.out.println("  javac --release 21:");
        System.out.println("    error: flexible constructors is not supported in -source 21");
        System.out.println("  javac on 25:");
        System.out.println("    compiles, prints \"hi\", then chains - verified");
        System.out.println();
        System.out.println("  What is still forbidden before this(...) on 25 is touching");
        System.out.println("  the object that does not exist yet:");
        System.out.println("    reading a field    error: cannot reference x before");
        System.out.println("                              supertype constructor has been called");
        System.out.println("    calling a method   error: cannot reference helper() before");
        System.out.println("                              supertype constructor has been called");
        System.out.println();
        System.out.println("  So the textbook rule is out of date on your JDK. The useful");
        System.out.println("  case is validating or computing arguments BEFORE chaining.");
        System.out.println("  This file keeps to the old style so it still compiles on 21.");
        System.out.println();
    }

    private static void trap3_cyclicChaining() {
        System.out.println("-- Trap 3: constructors that chain in a circle --");
        System.out.println("  C()      { this(1); }");
        System.out.println("  C(int x) { this(); }");
        System.out.println();
        System.out.println("  error: recursive constructor invocation");
        System.out.println();
        System.out.println("  Unlike a recursive METHOD (Day 29), which fails at runtime");
        System.out.println("  with StackOverflowError, javac catches a constructor cycle");
        System.out.println("  at compile time - the call graph is fully static.");
        System.out.println();
    }

    private static void trap4_finalOnEveryPath() {
        System.out.println("-- Trap 4: a final field must be set on EVERY path --");
        System.out.println("  private final int level;");
        System.out.println();
        System.out.println("  one constructor forgets it:");
        System.out.println("    F(String id) { this.id = id; }");
        System.out.println("    error: variable level might not have been initialized");
        System.out.println();
        System.out.println("  one branch forgets it:");
        System.out.println("    F2(boolean flag) { if (flag) { level = 1; } }");
        System.out.println("    error: variable level might not have been initialized");
        System.out.println();
        System.out.println("  javac tracks definite assignment through every constructor");
        System.out.println("  and every branch. This is the check that makes final fields");
        System.out.println("  catch Day 31's shadowing bug at compile time.");
        System.out.println();
    }

    static class Base {
        Base() {
            System.out.println("    Base constructor calls describe(): " + describe());
        }

        String describe() {
            return "base";
        }
    }

    static class Child extends Base {
        private final String constant = "literal";                 // compile-time constant
        private final String computed = String.valueOf("literal"); // not a constant
        private int count = 42;

        @Override
        String describe() {
            return "constant=" + constant + " computed=" + computed + " count=" + count;
        }
    }

    /**
     * TRAP 5. The base-class constructor runs BEFORE the subclass's field
     * initialisers. If it calls a method the subclass overrides, that method
     * runs against an object whose fields are still at their defaults.
     */
    private static void trap5_overridableCallInConstructor() {
        System.out.println("-- Trap 5: calling an overridable method from a constructor --");
        System.out.println("  new Child():");
        Child c = new Child();
        System.out.println("  after construction:");
        System.out.println("    " + c.describe());
        System.out.println();
        System.out.println("  During Base's constructor, Child's initialisers have not run,");
        System.out.println("  so computed is null and count is 0 - a final field reading");
        System.out.println("  null, which is supposed to be impossible.");
        System.out.println();
        System.out.println("  `constant` looks fine only because it is a compile-time");
        System.out.println("  constant, which javac inlines into describe() itself");
        System.out.println("  (Day 6). The field was not ready either.");
        System.out.println();
        System.out.println("  javac -Xlint:this-escape warns for a public top-level class:");
        System.out.println("    warning: [this-escape] possible 'this' escape before");
        System.out.println("             subclass is fully initialized");
        System.out.println("  It did NOT warn for these nested classes, which cannot be");
        System.out.println("  subclassed from outside this file. Rule: a constructor should");
        System.out.println("  only call private, static or final methods.");
    }
}
