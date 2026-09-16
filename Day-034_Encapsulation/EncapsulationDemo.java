import java.util.ArrayList;
import java.util.List;

/**
 * Day 34, part 2 — the encapsulated class under attack.
 *
 * Four attempts to put a Course into a state its rules forbid, and the same
 * four against a class that merely has private fields and getters.
 *
 * Compile with the class:
 *     javac Course.java EncapsulationDemo.java && java EncapsulationDemo
 *
 * @author  Divesh Agarwal
 * @since   2026-09-16
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        System.out.println("Day 34 - Encapsulation");
        System.out.println();

        theConstructorCopies();
        theGetterRefusesChanges();
        behaviourInsteadOfSetters();
        derivedStateCannotDrift();
        theLeakyVersionForContrast();
    }

    /** Attack 1: keep a reference to the list you passed in, then add to it. */
    private static void theConstructorCopies() {
        List<String> source = new ArrayList<>(List.of("ana", "ben"));
        Course course = new Course("CS101", 3, source, 4);

        source.add("gatecrasher");               // caller mutates the ORIGINAL

        System.out.println("-- Attack 1: mutate the list you handed in --");
        System.out.println("  source list now  : " + source);
        System.out.println("  course roster    : " + course.getRoster());
        System.out.println("  course           : " + course);
        System.out.println("  the constructor copied the list, so the course never saw it");
        System.out.println();
    }

    /** Attack 2: mutate the collection the getter returns. */
    private static void theGetterRefusesChanges() {
        Course course = new Course("CS101", 3, List.of("ana"), 4);

        System.out.println("-- Attack 2: mutate what the getter returns --");
        System.out.println("  roster before : " + course.getRoster());
        try {
            course.getRoster().add("gatecrasher");
        } catch (UnsupportedOperationException e) {
            System.out.println("  getRoster().add(...) -> UnsupportedOperationException");
        }
        try {
            course.getRoster().clear();
        } catch (UnsupportedOperationException e) {
            System.out.println("  getRoster().clear()  -> UnsupportedOperationException");
        }
        System.out.println("  roster after  : " + course.getRoster() + "   <- unchanged");
        System.out.println();
    }

    /** Attack 3: use the public API to break the capacity and duplicate rules. */
    private static void behaviourInsteadOfSetters() {
        Course course = new Course("CS101", 3, List.of("ana", "ben"), 4);

        System.out.println("-- Attack 3: break the rules through the API --");
        System.out.println("  enrol(\"cara\")  -> " + course.enrol("cara") + "   " + course);
        System.out.println("  enrol(\"dan\")   -> " + course.enrol("dan")
                + "  <- refused, course is full");
        System.out.println("  enrol(\"ana\")   -> " + course.enrol("ana")
                + "  <- refused, already enrolled");

        try {
            course.setCredits(9);
        } catch (IllegalArgumentException e) {
            System.out.println("  setCredits(9)  -> " + e.getMessage());
        }

        System.out.println("  final          : " + course);
        System.out.println();
        System.out.println("  There is no setRoster and no setEnrolled. \"Replace the");
        System.out.println("  whole roster\" is not an operation the rules permit, so it");
        System.out.println("  is not in the API. setCredits exists because it validates.");
        System.out.println();
    }

    /** Derived values cannot disagree with the thing they are derived from. */
    private static void derivedStateCannotDrift() {
        Course course = new Course("CS101", 4, List.of("ana"), 3);

        System.out.println("-- Derived state --");
        System.out.println("  " + course + "  seatsLeft=" + course.getSeatsLeft());
        course.enrol("ben");
        course.enrol("cara");
        System.out.println("  after two enrolments: seatsLeft=" + course.getSeatsLeft()
                + " enrolled=" + course.getEnrolled() + " full=" + course.isFull());
        course.withdraw("ana");
        System.out.println("  after one withdrawal: seatsLeft=" + course.getSeatsLeft()
                + " enrolled=" + course.getEnrolled());
        System.out.println();
        System.out.println("  seatsLeft is computed from the roster, never stored. A");
        System.out.println("  stored copy would need updating in enrol AND withdraw, and");
        System.out.println("  would eventually disagree with reality.");
        System.out.println();
    }

    /** The same class written the common way: private fields, getters, setters. */
    static class LeakyCourse {
        private final List<String> roster;
        private int credits;

        LeakyCourse(List<String> roster, int credits) {
            this.roster = roster;                 // NO COPY: stores the caller's list
            this.credits = credits;
        }

        List<String> getRoster() {
            return roster;                        // NO VIEW: hands out the real list
        }

        void setCredits(int credits) {
            this.credits = credits;               // NO CHECK
        }

        int getCredits() {
            return credits;
        }
    }

    /**
     * Every field private, a getter and setter each — and every guarantee gone.
     */
    private static void theLeakyVersionForContrast() {
        List<String> source = new ArrayList<>(List.of("ana", "ben"));
        LeakyCourse leaky = new LeakyCourse(source, 4);

        System.out.println("-- The same class, written the usual way --");
        System.out.println("  private fields, a getter and a setter for each:");
        System.out.println();

        source.add("gatecrasher");
        System.out.println("  caller mutates the list it passed in -> " + leaky.getRoster());

        leaky.getRoster().clear();
        System.out.println("  caller clears the getter's result     -> " + leaky.getRoster());

        leaky.setCredits(-99);
        System.out.println("  caller sets credits to -99            -> " + leaky.getCredits());

        System.out.println();
        System.out.println("  Three guarantees broken from outside, without touching a");
        System.out.println("  single field directly. `private` protected the FIELD; it");
        System.out.println("  protected nothing about the OBJECT.");
    }
}
