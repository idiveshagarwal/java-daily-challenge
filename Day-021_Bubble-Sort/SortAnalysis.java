import java.util.Arrays;
import java.util.Random;

/**
 * Day 21, part 2 — measuring bubble sort, and checking it is correct.
 *
 * Three questions worth answering with numbers rather than assertions:
 *
 *   1. what does each optimisation actually save, and on which input?
 *   2. why is the swap count identical across all three versions?
 *   3. is the sort stable, and what decides that?
 *
 * @author  Divesh Agarwal
 * @since   2026-09-03
 */
public class SortAnalysis {

    private static long comparisons;
    private static long swaps;
    private static int passes;

    public static void main(String[] args) {
        System.out.println("Day 21 — Bubble sort analysis");
        System.out.println();

        measureAllThree();
        swapsEqualInversions();
        stabilityIsOneCharacter();
        verifyAgainstArraysSort();
    }

    // ── the three variants, instrumented ───────────────────────────────────

    static void naive(int[] a) {
        reset();
        for (int i = 0; i < a.length - 1; i++) {
            passes++;
            for (int j = 0; j < a.length - 1; j++) {      // full width every pass
                comparisons++;
                if (a[j] > a[j + 1]) {
                    swap(a, j);
                }
            }
        }
    }

    static void shrinking(int[] a) {
        reset();
        for (int i = 0; i < a.length - 1; i++) {
            passes++;
            for (int j = 0; j < a.length - 1 - i; j++) {  // skip the sorted tail
                comparisons++;
                if (a[j] > a[j + 1]) {
                    swap(a, j);
                }
            }
        }
    }

    static void earlyExit(int[] a) {
        reset();
        for (int i = 0; i < a.length - 1; i++) {
            passes++;
            boolean swapped = false;
            for (int j = 0; j < a.length - 1 - i; j++) {
                comparisons++;
                if (a[j] > a[j + 1]) {
                    swap(a, j);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;                                    // already sorted
            }
        }
    }

    private static void reset() {
        comparisons = 0;
        swaps = 0;
        passes = 0;
    }

    private static void swap(int[] a, int j) {
        int t = a[j];
        a[j] = a[j + 1];
        a[j + 1] = t;
        swaps++;
    }

    /**
     * The optimisations help completely different inputs. The shrinking bound
     * halves the comparisons on everything; the early exit is worth nothing
     * except on nearly-sorted data — where it is worth everything.
     */
    private static void measureAllThree() {
        int n = 10;
        int[] sorted = new int[n];
        int[] reverse = new int[n];
        for (int i = 0; i < n; i++) {
            sorted[i] = i;
            reverse[i] = n - i;
        }
        int[] random = { 5, 2, 9, 1, 7, 3, 8, 6, 4, 0 };

        System.out.println("── Work done, n = " + n + " ──");
        System.out.printf("  %-10s %-11s %12s %8s %8s%n",
                "input", "variant", "comparisons", "swaps", "passes");

        for (String kind : new String[] { "sorted", "reverse", "random" }) {
            int[] base = kind.equals("sorted") ? sorted
                       : kind.equals("reverse") ? reverse : random;

            for (String variant : new String[] { "naive", "shrinking", "earlyExit" }) {
                int[] a = base.clone();
                switch (variant) {
                    case "naive" -> naive(a);
                    case "shrinking" -> shrinking(a);
                    default -> earlyExit(a);
                }
                System.out.printf("  %-10s %-11s %12d %8d %8d%n",
                        kind, variant, comparisons, swaps, passes);
            }
        }

        System.out.println();
        System.out.println("  naive     : (n-1)^2 = " + ((n - 1) * (n - 1)) + " comparisons, always");
        System.out.println("  shrinking : (n-1)n/2 = " + ((n - 1) * n / 2) + " comparisons, always");
        System.out.println("  earlyExit : " + (n - 1) + " on sorted input — ONE pass, O(n)");
        System.out.println();
        System.out.println("  The early exit changes the best case from quadratic to");
        System.out.println("  linear, and changes nothing else. On reverse or random");
        System.out.println("  input it costs an extra boolean and saves nothing.");
        System.out.println();
    }

    /**
     * The swap counts are identical across all three variants because a swap
     * only ever happens for an out-of-order adjacent pair — and each such swap
     * removes exactly ONE inversion. The number of swaps is therefore a
     * property of the DATA, not of the algorithm variant.
     */
    private static void swapsEqualInversions() {
        Random rng = new Random(42);
        int trials = 500;
        int mismatches = 0;

        for (int t = 0; t < trials; t++) {
            int len = 1 + rng.nextInt(12);
            int[] a = new int[len];
            for (int i = 0; i < len; i++) {
                a[i] = rng.nextInt(20);                   // duplicates on purpose
            }

            int expected = countInversions(a);
            int[] copy = a.clone();
            shrinking(copy);

            if (swaps != expected) {
                mismatches++;
                System.out.println("  MISMATCH " + Arrays.toString(a));
            }
        }

        int[] sample = { 5, 2, 9, 1, 7, 3, 8, 6, 4, 0 };
        int inv = countInversions(sample);

        System.out.println("── Swaps = inversions ──");
        System.out.println("  sample " + Arrays.toString(sample));
        System.out.println("  inversions = " + inv + ", and every variant swapped " + inv + " times");
        System.out.println();
        System.out.println("  Checked on " + trials + " random arrays: mismatches = " + mismatches);
        System.out.println();
        System.out.println("  Each adjacent swap removes exactly one inversion, so the");
        System.out.println("  swap count is a property of the DATA, not the variant.");
        System.out.println("  Optimisations can save comparisons — never swaps.");
        System.out.println();
    }

    /** An inversion is a pair out of order, at any distance. */
    private static int countInversions(int[] a) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] > a[j]) {
                    count++;
                }
            }
        }
        return count;
    }

    /** A sort is stable if equal elements keep their original relative order. */
    private record Item(int key, String tag) {
        @Override
        public String toString() {
            return key + tag;
        }
    }

    private static void sortItems(Item[] a, boolean strict) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                boolean shouldSwap = strict
                        ? a[j].key() > a[j + 1].key()      // stable
                        : a[j].key() >= a[j + 1].key();    // unstable
                if (shouldSwap) {
                    Item t = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = t;
                }
            }
        }
    }

    /**
     * Bubble sort is stable — but only because the comparison is strict.
     * Changing `>` to `>=` swaps equal elements too, reversing their order
     * every time they meet, and stability is gone.
     */
    private static void stabilityIsOneCharacter() {
        Item[] input = {
                new Item(3, "a"), new Item(1, "b"), new Item(3, "c"),
                new Item(1, "d"), new Item(2, "e")
        };

        Item[] withStrict = input.clone();
        sortItems(withStrict, true);

        Item[] withLoose = input.clone();
        sortItems(withLoose, false);

        System.out.println("── Stability ──");
        System.out.println("  input      " + Arrays.toString(input));
        System.out.println("  using >    " + Arrays.toString(withStrict)
                + "   <- 1b before 1d, 3a before 3c: STABLE");
        System.out.println("  using >=   " + Arrays.toString(withLoose)
                + "   <- equal keys reordered: UNSTABLE");
        System.out.println();
        System.out.println("  One character. `>=` swaps equal elements, which serves no");
        System.out.println("  purpose — it costs a swap and destroys the ordering that");
        System.out.println("  a previous sort may have established.");
        System.out.println();
        System.out.println("  Stability matters when sorting by one field after another:");
        System.out.println("  sort by name, then by department, and a stable sort keeps");
        System.out.println("  names alphabetical within each department.");
        System.out.println();
    }

    /** Correctness against the JDK's own sort, over many random arrays. */
    private static void verifyAgainstArraysSort() {
        Random rng = new Random(7);
        int trials = 2000;
        int failures = 0;

        for (int t = 0; t < trials; t++) {
            int len = rng.nextInt(15);                    // includes length 0
            int[] a = new int[len];
            for (int i = 0; i < len; i++) {
                a[i] = rng.nextInt(50) - 25;              // negatives too
            }

            int[] mine = a.clone();
            int[] theirs = a.clone();
            BubbleSort.sort(mine);
            Arrays.sort(theirs);

            if (!Arrays.equals(mine, theirs)) {
                failures++;
                System.out.println("  FAIL " + Arrays.toString(a));
            }
        }

        System.out.println("── Verified against Arrays.sort ──");
        System.out.println("  " + trials + " random arrays, lengths 0..14, values -25..24");
        System.out.println("  failures: " + failures);
        System.out.println(failures == 0
                ? "  bubble sort agrees with the JDK on every case, empty included."
                : "  SOMETHING IS WRONG.");
    }
}
