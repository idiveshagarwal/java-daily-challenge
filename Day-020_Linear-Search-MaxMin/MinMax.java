import java.util.Arrays;

/**
 * Day 20, part 2 — finding max and min.
 *
 * The algorithm is three lines. The bug is in the FIRST line: what you
 * initialise the running maximum to. Getting that wrong produces an answer
 * that is not merely off, but not even a member of the array.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-02
 */
public class MinMax {

    public static void main(String[] args) {
        System.out.println("Day 20 — Max and min");
        System.out.println();

        theInitialisationBug();
        theEmptyArrayQuestion();
        singlePassBoth();
        nanPoisonsTheComparison();
    }

    /** WRONG: assumes the array contains something >= 0. */
    static int maxFromZero(int[] a) {
        int max = 0;
        for (int v : a) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    /** Works, but hides the empty case. */
    static int maxFromMinValue(int[] a) {
        int max = Integer.MIN_VALUE;
        for (int v : a) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    /** Correct: start from a real element, so the answer is always a member. */
    static int maxFromFirst(int[] a) {
        int max = a[0];                       // throws on empty — deliberately
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }

    /**
     * The bug everyone writes once. `int max = 0` silently assumes a
     * non-negative element exists.
     */
    private static void theInitialisationBug() {
        int[] negatives = { -5, -2, -9, -1 };

        System.out.println("── The initialisation bug ──");
        System.out.println("  data " + Arrays.toString(negatives) + "   (all negative)");
        System.out.println();
        System.out.println("  int max = 0                -> " + maxFromZero(negatives)
                + "    <- WRONG: 0 is not in the array");
        System.out.println("  int max = Integer.MIN_VALUE -> " + maxFromMinValue(negatives));
        System.out.println("  int max = a[0]              -> " + maxFromFirst(negatives));
        System.out.println();
        System.out.println("  Starting from 0 makes the function return a value that");
        System.out.println("  is not a member of its own input. Starting from a[0]");
        System.out.println("  makes that impossible by construction.");
        System.out.println();
    }

    /** MIN_VALUE and a[0] disagree about the empty array — loudly vs silently. */
    private static void theEmptyArrayQuestion() {
        int[] empty = {};

        System.out.println("── The empty array ──");
        System.out.println("  MIN_VALUE init -> " + maxFromMinValue(empty)
                + "   <- silent, and looks like data");

        try {
            System.out.println(maxFromFirst(empty));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  a[0] init      -> " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
        }

        System.out.println();
        System.out.println("  There IS no maximum of an empty set, so returning a");
        System.out.println("  number is a lie. The options, best first:");
        System.out.println("    1. OptionalInt  — the absence is in the type");
        System.out.println("    2. throw IllegalArgumentException with a clear message");
        System.out.println("    3. return MIN_VALUE — indistinguishable from real data");
        System.out.println();
        System.out.println("  Arrays.stream(a).max() returns OptionalInt: "
                + Arrays.stream(empty).max());
        System.out.println("  and for real data: " + Arrays.stream(new int[] { 3, 9, 4 }).max());
        System.out.println();
    }

    /**
     * Both extremes, and their positions, in one pass. Two separate loops read
     * the array twice for no benefit — and get out of step if it changes
     * between them.
     */
    private static void singlePassBoth() {
        int[] data = { 42, 7, 99, 7, 63, 99 };

        if (data.length == 0) {
            throw new IllegalArgumentException("no extremes of an empty array");
        }

        int max = data[0];
        int min = data[0];
        int maxAt = 0;
        int minAt = 0;

        for (int i = 1; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
                maxAt = i;
            } else if (data[i] < min) {       // else-if: cannot be both
                min = data[i];
                minAt = i;
            }
        }

        System.out.println("── One pass, both extremes ──");
        System.out.println("  data " + Arrays.toString(data));
        System.out.println("  max = " + max + " at index " + maxAt
                + "   (first of " + countOf(data, max) + " occurrences)");
        System.out.println("  min = " + min + " at index " + minAt);
        System.out.println();
        System.out.println("  `else if` is safe here: a value cannot beat both the");
        System.out.println("  running max and the running min in the same iteration.");
        System.out.println("  With `>=` instead of `>` you would get the LAST");
        System.out.println("  occurrence rather than the first — decide which you mean.");
        System.out.println();
    }

    private static int countOf(int[] a, int target) {
        int n = 0;
        for (int v : a) {
            if (v == target) {
                n++;
            }
        }
        return n;
    }

    /**
     * For doubles, NaN loses every comparison (Day 8), so a NaN in the data is
     * never selected as the maximum — the loop reports the largest of the
     * others and gives no hint that the data was contaminated.
     *
     * Exactly the Day 11 failure, now spread across a whole array.
     */
    private static void nanPoisonsTheComparison() {
        double[] withNan = { 3.0, Double.NaN, 9.0, 1.0 };

        double manual = withNan[0];
        for (int i = 1; i < withNan.length; i++) {
            if (withNan[i] > manual) {
                manual = withNan[i];
            }
        }

        double viaMathMax = withNan[0];
        for (int i = 1; i < withNan.length; i++) {
            viaMathMax = Math.max(viaMathMax, withNan[i]);
        }

        System.out.println("── NaN in a double[] ──");
        System.out.println("  data " + Arrays.toString(withNan));
        System.out.println("  manual  `if (v > max)` -> " + manual
                + "   <- NaN silently ignored");
        System.out.println("  Math.max in the loop   -> " + viaMathMax
                + "   <- NaN propagates, as documented");
        System.out.println();
        System.out.println("  Neither is wrong, but they answer different questions.");
        System.out.println("  If NaN means \"missing\", filter it out explicitly rather");
        System.out.println("  than relying on comparisons to drop it (Day 8, Day 11).");
    }
}
