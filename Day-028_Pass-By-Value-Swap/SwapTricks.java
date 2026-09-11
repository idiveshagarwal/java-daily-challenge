import java.util.Arrays;
import java.util.Random;

/**
 * Day 28, part 2 — swapping without a temporary variable.
 *
 * Two classic tricks, both presented in interviews as clever:
 *
 *     XOR         a ^= b;  b ^= a;  a ^= b;
 *     arithmetic  a += b;  b = a - b;  a = a - b;
 *
 * Both work. Both have a failure mode. The interesting part is that the
 * failure mode is NOT the one usually cited.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-10
 */
public class SwapTricks {

    public static void main(String[] args) {
        System.out.println("Day 28 - Swapping without a temp");
        System.out.println();

        theyBothWork();
        theRealFailureIsAliasing();
        overflowDoesNotBreakThem();
        butFloatingPointDoes();
        theVerdict();
    }

    static void xorSwap(int[] a, int i, int j) {
        a[i] ^= a[j];
        a[j] ^= a[i];
        a[i] ^= a[j];
    }

    static void arithmeticSwap(int[] a, int i, int j) {
        a[i] = a[i] + a[j];
        a[j] = a[i] - a[j];
        a[i] = a[i] - a[j];
    }

    static void tempSwap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private static void theyBothWork() {
        System.out.println("-- All three work on distinct indices --");
        for (String kind : new String[] { "xor", "arithmetic", "temp" }) {
            int[] a = { 10, 20 };
            apply(kind, a, 0, 1);
            System.out.printf("  %-12s [10, 20] -> %s%n", kind, Arrays.toString(a));
        }
        System.out.println();
    }

    private static void apply(String kind, int[] a, int i, int j) {
        switch (kind) {
            case "xor" -> xorSwap(a, i, j);
            case "arithmetic" -> arithmeticSwap(a, i, j);
            default -> tempSwap(a, i, j);
        }
    }

    /**
     * THE actual failure. When i == j both clever versions destroy the value,
     * because each operates on one storage location twice.
     *
     * a ^= a is always 0. a = a + a then a = a - a is also 0.
     */
    private static void theRealFailureIsAliasing() {
        System.out.println("-- The real failure: i == j --");
        for (String kind : new String[] { "xor", "arithmetic", "temp" }) {
            int[] a = { 10, 20 };
            apply(kind, a, 0, 0);
            System.out.printf("  %-12s swap(a, 0, 0) -> %s%s%n",
                    kind, Arrays.toString(a), a[0] == 10 ? "" : "   <- DESTROYED");
        }

        System.out.println();
        System.out.println("  Swapping an element with itself should be a no-op. Both");
        System.out.println("  clever versions zero it instead:");
        System.out.println("    a ^= a          is always 0");
        System.out.println("    a = a + a; a = a - a;   is also 0");
        System.out.println();
        System.out.println("  This matters because sorting algorithms DO call swap(i, i)");
        System.out.println("  — quicksort partitioning hits it routinely. A temp-based");
        System.out.println("  swap handles it for free; the tricks silently corrupt the");
        System.out.println("  array.");
        System.out.println();
    }

    /**
     * The usual objection to the arithmetic version is "it overflows". For
     * Java ints that objection is WRONG, and it is worth checking rather than
     * repeating.
     *
     * Two's-complement + and - are modular arithmetic, so (a + b) - b == a
     * holds even when a + b wraps past MAX_VALUE.
     */
    private static void overflowDoesNotBreakThem() {
        int[] extremes = { Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1 };
        Random rng = new Random(3);

        int tested = 0;
        int arithmeticFailures = 0;
        int xorFailures = 0;

        for (int a : extremes) {
            for (int b : extremes) {
                if (!check(a, b)) {
                    arithmeticFailures++;
                }
                tested++;
            }
        }
        for (int t = 0; t < 200_000; t++) {
            int a = rng.nextInt();
            int b = rng.nextInt();
            if (!check(a, b)) {
                arithmeticFailures++;
            }
            if (!checkXor(a, b)) {
                xorFailures++;
            }
            tested++;
        }

        System.out.println("-- Does overflow break the arithmetic swap? --");
        System.out.println("  Common claim: \"a + b overflows, so the trick fails.\"");
        System.out.println();
        System.out.println("  tested " + tested + " pairs, including every combination of");
        System.out.println("  MIN_VALUE, MAX_VALUE, 0, -1 and 1:");
        System.out.println("    arithmetic swap failures: " + arithmeticFailures);
        System.out.println("    xor swap failures:        " + xorFailures);
        System.out.println();
        System.out.println("  The claim is FALSE for Java ints. Two's-complement");
        System.out.println("  addition and subtraction are modular, so (a + b) - b == a");
        System.out.println("  even when a + b wraps (Day 4).");
        System.out.println();
        System.out.println("  It WOULD be true where overflow is undefined or throws —");
        System.out.println("  C signed arithmetic, or Java's own Math.addExact.");
        System.out.println();
    }

    /** Round-trips one pair through the arithmetic swap. */
    private static boolean check(int a, int b) {
        int x = a;
        int y = b;
        x = x + y;
        y = x - y;
        x = x - y;
        return x == b && y == a;
    }

    private static boolean checkXor(int a, int b) {
        int x = a;
        int y = b;
        x ^= y;
        y ^= x;
        x ^= y;
        return x == b && y == a;
    }

    /**
     * Floating-point + and - are NOT modular — they round. So the trick that
     * survives int overflow fails on double.
     */
    private static void butFloatingPointDoes() {
        double[][] cases = {
                { 1e16, 1.0 },
                { 0.1, 0.2 },
                { Double.MAX_VALUE, 1.0 }
        };

        System.out.println("-- On double, it genuinely does break --");
        System.out.printf("  %-26s %-26s %s%n", "input", "after arithmetic swap", "ok?");

        for (double[] c : cases) {
            double a = c[0];
            double b = c[1];
            double x = a;
            double y = b;
            x = x + y;
            y = x - y;
            x = x - y;
            boolean ok = (x == b && y == a);
            System.out.printf("  (%-10s %-12s (%-10s %-12s %s%n",
                    fmt(a) + ",", fmt(b) + ")", fmt(x) + ",", fmt(y) + ")", ok ? "yes" : "NO");
        }

        System.out.println();
        System.out.println("  1e16 + 1.0 cannot be represented, so the 1.0 is lost and");
        System.out.println("  never comes back. Even 0.1 and 0.2 fail to round-trip");
        System.out.println("  exactly (Day 4) — they LOOK swapped but are not equal to");
        System.out.println("  the originals.");
        System.out.println();
    }

    private static String fmt(double d) {
        return String.format("%.4g", d);
    }

    private static void theVerdict() {
        System.out.println("-- Verdict --");
        System.out.printf("  %-16s %-14s %-14s %s%n", "", "i == j", "int overflow", "double");
        System.out.printf("  %-16s %-14s %-14s %s%n", "xor", "DESTROYS", "safe", "n/a");
        System.out.printf("  %-16s %-14s %-14s %s%n", "arithmetic", "DESTROYS", "safe", "BREAKS");
        System.out.printf("  %-16s %-14s %-14s %s%n", "temp variable", "fine", "safe", "fine");
        System.out.println();
        System.out.println("  Use the temp variable. It is correct in every column, it");
        System.out.println("  reads as what it does, and on any real JVM it is not");
        System.out.println("  slower — the tricks save one local slot the register");
        System.out.println("  allocator was going to reuse anyway.");
        System.out.println();
        System.out.println("  The tricks are worth knowing only so you can explain why");
        System.out.println("  they are a bad idea — and the honest reason is aliasing,");
        System.out.println("  not the overflow everyone cites.");
    }
}
