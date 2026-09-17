import java.util.ArrayList;
import java.util.List;

/**
 * Day 35, part 3 — what `extends` does not do, and what it does behind your back.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-17
 */
public class InheritancePitfalls {

    public static void main(String[] args) {
        System.out.println("Day 35 - Inheritance pitfalls");
        System.out.println();

        trap1_constructorsAreNotInherited();
        trap2_constructionOrder();
        trap3_callingOverridableMethodsFromAConstructor();
        trap4_fieldsAreHiddenNotOverridden();
        trap5_protectedIsNarrowerThanItLooks();
        trap6_oneParentOnly();
    }

    private static void trap1_constructorsAreNotInherited() {
        System.out.println("-- Trap 1: constructors are not inherited --");
        System.out.println("  class Parent { Parent(String n) {} }");
        System.out.println("  class Child extends Parent { }");
        System.out.println();
        System.out.println("  Child gets a default constructor, whose hidden first line is");
        System.out.println("  super() — and Parent has no no-arg constructor:");
        System.out.println();
        System.out.println("    error: constructor Parent in class Parent cannot be applied to given types;");
        System.out.println("      required: String");
        System.out.println("      found:    no arguments");
        System.out.println();
        System.out.println("  And new Child(\"x\") fails too, because Parent(String) did not");
        System.out.println("  become Child(String):");
        System.out.println();
        System.out.println("    error: constructor Child in class Child cannot be applied to given types;");
        System.out.println("      required: no arguments");
        System.out.println("      found:    String");
        System.out.println();
    }

    static String log(String message) {
        System.out.println("    " + message);
        return message;
    }

    static class Base {
        private final String baseField = log("1. Base field initialiser");

        Base() {
            log("2. Base constructor body");
        }
    }

    static class Derived extends Base {
        private final String derivedField = log("3. Derived field initialiser");

        Derived() {
            super();                                     // implicit if omitted
            log("4. Derived constructor body");
        }
    }

    private static void trap2_constructionOrder() {
        System.out.println("-- Trap 2: the parent is built first, all of it --");
        System.out.println("  new Derived() runs:");
        new Derived();
        System.out.println();
        System.out.println("  Derived() is called first, but its super() runs before");
        System.out.println("  Derived's own field initialisers. The child's fields do not");
        System.out.println("  exist in any useful sense until the parent is completely done.");
        System.out.println();
    }

    static class Widget {
        Widget() {
            System.out.println("    Widget constructor calls describe(): " + describe());
        }

        String describe() {
            return "widget";
        }
    }

    static class Label extends Widget {
        private final String text = new String("hello");  // set at runtime
        private final String constantText = "hello";      // compile-time constant
        private final List<String> tags = new ArrayList<>();

        @Override
        String describe() {
            return "text=" + text + " constantText=" + constantText
                    + " tags=" + (tags == null ? "null" : tags.size());
        }
    }

    /**
     * Previews overriding (a later day), because it is the sharpest consequence
     * of trap 2: the parent constructor reaches into a child that has not
     * initialised its fields yet.
     */
    private static void trap3_callingOverridableMethodsFromAConstructor() {
        System.out.println("-- Trap 3: a parent constructor can see a half-built child --");
        Label label = new Label();
        System.out.println("    after construction, describe(): " + label.describe());
        System.out.println();
        System.out.println("  During Widget(), Label's fields are still at their defaults:");
        System.out.println("  text is null and tags is null — even though both are final.");
        System.out.println("  constantText already reads \"hello\" only because javac inlined");
        System.out.println("  the constant into describe() (Day 6); it was not initialised");
        System.out.println("  early. Calling tags.size() there would throw a");
        System.out.println("  NullPointerException. Don't call overridable methods from a");
        System.out.println("  constructor.");
        System.out.println();
    }

    static class Animal {
        String sound = "...";

        String speak() {
            return sound;
        }
    }

    static class Dog extends Animal {
        String sound = "woof";                            // a SECOND field, not a replacement
    }

    private static void trap4_fieldsAreHiddenNotOverridden() {
        Dog dog = new Dog();
        Animal asAnimal = dog;

        System.out.println("-- Trap 4: redeclaring a field makes two fields --");
        System.out.println("  dog.sound        : " + dog.sound);
        System.out.println("  asAnimal.sound   : " + asAnimal.sound + "     <- same object!");
        System.out.println("  dog.speak()      : " + dog.speak() + "     <- Animal's method sees Animal's field");
        System.out.println("  ((Animal) dog).sound == dog.sound : " + (((Animal) dog).sound == dog.sound));
        System.out.println();
        System.out.println("  Dog now carries both fields. Which one you get depends on the");
        System.out.println("  declared type of the expression, not on the object. Methods");
        System.out.println("  can be overridden; fields can only be hidden.");
        System.out.println();
    }

    private static void trap5_protectedIsNarrowerThanItLooks() {
        System.out.println("-- Trap 5: protected, across packages --");
        System.out.println("  package a;  public class Base { protected int secret = 7; }");
        System.out.println("  package b;  public class Sub extends Base {");
        System.out.println("      int mine()             { return this.secret;  }   // ok");
        System.out.println("      int viaSub(Sub other)  { return other.secret; }   // ok");
        System.out.println("      int viaBase(Base other){ return other.secret; }   // error");
        System.out.println("  }");
        System.out.println();
        System.out.println("    error: secret has protected access in Base");
        System.out.println();
        System.out.println("  From another package, a subclass may use protected members of");
        System.out.println("  itself and of its own kind — not of an arbitrary Base, which");
        System.out.println("  might be some unrelated subclass's object.");
        System.out.println();
    }

    private static void trap6_oneParentOnly() {
        System.out.println("-- Trap 6: one parent, and only if the parent allows it --");
        System.out.println("  class C extends A, B {}      -> error: '{' expected");
        System.out.println("  final class A {}");
        System.out.println("  class C extends A {}         -> error: cannot inherit from final A");
        System.out.println();
        System.out.println("  Java classes have exactly one direct superclass. The compiler");
        System.out.println("  doesn't even reach a type error for two: after one name it");
        System.out.println("  expects the class body. String, Integer and every record are");
        System.out.println("  final, so none of them can be a parent.");
        System.out.println("  String is final: " + java.lang.reflect.Modifier.isFinal(String.class.getModifiers()));
    }
}
