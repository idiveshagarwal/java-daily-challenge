/**
 * Day 25 — StringBuilder.
 *
 * String is immutable (Day 23), which makes repeated modification quadratic.
 * StringBuilder is the mutable counterpart: one growable char buffer that is
 * edited in place, then converted to a String once at the end.
 *
 * Day 23 measured the difference — 22x to 257x depending on size. This day is
 * about the class itself.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-07
 */
public class StringBuilderBasics {

    public static void main(String[] args) {
        System.out.println("Day 25 - StringBuilder");
        System.out.println();

        mutability();
        theEditingMethods();
        chainingReturnsThis();
        appendOverloads();
        practicalPatterns();
    }

    /** The defining difference from String: the object itself changes. */
    private static void mutability() {
        String s = "abc";
        s.concat("def");                     // discarded — String is immutable

        StringBuilder sb = new StringBuilder("abc");
        sb.append("def");                    // the buffer itself changed

        System.out.println("-- Mutability --");
        System.out.println("  String  s.concat(\"def\")  -> s  is still [" + s + "]");
        System.out.println("  Builder sb.append(\"def\") -> sb is now   [" + sb + "]");
        System.out.println();
        System.out.println("  With String you must assign the result. With");
        System.out.println("  StringBuilder the call IS the modification.");
        System.out.println();
    }

    /** The full editing surface — String has no equivalent for most of these. */
    private static void theEditingMethods() {
        StringBuilder sb = new StringBuilder("Hello World");

        System.out.println("-- Editing methods --");
        System.out.println("  start                 [" + sb + "]");

        sb.append("!");
        System.out.println("  append(\"!\")           [" + sb + "]");

        sb.insert(5, ",");
        System.out.println("  insert(5, \",\")        [" + sb + "]");

        sb.replace(0, 5, "Howdy");
        System.out.println("  replace(0, 5, \"Howdy\")[" + sb + "]   (end exclusive, like substring)");

        sb.deleteCharAt(sb.length() - 1);
        System.out.println("  deleteCharAt(last)    [" + sb + "]");

        sb.delete(0, 6);
        System.out.println("  delete(0, 6)          [" + sb + "]");

        sb.setCharAt(0, 'w');
        System.out.println("  setCharAt(0, 'w')     [" + sb + "]");

        sb.reverse();
        System.out.println("  reverse()             [" + sb + "]   (surrogate-safe, Day 24)");

        System.out.println();
        System.out.println("  insert, delete, replace and setCharAt have no String");
        System.out.println("  equivalent at all — String can only produce new strings.");
        System.out.println();
    }

    /**
     * Every mutator returns `this`, which is what makes chaining work. It also
     * means the "result" is the same object, so assigning it creates an ALIAS,
     * not a copy (Day 19's rule again).
     */
    private static void chainingReturnsThis() {
        StringBuilder sb = new StringBuilder();

        String built = sb.append("a").append(1).append(true).append('!').toString();

        StringBuilder first = new StringBuilder("x");
        StringBuilder second = first.append("y");
        second.append("z");

        System.out.println("-- Chaining returns this --");
        System.out.println("  chained  -> " + built);
        System.out.println();
        System.out.println("  StringBuilder second = first.append(\"y\");");
        System.out.println("  second.append(\"z\");");
        System.out.println("    first  = [" + first + "]");
        System.out.println("    second = [" + second + "]");
        System.out.println("    first == second : " + (first == second) + "   <- one object, two names");
        System.out.println();
        System.out.println("  There is no copy constructor shortcut: to branch, use");
        System.out.println("  new StringBuilder(existing).");
        System.out.println();
    }

    /**
     * append is overloaded for every primitive plus Object and CharSequence.
     * Two of those overloads surprise people.
     */
    private static void appendOverloads() {
        String nullString = null;
        Object nullObject = null;

        System.out.println("-- append overloads --");
        System.out.println("  append(42)           -> " + new StringBuilder().append(42));
        System.out.println("  append(3.5)          -> " + new StringBuilder().append(3.5));
        System.out.println("  append(true)         -> " + new StringBuilder().append(true));
        System.out.println("  append('a')          -> " + new StringBuilder().append('a'));
        System.out.println();
        System.out.println("  Surprise 1 - char arithmetic promotes to int (Day 7):");
        System.out.println("    append('a' + 1)       -> " + new StringBuilder().append('a' + 1)
                + "     <- the int 98, not 'b'");
        System.out.println("    append((char)('a'+1)) -> " + new StringBuilder().append((char) ('a' + 1)));
        System.out.println();
        System.out.println("  Surprise 2 - null is appended as text, not skipped:");
        System.out.println("    append((String) null) -> [" + new StringBuilder().append(nullString)
                + "]  length " + new StringBuilder().append(nullString).length());
        System.out.println("    append((Object) null) -> [" + new StringBuilder().append(nullObject) + "]");
        System.out.println("    No NullPointerException — you get the four characters");
        System.out.println("    n-u-l-l, which is rarely what a caller wanted.");
        System.out.println();
    }

    /** Two idioms worth having ready. */
    private static void practicalPatterns() {
        System.out.println("-- Practical patterns --");

        String[] items = { "red", "green", "blue" };

        StringBuilder joined = new StringBuilder();
        for (String item : items) {
            joined.append(item).append(", ");
        }
        if (joined.length() > 0) {
            joined.setLength(joined.length() - 2);      // drop the trailing ", "
        }
        System.out.println("  trailing separator, trimmed with setLength:");
        System.out.println("    [" + joined + "]");
        System.out.println("    String.join(\", \", items) does this for you: ["
                + String.join(", ", items) + "]");
        System.out.println();

        StringBuilder reused = new StringBuilder("first batch");
        int capacityBefore = reused.capacity();
        reused.setLength(0);                            // empties, KEEPS the buffer
        reused.append("second batch");

        System.out.println("  reuse with setLength(0):");
        System.out.println("    capacity before " + capacityBefore
                + ", after clearing and refilling " + reused.capacity());
        System.out.println("    [" + reused + "]");
        System.out.println("    setLength(0) empties without releasing the array, so a");
        System.out.println("    builder reused in a loop stops reallocating.");
    }
}
