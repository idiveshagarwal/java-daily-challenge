/**
 * Day 32 — constructors: default and parameterised.
 *
 * A constructor runs once, when `new` creates the object, and its job is to
 * leave that object valid. This class offers five ways in, but only ONE of
 * them does the real work — the rest chain to it with this(...).
 *
 *     new Rectangle()                 default: a unit square
 *     new Rectangle(3)                a square
 *     new Rectangle(4, 2.5)           width and height
 *     new Rectangle(4, 2.5, "door")   everything        <- the real one
 *     new Rectangle(existing)         copy constructor
 *
 * @author  Divesh Agarwal
 * @since   2026-09-14
 */
public class Rectangle {

    private final double width;
    private final double height;
    private final String label;

    /** Counts objects, to prove the chained constructors run the body once. */
    private static int created = 0;

    /**
     * The DEFAULT (no-argument) constructor, written explicitly.
     *
     * Java only supplies one for free when a class declares no constructor at
     * all (Day 31). This class declares several, so without this line
     * `new Rectangle()` would not compile.
     */
    public Rectangle() {
        this(1.0, 1.0);
    }

    /** A square. */
    public Rectangle(double side) {
        this(side, side);
    }

    /** Width and height, with a default label. */
    public Rectangle(double width, double height) {
        this(width, height, "rect");
    }

    /**
     * The one constructor that actually assigns fields and validates. Every
     * other constructor ends up here, so the rules exist in exactly one place.
     *
     * Because the fields are final, javac checks that EVERY constructor path
     * assigns them — forget one and it will not compile (ConstructorPitfalls).
     */
    public Rectangle(double width, double height, String label) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "dimensions must be positive: " + width + " x " + height);
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label is required");
        }
        this.width = width;
        this.height = height;
        this.label = label;
        created++;
    }

    /**
     * COPY constructor: builds a new, independent object from an existing one.
     * It chains too, so a copy passes the same validation as an original.
     */
    public Rectangle(Rectangle other) {
        this(other.width, other.height, other.label + "-copy");
    }

    public double area() {
        return width * height;
    }

    public double perimeter() {
        return 2 * (width + height);
    }

    public static int getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return String.format("%s[%.1f x %.1f]", label, width, height);
    }
}
