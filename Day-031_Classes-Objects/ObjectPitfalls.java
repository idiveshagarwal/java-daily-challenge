/**
 * Day 31, part 3 — five mistakes a first class invites.
 *
 * The first is the one worth remembering: it compiles, runs, passes a quick
 * glance, and javac -Xlint:all says nothing about it.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-13
 */
public class ObjectPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 31 - Object pitfalls");
        System.out.println();

        trap1_shadowedField();
        trap2_defaultConstructorVanishes();
        trap3_fieldsDefaultLocalsDoNot();
        trap4_defaultToString();
        trap5_nullReference();
    }

    /** A constructor whose parameter shares the field's name. */
    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            name = name;          // BUG: parameter assigned to itself
            this.age = age;       // correct
        }
    }

    /**
     * TRAP 1. Inside the constructor, `name` means the PARAMETER — it shadows
     * the field. So `name = name` copies the parameter onto itself and the
     * field keeps its default, null.
     */
    private static void trap1_shadowedField() {
        Person p = new Person("Divesh", 30);

        System.out.println("-- Trap 1: a shadowed field --");
        System.out.println("  Person(String name, int age) {");
        System.out.println("      name = name;          // meant this.name = name");
        System.out.println("      this.age = age;");
        System.out.println("  }");
        System.out.println();
        System.out.println("  new Person(\"Divesh\", 30)");
        System.out.println("    name = " + p.name + "   <- never set");
        System.out.println("    age  = " + p.age);
        System.out.println();
        System.out.println("  It compiles. It runs. javac -Xlint:all reports NOTHING —");
        System.out.println("  verified on " + System.getProperty("java.version") + ".");
        System.out.println();
        System.out.println("  The null surfaces later, far from the constructor, as a");
        System.out.println("  NullPointerException in some unrelated method.");
        System.out.println();
        System.out.println("  Fix: always write this.field = parameter in constructors.");
        System.out.println();
        System.out.println("  Better: make the field final, and the SAME mistake stops");
        System.out.println("  compiling, because a final field must be assigned:");
        System.out.println("    final String name;");
        System.out.println("    Person(String name) { name = name; }");
        System.out.println("    error: variable name might not have been initialized");
        System.out.println("  The bug moves from a runtime null to a compile error.");
        System.out.println();
    }

    /**
     * TRAP 2. Java supplies a no-argument constructor ONLY if you declare no
     * constructor at all. The moment you write one, the free one is gone.
     */
    private static void trap2_defaultConstructorVanishes() {
        System.out.println("-- Trap 2: the default constructor vanishes --");
        System.out.println("  class Box { int w; }");
        System.out.println("    new Box()   compiles — javac supplied Box()");
        System.out.println();
        System.out.println("  class Box { int w; Box(int w) { this.w = w; } }");
        System.out.println("    new Box()   error: constructor Box in class Box cannot be");
        System.out.println("                applied to given types;");
        System.out.println("                  required: int");
        System.out.println("                  found:    no arguments");
        System.out.println();
        System.out.println("  Adding a constructor silently REMOVES one. Any code that");
        System.out.println("  was calling new Box() breaks. Declare Box() explicitly if");
        System.out.println("  you still want it.");
        System.out.println();
    }

    /** A class with no constructor and no initialisers. */
    static class Defaults {
        int count;
        double price;
        boolean active;
        String label;
    }

    /**
     * TRAP 3. Fields get default values; local variables do not. The same
     * `int x;` means 0 in one place and a compile error in the other.
     */
    private static void trap3_fieldsDefaultLocalsDoNot() {
        Defaults d = new Defaults();

        System.out.println("-- Trap 3: fields default, locals do not --");
        System.out.println("  fields of a new object:");
        System.out.println("    count=" + d.count + "  price=" + d.price
                + "  active=" + d.active + "  label=" + d.label);
        System.out.println();
        System.out.println("  but a local:");
        System.out.println("    int local;  System.out.println(local);");
        System.out.println("    error: variable local might not have been initialized");
        System.out.println();
        System.out.println("  Fields are zeroed when `new` allocates the object, so they");
        System.out.println("  always hold something. Locals live on the stack and javac");
        System.out.println("  insists you assign them first. The field default is the");
        System.out.println("  riskier of the two: a forgotten assignment silently reads");
        System.out.println("  as 0 or null instead of failing to compile.");
        System.out.println();
    }

    /** TRAP 4. Printing an object you have not given a toString. */
    private static void trap4_defaultToString() {
        Person p = new Person("x", 1);

        System.out.println("-- Trap 4: the default toString --");
        System.out.println("  System.out.println(person) -> " + p);
        System.out.println();
        System.out.println("  That is Object.toString(): class name, '@', hash code in");
        System.out.println("  hex. The '$' is because Person is nested inside");
        System.out.println("  ObjectPitfalls. It says nothing about the object's state.");
        System.out.println();
        System.out.println("  Override toString in any class you will ever print or log");
        System.out.println("  — BankAccount does.");
        System.out.println();
    }

    static class Counter {
        int value;

        void increment() {
            value++;
        }
    }

    /**
     * TRAP 5. A reference variable can hold null — no object at all. Calling
     * any method through it throws.
     */
    private static void trap5_nullReference() {
        Counter counter = null;

        System.out.println("-- Trap 5: calling through null --");
        try {
            counter.increment();
        } catch (NullPointerException e) {
            System.out.println("  counter.increment() ->");
            System.out.println("    " + e.getMessage());
        }
        System.out.println();
        System.out.println("  Java's helpful NPE messages name what was null. Whether");
        System.out.println("  you see the variable NAME or a placeholder like <local0>");
        System.out.println("  depends on whether the class was compiled with -g, which");
        System.out.println("  keeps local variable names in the class file.");
    }
}
