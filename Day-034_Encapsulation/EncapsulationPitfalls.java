import java.util.ArrayList;
import java.util.List;

/**
 * Day 34, part 3 — what `private` does and does not buy you.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-16
 */
public class EncapsulationPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 34 - Encapsulation pitfalls");
        System.out.println();

        trap1_privateIsPerClassNotPerObject();
        trap2_theLeakyGetter();
        trap3_theLeakyConstructor();
        trap4_recordsLeakToo();
        trap5_accessModifiers();
    }

    static class Wallet {
        private int balance;

        Wallet(int balance) {
            this.balance = balance;
        }

        /**
         * Reads ANOTHER Wallet's private field. This compiles, because
         * `private` means "visible inside this class", not "visible inside
         * this object".
         */
        int compareWith(Wallet other) {
            return Integer.compare(this.balance, other.balance);
        }

        int peekAt(Wallet other) {
            return other.balance;
        }
    }

    private static void trap1_privateIsPerClassNotPerObject() {
        Wallet mine = new Wallet(100);
        Wallet yours = new Wallet(250);

        System.out.println("-- Trap 1: private is per CLASS, not per object --");
        System.out.println("  mine.peekAt(yours)    = " + mine.peekAt(yours)
                + "   <- read another object's private field");
        System.out.println("  mine.compareWith(yours) = " + mine.compareWith(yours));
        System.out.println();
        System.out.println("  Any method of Wallet can touch any Wallet's privates. That");
        System.out.println("  is what makes equals, compareTo and copy constructors");
        System.out.println("  possible without accessors — and it means `private` is a");
        System.out.println("  boundary around the CLASS, not around the instance.");
        System.out.println();
    }

    static class Leaky {
        private final List<String> items = new ArrayList<>(List.of("a", "b"));

        List<String> getItems() {
            return items;                          // hands out the real list
        }

        int size() {
            return items.size();
        }
    }

    private static void trap2_theLeakyGetter() {
        Leaky leaky = new Leaky();

        System.out.println("-- Trap 2: the leaky getter --");
        System.out.println("  private final List<String> items;");
        System.out.println("  List<String> getItems() { return items; }");
        System.out.println();
        System.out.println("  size before        : " + leaky.size());
        leaky.getItems().add("injected");
        leaky.getItems().clear();
        System.out.println("  after caller ran add() then clear() on the getter result:");
        System.out.println("  size after         : " + leaky.size());
        System.out.println();
        System.out.println("  `private` and `final` are both intact. final froze the");
        System.out.println("  REFERENCE, not the list (Day 6), and the getter handed the");
        System.out.println("  reference out. Return a copy or an unmodifiable view.");
        System.out.println();
    }

    static class Holder {
        private final List<String> items;

        Holder(List<String> items) {
            this.items = items;                    // stores the caller's list
        }

        int size() {
            return items.size();
        }
    }

    private static void trap3_theLeakyConstructor() {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        Holder holder = new Holder(source);

        System.out.println("-- Trap 3: the leaky constructor --");
        System.out.println("  size at construction : " + holder.size());
        source.add("added later");
        source.add("and again");
        System.out.println("  caller adds to the list it passed in");
        System.out.println("  size now             : " + holder.size());
        System.out.println();
        System.out.println("  The object never exposed anything. The caller simply kept");
        System.out.println("  the reference it already had. Copy on the way IN as well as");
        System.out.println("  on the way out.");
        System.out.println();
    }

    record Team(String name, List<String> members) {
    }

    record SafeTeam(String name, List<String> members) {
        SafeTeam {
            members = List.copyOf(members);        // compact constructor copies
        }
    }

    /**
     * TRAP 4. A record gives you final fields and accessors automatically, and
     * people assume that makes it immutable. It makes the REFERENCES final.
     */
    private static void trap4_recordsLeakToo() {
        List<String> source = new ArrayList<>(List.of("ana", "ben"));
        Team team = new Team("red", source);

        System.out.println("-- Trap 4: records leak too --");
        source.add("via the original list");
        System.out.println("  after mutating the source list : " + team.members());
        team.members().add("via the accessor");
        System.out.println("  after mutating via accessor    : " + team.members());
        System.out.println();

        List<String> source2 = new ArrayList<>(List.of("ana", "ben"));
        SafeTeam safe = new SafeTeam("blue", source2);
        source2.add("ignored");

        System.out.println("  with a compact constructor doing List.copyOf:");
        System.out.println("    after mutating the source    : " + safe.members());
        try {
            safe.members().add("nope");
        } catch (UnsupportedOperationException e) {
            System.out.println("    accessor add()               -> UnsupportedOperationException");
        }
        System.out.println();
        System.out.println("  A record is shallowly immutable: you cannot reassign the");
        System.out.println("  field, but you can still change what it points at. Copy in");
        System.out.println("  the compact constructor to close both holes at once —");
        System.out.println("  List.copyOf returns an immutable list, so the accessor is");
        System.out.println("  safe without any extra work.");
        System.out.println();
    }

    private static void trap5_accessModifiers() {
        System.out.println("-- Trap 5: four levels, and the one with no keyword --");
        System.out.println();
        System.out.printf("  %-18s %-8s %-10s %-11s %s%n",
                "modifier", "class", "package", "subclass", "everywhere");
        System.out.printf("  %-18s %-8s %-10s %-11s %s%n", "private", "yes", "no", "no", "no");
        System.out.printf("  %-18s %-8s %-10s %-11s %s%n", "(none)", "yes", "yes", "no", "no");
        System.out.printf("  %-18s %-8s %-10s %-11s %s%n", "protected", "yes", "yes", "yes", "no");
        System.out.printf("  %-18s %-8s %-10s %-11s %s%n", "public", "yes", "yes", "yes", "yes");
        System.out.println();
        System.out.println("  Writing no modifier is not \"default to public\" — it is");
        System.out.println("  package-private, the second row. Compiling a class in a");
        System.out.println("  different package against those fields gives:");
        System.out.println();
        System.out.println("    error: packageOnly is not public in Box;");
        System.out.println("           cannot be accessed from outside package");
        System.out.println("    error: mine has private access in Box");
        System.out.println("    error: openToSubs has protected access in Box");
        System.out.println();
        System.out.println("  Note protected is WIDER than package-private, not narrower:");
        System.out.println("  it adds subclasses to everything the package already had.");
    }
}
