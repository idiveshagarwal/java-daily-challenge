/**
 * Day 33, part 2 — using `this`, and watching instance and static state differ.
 *
 * Compile with the class:
 *     javac Player.java ThisAndStaticDemo.java && java ThisAndStaticDemo
 *
 * @author  Divesh Agarwal
 * @since   2026-09-15
 */
public class ThisAndStaticDemo {

    public static void main(String[] args) {
        System.out.println("Day 33 - this, instance and static members");
        System.out.println();

        instanceVersusStatic();
        fluentChaining();
        passingThis();
        theHiddenParameter();
        staticNeedsNoObject();
    }

    private static void instanceVersusStatic() {
        Player alice = new Player("alice");
        Player bob = new Player("bob");

        alice.addPoints(40);
        bob.addPoints(15);

        System.out.println("-- Instance vs static state --");
        System.out.println("  " + alice);
        System.out.println("  " + bob);
        System.out.println("  Player.getOnline() = " + Player.getOnline());
        System.out.println();
        System.out.println("  score and id are INSTANCE fields: each player has its own.");
        System.out.println("  nextId and online are STATIC: one copy, shared by all.");
        System.out.println();
        System.out.println("  The constructor reads the shared nextId and stores the");
        System.out.println("  result in the per-object id — that is how two players");
        System.out.println("  get different ids from one counter.");
        System.out.println();
    }

    /** Each call returns `this`, so the next call acts on the same object. */
    private static void fluentChaining() {
        Player carol = new Player("carol");
        Player returned = carol.addPoints(10).addPoints(5).addPoints(3);

        System.out.println("-- Returning this: fluent chaining --");
        System.out.println("  carol.addPoints(10).addPoints(5).addPoints(3)");
        System.out.println("  -> " + carol);
        System.out.println("  returned == carol : " + (returned == carol)
                + "   <- every call handed back the same object");
        System.out.println();
    }

    /** `this` as an ordinary value, handed to another object. */
    private static void passingThis() {
        Player dev = new Player("dev");
        Player esha = new Player("esha");

        dev.pairWith(esha);

        System.out.println("-- Passing this --");
        System.out.println("  dev.pairWith(esha)  sets  other.partner = this");
        System.out.println("  esha.isPartnerOf(dev) : " + esha.isPartnerOf(dev));
        System.out.println("  dev.isPartnerOf(esha) : " + dev.isPartnerOf(esha));
        System.out.println();
        System.out.println("  Inside pairWith, `this` is dev. Passing it lets esha");
        System.out.println("  store a reference back to dev — both links from one call.");
        System.out.println();
    }

    /**
     * The same effect via an instance method and a static one. The static
     * version has to be told which object to act on, because it has no `this`.
     */
    private static void theHiddenParameter() {
        Player one = new Player("one");
        Player two = new Player("two");

        one.addPoints(7);                        // `this` is one
        Player.addPointsTo(two, 7);              // no `this`: pass the object

        System.out.println("-- this is a hidden parameter --");
        System.out.println("  one.addPoints(7)          -> " + one);
        System.out.println("  Player.addPointsTo(two, 7) -> " + two);
        System.out.println();
        System.out.println("  Same result. An instance method receives the object it");
        System.out.println("  was called on as an invisible argument, named `this`.");
        System.out.println("  A static method receives nothing of the kind, so `this`");
        System.out.println("  has nothing to refer to — hence the compile error.");
        System.out.println();
    }

    private static void staticNeedsNoObject() {
        int before = Player.getOnline();
        Player guest = Player.guest();
        int afterGuest = Player.getOnline();
        guest.logout();

        System.out.println("-- Static members need no object --");
        System.out.println("  online before guest(): " + before);
        System.out.println("  Player.guest()       -> " + guest);
        System.out.println("  online after guest() : " + afterGuest);
        System.out.println("  online after logout(): " + Player.getOnline());
        System.out.println();
        System.out.println("  guest() and getOnline() are called on the CLASS. There is");
        System.out.println("  no player to call them on before one exists — which is");
        System.out.println("  exactly the job a static factory does.");
    }
}
