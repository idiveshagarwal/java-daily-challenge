/**
 * Day 33 — the `this` keyword, and instance vs static members.
 *
 * `this` is a reference to the object a method was called ON. Every instance
 * method receives it as a hidden first argument: `alice.addPoints(5)` is, in
 * effect, `addPoints(alice, 5)`. Static methods receive no such argument —
 * which is the whole reason `this` cannot appear in them.
 *
 * This class uses `this` in all three of its roles, and pairs per-object
 * state with per-class state:
 *
 *     instance    id, name, score, partner   one copy PER OBJECT
 *     static      nextId, online             one copy for the CLASS
 *
 * @author  Divesh Agarwal
 * @since   2026-09-15
 */
public class Player {

    // ── static: shared by every Player ─────────────────────────────────────

    /** Hands out ids. Shared, so no two players ever receive the same one. */
    private static int nextId = 1;

    /** How many players exist right now. */
    private static int online = 0;

    // ── instance: one copy per Player ──────────────────────────────────────

    private final int id;
    private final String name;
    private int score;
    private Player partner;

    public Player(String name) {
        // ROLE 1 — disambiguation: the parameter shadows the field (Day 31)
        this.name = name;

        // static state feeding instance state: read shared, store per object
        this.id = nextId++;
        online++;
    }

    /**
     * ROLE 2 — returning `this` enables fluent chaining:
     *     alice.addPoints(10).addPoints(5).addPoints(3)
     * Each call hands back the same object, ready for the next call. This is
     * exactly how StringBuilder.append works (Day 25).
     */
    public Player addPoints(int points) {
        this.score += points;
        return this;
    }

    /**
     * ROLE 3 — passing `this` as a value. The other player needs a reference
     * to the current object, and `this` is that reference.
     */
    public Player pairWith(Player other) {
        this.partner = other;
        other.partner = this;
        return this;
    }

    public boolean isPartnerOf(Player other) {
        return this.partner == other;
    }

    /**
     * The same operation written as a static method, to make the hidden
     * parameter visible: with no `this`, the object has to be passed in.
     */
    public static Player addPointsTo(Player self, int points) {
        self.score += points;
        return self;
    }

    public void logout() {
        online--;
    }

    /** A static factory: needs no object, so it is static. */
    public static Player guest() {
        return new Player("guest");
    }

    public static int getOnline() {
        return online;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Player#" + id + "[" + name + ", score=" + score + "]";
    }
}
