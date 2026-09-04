import java.util.Arrays;

/**
 * Day 21 — bubble sort.
 *
 * Repeatedly compare adjacent pairs and swap them when out of order. After one
 * full pass the largest value has "bubbled" to the end, so each pass fixes one
 * more element at the back.
 *
 * Three versions, each one line different from the last:
 *
 *   naive      always n-1 passes over the whole array
 *   shrinking  inner bound shrinks — the tail is already sorted
 *   earlyExit  stop when a pass makes no swaps
 *
 * The third turns the best case from quadratic into linear (SortAnalysis).
 *
 * @author  Divesh Agarwal
 * @since   2026-09-03
 */
public class BubbleSort {

    public static void main(String[] args) {
        System.out.println("Day 21 — Bubble sort");
        System.out.println();

        traceOnePass();
        showTheThreeVersions();
        whyTheTailIsSorted();
    }

    /** The version worth writing: shrinking bound plus early exit. */
    public static void sort(int[] a) {
        for (int pass = 0; pass < a.length - 1; pass++) {
            boolean swapped = false;

            // -1-pass: the last `pass` elements are already in final position
            for (int j = 0; j < a.length - 1 - pass; j++) {
                if (a[j] > a[j + 1]) {          // > not >= — see stability, SortAnalysis
                    swap(a, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) {
                return;                         // fully sorted, nothing left to do
            }
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    /** Shows every comparison of the first pass, so the "bubbling" is visible. */
    private static void traceOnePass() {
        int[] a = { 5, 1, 4, 2, 8 };

        System.out.println("── One pass, step by step ──");
        System.out.println("  start " + Arrays.toString(a));

        for (int j = 0; j < a.length - 1; j++) {
            boolean needsSwap = a[j] > a[j + 1];
            System.out.printf("  compare a[%d]=%d and a[%d]=%d -> %s",
                    j, a[j], j + 1, a[j + 1], needsSwap ? "swap  " : "keep  ");
            if (needsSwap) {
                swap(a, j, j + 1);
            }
            System.out.println(Arrays.toString(a));
        }

        System.out.println("  After one pass the largest value (8) is at the end.");
        System.out.println("  That position is now FINAL and never examined again.");
        System.out.println();
    }

    private static void showTheThreeVersions() {
        int[] data = { 5, 2, 9, 1, 7, 3, 8, 6, 4, 0 };

        int[] a = data.clone();
        sort(a);

        System.out.println("── Sorted ──");
        System.out.println("  before " + Arrays.toString(data));
        System.out.println("  after  " + Arrays.toString(a));
        System.out.println();
        System.out.println("  naive     : inner loop runs to a.length-1 every pass");
        System.out.println("  shrinking : inner loop runs to a.length-1-pass");
        System.out.println("  earlyExit : plus `if (!swapped) return;`");
        System.out.println();
        System.out.println("  Same output, very different work — see SortAnalysis.");
        System.out.println();
    }

    /**
     * The shrinking bound is not a guess: after pass p, the last p elements are
     * provably in final position, because each pass carries the largest
     * remaining value all the way to the right.
     */
    private static void whyTheTailIsSorted() {
        int[] a = { 5, 1, 4, 2, 8 };

        System.out.println("── Why the tail can be skipped ──");
        System.out.println("  start          " + Arrays.toString(a));

        for (int pass = 0; pass < a.length - 1; pass++) {
            for (int j = 0; j < a.length - 1 - pass; j++) {
                if (a[j] > a[j + 1]) {
                    swap(a, j, j + 1);
                }
            }
            int fixed = pass + 1;
            System.out.printf("  after pass %d   %s   last %d element%s final%n",
                    pass + 1, Arrays.toString(a), fixed, fixed == 1 ? "" : "s");
        }

        System.out.println();
        System.out.println("  Each pass moves the largest UNSORTED value to its place,");
        System.out.println("  so the sorted region grows by one from the right. Scanning");
        System.out.println("  it again cannot find a swap — hence a.length-1-pass.");
    }
}
