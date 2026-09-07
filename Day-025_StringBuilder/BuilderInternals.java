/**
 * Day 25, part 2 — capacity, and the equals trap.
 *
 * StringBuilder wraps a growable array (Day 19), so it has the same two
 * distinct sizes any growable array does: how much is USED, and how much is
 * ALLOCATED. Confusing them is the source of most StringBuilder surprises.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-07
 */
public class BuilderInternals {

    public static void main(String[] args) {
        System.out.println("Day 25 - StringBuilder internals");
        System.out.println();

        lengthIsNotCapacity();
        growthIsGeometric();
        presizingIsMeasurable();
        equalsIsNotOverridden();
        setLengthBothWays();
        builderVersusBuffer();
    }

    /** Two different sizes, and only one of them is the string's length. */
    private static void lengthIsNotCapacity() {
        StringBuilder empty = new StringBuilder();
        StringBuilder fromText = new StringBuilder("abc");
        StringBuilder sized = new StringBuilder(100);

        System.out.println("-- length vs capacity --");
        System.out.printf("  %-26s length %3d   capacity %3d%n",
                "new StringBuilder()", empty.length(), empty.capacity());
        System.out.printf("  %-26s length %3d   capacity %3d   (16 + text length)%n",
                "new StringBuilder(\"abc\")", fromText.length(), fromText.capacity());
        System.out.printf("  %-26s length %3d   capacity %3d%n",
                "new StringBuilder(100)", sized.length(), sized.capacity());
        System.out.println();
        System.out.println("  length()   characters actually present");
        System.out.println("  capacity() slots allocated before the next reallocation");
        System.out.println();
        System.out.println("  The default 16 is why a builder that will hold thousands");
        System.out.println("  of characters reallocates repeatedly if left unsized.");
        System.out.println();
    }

    /** Each growth is 2n+2, so the count of reallocations is logarithmic. */
    private static void growthIsGeometric() {
        StringBuilder sb = new StringBuilder();
        int previous = sb.capacity();

        System.out.println("-- Growth is geometric --");
        System.out.println("  appending one char at a time:");

        for (int i = 0; i < 600; i++) {
            sb.append('x');
            if (sb.capacity() != previous) {
                System.out.printf("    length %4d  capacity %4d -> %4d%n",
                        sb.length(), previous, sb.capacity());
                previous = sb.capacity();
            }
        }

        System.out.println();
        System.out.println("  Each step is 2n+2, not +1, so appending n characters");
        System.out.println("  costs O(log n) reallocations rather than O(n) — which is");
        System.out.println("  exactly why append is amortised O(1) and String += is");
        System.out.println("  quadratic (Day 23).");
        System.out.println();
    }

    /** Presizing removes the copying entirely. Measured, not assumed. */
    private static void presizingIsMeasurable() {
        int n = 2_000_000;
        long growing = 0;
        long presized = 0;

        for (int run = 0; run < 2; run++) {             // run once to warm up
            long start = System.nanoTime();
            StringBuilder grow = new StringBuilder();
            for (int i = 0; i < n; i++) {
                grow.append('x');
            }
            growing = System.nanoTime() - start;

            start = System.nanoTime();
            StringBuilder pre = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                pre.append('x');
            }
            presized = System.nanoTime() - start;
        }

        int reallocations = 0;
        for (int capacity = 16; capacity < n; capacity = capacity * 2 + 2) {
            reallocations++;
        }

        System.out.println("-- Presizing --");
        System.out.printf("  default capacity      %6.1f ms%n", growing / 1e6);
        System.out.printf("  new StringBuilder(n)  %6.1f ms   (%.1fx faster)%n",
                presized / 1e6, (double) growing / presized);
        System.out.println("  reallocations avoided: " + reallocations);
        System.out.println();
        System.out.println("  Each reallocation allocates a bigger array and copies");
        System.out.println("  everything across. If you know the size, say so.");
        System.out.println();
    }

    /**
     * THE trap. StringBuilder does NOT override equals(), so it inherits
     * Object's identity comparison — two builders with identical contents are
     * not equal. But compareTo (Java 11+) DOES compare content, so the two
     * disagree.
     */
    private static void equalsIsNotOverridden() {
        StringBuilder a = new StringBuilder("hi");
        StringBuilder b = new StringBuilder("hi");

        System.out.println("-- equals() is not overridden --");
        System.out.println("  a and b both contain \"hi\"");
        System.out.println("    a.equals(b)                       = " + a.equals(b)
                + "   <- identity, NOT content");
        System.out.println("    a.compareTo(b)                    = " + a.compareTo(b)
                + "       <- compareTo DOES compare content");
        System.out.println("    a.toString().equals(b.toString()) = " + a.toString().equals(b.toString())
                + "    <- the correct test");
        System.out.println();
        System.out.println("  This is worse than String's == pitfall (Day 6), because");
        System.out.println("  equals() is exactly the method you are told to use. Here");
        System.out.println("  it silently means reference identity.");
        System.out.println();
        System.out.println("  Consequence: a StringBuilder is unusable as a HashMap key");
        System.out.println("  or in a Set — every instance is distinct. Call toString()");
        System.out.println("  before storing or comparing.");
        System.out.println();
    }

    /** setLength truncates or pads, and never shrinks the buffer. */
    private static void setLengthBothWays() {
        StringBuilder shrink = new StringBuilder("hello world");
        int before = shrink.capacity();
        shrink.setLength(5);

        StringBuilder cleared = new StringBuilder("hello world");
        cleared.setLength(0);

        StringBuilder grown = new StringBuilder("ab");
        grown.setLength(5);                             // pads with the null char

        System.out.println("-- setLength --");
        System.out.println("  setLength(5) on \"hello world\" -> [" + shrink + "]");
        System.out.println("    capacity " + before + " -> " + shrink.capacity()
                + "   <- unchanged; truncating does not free memory");
        System.out.println("  setLength(0)  -> length " + cleared.length()
                + ", capacity " + cleared.capacity() + "   <- the reuse idiom");
        System.out.println("  setLength(5) on \"ab\" -> length " + grown.length()
                + ", chars now: " + describe(grown));
        System.out.println("    Growing PADS with the null character, code 0 — not");
        System.out.println("    with spaces. Those pad characters are invisible when");
        System.out.println("    printed but count toward length().");
        System.out.println();
    }

    private static String describe(CharSequence cs) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < cs.length(); i++) {
            out.append((int) cs.charAt(i));
            if (i < cs.length() - 1) {
                out.append(' ');
            }
        }
        return out.toString();
    }

    private static void builderVersusBuffer() {
        System.out.println("-- StringBuilder vs StringBuffer --");
        System.out.println("  Identical APIs. StringBuffer's methods are synchronized;");
        System.out.println("  StringBuilder's are not.");
        System.out.println();
        System.out.println("  StringBuffer  (Java 1.0)  thread-safe, slower");
        System.out.println("  StringBuilder (Java 5)    not thread-safe, faster");
        System.out.println();
        System.out.println("  Use StringBuilder. A builder is almost always a local");
        System.out.println("  variable that never escapes its method, so the locking");
        System.out.println("  protects nothing and costs on every call.");
    }
}
