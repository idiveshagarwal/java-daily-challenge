/**
 * Day 5, part 2 — the same two directions, applied to objects.
 *
 * Primitives convert the VALUE. References never touch the object: casting a
 * reference only changes which type the compiler lets you use it through. The
 * object on the heap is unchanged, and its real class is fixed at creation.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-18
 */
public class ReferenceCasting {

    public static void main(String[] args) {
        System.out.println("Day 5 — Reference casting");
        System.out.println();

        upcastIsImplicit();
        downcastIsExplicit();
        downcastCanFailAtRuntime();
        patternMatching();
    }

    /** Upcast: child -> parent. Always safe, so never needs a cast. */
    private static void upcastIsImplicit() {
        Dog dog = new Dog();
        Animal asAnimal = dog;        // implicit upcast

        System.out.println("── Upcast (implicit) ──");
        System.out.println("Declared type : Animal");
        System.out.println("Actual class  : " + asAnimal.getClass().getSimpleName());
        System.out.println("speak()       : " + asAnimal.speak() + "   <- still the Dog override");
        // asAnimal.fetch();  // won't compile — Animal has no fetch()
        System.out.println("The object never changed; only the view of it did.");
        System.out.println();
    }

    /** Downcast: parent -> child. Needs an explicit cast — it may be wrong. */
    private static void downcastIsExplicit() {
        Animal animal = new Dog();    // really a Dog
        Dog backToDog = (Dog) animal; // explicit downcast, valid here

        System.out.println("── Downcast (explicit) ──");
        System.out.println("fetch() after downcast: " + backToDog.fetch());
        System.out.println();
    }

    /**
     * The compiler allows any downcast that is *plausible* by the type
     * hierarchy. Whether it is actually correct is only known at runtime, and a
     * wrong one throws ClassCastException — the reference-world equivalent of
     * silent primitive overflow, except loud.
     */
    private static void downcastCanFailAtRuntime() {
        Animal animal = new Cat();    // really a Cat

        System.out.println("── When a downcast is wrong ──");
        try {
            Dog notADog = (Dog) animal;   // compiles fine; fails now
            System.out.println(notADog.fetch());
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Guarding with instanceof is the fix. Since Java 16 the pattern variable
     * form binds the cast result directly, so the cast is never written twice.
     */
    private static void patternMatching() {
        System.out.println("── Safe downcasting ──");

        for (Animal a : new Animal[] { new Dog(), new Cat(), new Animal() }) {
            if (a instanceof Dog d) {            // test and bind in one step
                System.out.println("Dog    -> " + d.fetch());
            } else if (a instanceof Cat c) {
                System.out.println("Cat    -> " + c.ignore());
            } else {
                System.out.println("Animal -> " + a.speak());
            }
        }
    }
}

class Animal {
    String speak() {
        return "...";
    }
}

class Dog extends Animal {
    @Override
    String speak() {
        return "Woof";
    }

    String fetch() {
        return "ball retrieved";
    }
}

class Cat extends Animal {
    @Override
    String speak() {
        return "Meow";
    }

    String ignore() {
        return "request noted, ignoring";
    }
}
