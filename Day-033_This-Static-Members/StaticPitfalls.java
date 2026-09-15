/**
 * Day 33, part 3 — five ways static members surprise you.
 *
 * All five come from one fact: a static member belongs to the CLASS, and the
 * compiler resolves it from the DECLARED type — never from an object.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-15
 */
public class StaticPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 33 - Static pitfalls");
        System.out.println();

        trap1_staticThroughNull();
        trap2_staticMethodsAreHiddenNotOverridden();
        trap3_sharedStateThroughAnInstance();
        trap4_localShadowsAStaticField();
        trap5_whatTheCompilerRejects();
    }

    static class Config {
        static String version = "2.1";

        static String describe() {
            return "Config version " + version;
        }
    }

    /**
     * TRAP 1. A static call through a variable uses only the variable's TYPE.
     * The variable's value is never examined — so it may be null.
     */
    @SuppressWarnings("static")
    private static void trap1_staticThroughNull() {
        Config nothing = null;

        System.out.println("-- Trap 1: static access through null does not throw --");
        System.out.println("  Config nothing = null;");
        System.out.println("  nothing.describe() -> " + nothing.describe());
        System.out.println("  nothing.version    -> " + nothing.version);
        System.out.println("  no NullPointerException");
        System.out.println();
        System.out.println("  javac rewrites nothing.describe() as Config.describe() at");
        System.out.println("  compile time, so the null is never dereferenced.");
        System.out.println();
        System.out.println("  It compiles cleanly. With -Xlint:static:");
        System.out.println("    warning: [static] static method should be qualified by");
        System.out.println("             type name, Config, instead of by an expression");
        System.out.println();
        System.out.println("  Always call statics as ClassName.member(). Calling them");
        System.out.println("  through a variable suggests the object matters. It doesn't.");
        System.out.println();
    }

    static class Parent {
        static String who() {
            return "Parent.who (static)";
        }

        String name() {
            return "Parent.name (instance)";
        }
    }

    static class Child extends Parent {
        static String who() {                 // HIDES Parent.who
            return "Child.who (static)";
        }

        @Override
        String name() {                       // OVERRIDES Parent.name
            return "Child.name (instance)";
        }
    }

    /**
     * TRAP 2. A subclass static method with the same signature does not
     * override — it HIDES. Which one runs depends on the declared type,
     * decided at compile time, exactly like overload resolution (Day 27).
     */
    @SuppressWarnings("static")
    private static void trap2_staticMethodsAreHiddenNotOverridden() {
        Parent p = new Child();               // declared Parent, actually Child

        System.out.println("-- Trap 2: static methods are hidden, not overridden --");
        System.out.println("  Parent p = new Child();");
        System.out.println("  p.name() -> " + p.name() + "   <- runtime type decides");
        System.out.println("  p.who()  -> " + p.who() + "      <- declared type decides");
        System.out.println();
        System.out.println("  Same object, same call syntax, different rules. Instance");
        System.out.println("  methods dispatch on the object; static methods are fixed");
        System.out.println("  at compile time by the variable's type.");
        System.out.println();
        System.out.println("  javac enforces the difference:");
        System.out.println("    @Override on a static method");
        System.out.println("      error: static methods cannot be annotated with @Override");
        System.out.println("    an instance method matching a parent's static one");
        System.out.println("      error: who() in Child cannot override who() in Parent");
        System.out.println("        overridden method is static");
        System.out.println();
    }

    static class Visitor {
        static int total = 0;                 // one for the class
        int mine = 0;                         // one per object

        void visit() {
            total++;
            mine++;
        }
    }

    /**
     * TRAP 3. Writing a static field through one instance changes it for every
     * instance, because there is only one.
     */
    @SuppressWarnings("static")
    private static void trap3_sharedStateThroughAnInstance() {
        Visitor alice = new Visitor();
        Visitor bob = new Visitor();

        alice.visit();
        alice.visit();
        bob.visit();

        System.out.println("-- Trap 3: one instance changes a static for all --");
        System.out.println("  alice visits twice, bob once:");
        System.out.println("    alice.mine=" + alice.mine + "  bob.mine=" + bob.mine);
        System.out.println("    alice.total=" + alice.total + "  bob.total=" + bob.total
                + "  Visitor.total=" + Visitor.total);

        alice.total = 100;

        System.out.println("  alice.total = 100;");
        System.out.println("    bob.total=" + bob.total + "  Visitor.total=" + Visitor.total
                + "   <- bob changed too");
        System.out.println();
        System.out.println("  `alice.total = 100` reads as if it concerns alice. It");
        System.out.println("  rewrote the one field every Visitor shares. Mutable static");
        System.out.println("  state is effectively a global variable, with all the same");
        System.out.println("  problems — including between threads.");
        System.out.println();
    }

    static class Registry {
        static int count = 5;

        static int withLocal() {
            int count = 99;                   // a LOCAL shadows the static field
            return count + Registry.count;    // qualify with the class name
        }
    }

    /**
     * TRAP 4. Day 31 showed a parameter shadowing an instance field, fixed
     * with `this.`. A local can shadow a static field too — and `this.` is
     * not the fix there, because a static method has no `this`.
     */
    private static void trap4_localShadowsAStaticField() {
        System.out.println("-- Trap 4: a local shadows a static field --");
        System.out.println("  static int count = 5;");
        System.out.println("  static int withLocal() { int count = 99; return count + Registry.count; }");
        System.out.println("  withLocal() -> " + Registry.withLocal() + "   (99 local + 5 static)");
        System.out.println();
        System.out.println("  Inside the method, bare `count` is the local. The static");
        System.out.println("  field is reached through the class name, Registry.count —");
        System.out.println("  there is no `this` to use in a static method.");
        System.out.println();
    }

    private static void trap5_whatTheCompilerRejects() {
        System.out.println("-- Trap 5: what javac refuses --");
        System.out.println("  `this` inside a static method");
        System.out.println("    error: non-static variable this cannot be referenced");
        System.out.println("           from a static context");
        System.out.println();
        System.out.println("  an instance field used from a static method");
        System.out.println("    error: non-static variable count cannot be referenced");
        System.out.println("           from a static context");
        System.out.println();
        System.out.println("  assigning to this");
        System.out.println("    this = other;");
        System.out.println("    error: cannot assign to 'this'");
        System.out.println();
        System.out.println("  The first two are Day 26's static-context error, reached");
        System.out.println("  through a field and through `this` itself. The third is why");
        System.out.println("  `this` is safe to rely on: inside a method it can never be");
        System.out.println("  repointed at a different object.");
    }
}
