import java.util.Arrays;
import java.util.Scanner;

/**
 * Day 20 — linear search.
 *
 * The first real algorithm: walk the array, compare, report where. Simple
 * enough that the interesting decisions are all about the EDGES —
 *
 *   what to return when the value is absent
 *   which occurrence to return when there are several
 *   what "equal" means for objects rather than ints
 *
 * Run:  printf '30\n' | java LinearSearch
 *
 * @author  Divesh Agarwal
 * @since   2026-09-02
 */
public class LinearSearch {

    private static final int NOT_FOUND = -1;

    public static void main(String[] args) {
        int[] data = { 50, 30, 70, 30, 10, 90 };

        System.out.println("Day 20 — Linear search");
        System.out.println();
        System.out.println("  data = " + Arrays.toString(data));

        int target;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\n  Search for: ");
            target = sc.hasNextInt() ? sc.nextInt() : 30;
        }

        System.out.println();
        System.out.println("  indexOf      -> " + indexOf(data, target));
        System.out.println("  lastIndexOf  -> " + lastIndexOf(data, target));
        System.out.println("  contains     -> " + contains(data, target));
        System.out.println("  countOf      -> " + countOf(data, target));
        System.out.println();

        whyMinusOne();
        objectsNeedEquals();
        binarySearchNeedsSortedInput();
        verifyAgainstBruteForce();
    }

    /**
     * The canonical form: return the index of the FIRST match, or -1.
     *
     * `return` doubles as the break (Day 16) — once found there is nothing
     * left to learn, so the remaining elements are never examined.
     *
     * @return index of the first occurrence, or -1 if absent
     */
    public static int indexOf(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /** Scanning backwards finds the LAST occurrence in one pass. */
    public static int lastIndexOf(int[] array, int target) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /** When only presence matters, say so — the index is an implementation detail. */
    public static boolean contains(int[] array, int target) {
        return indexOf(array, target) != NOT_FOUND;
    }

    /** Counting cannot stop early: every element has to be examined. */
    public static int countOf(int[] array, int target) {
        int count = 0;
        for (int value : array) {
            if (value == target) {
                count++;
            }
        }
        return count;
    }

    /** Why -1 rather than 0, false, or an exception. */
    private static void whyMinusOne() {
        System.out.println("── Why -1 for \"not found\" ──");
        System.out.println("  0      is a VALID index — indistinguishable from a hit");
        System.out.println("  false  loses the position when you needed it");
        System.out.println("  throw  makes absence an error; it usually is not");
        System.out.println();
        System.out.println("  -1 is never a valid index, so it cannot be mistaken for");
        System.out.println("  a result. String.indexOf and List.indexOf both use it.");
        System.out.println();
        System.out.println("  The cost: callers MUST check. `if (indexOf(a, t))` does");
        System.out.println("  not compile in Java (Day 10), which helps — but");
        System.out.println("  `array[indexOf(a, t)]` compiles and throws on -1.");
        System.out.println();
    }

    /**
     * == compares references for objects (Day 8, Day 19). A search that uses
     * it finds only the identical object, not an equal one.
     */
    private static void objectsNeedEquals() {
        String[] names = { "alice", "bob", "carol" };
        String needle = new String("bob");        // equal contents, new object

        int byIdentity = NOT_FOUND;
        for (int i = 0; i < names.length; i++) {
            if (names[i] == needle) {             // WRONG for objects
                byIdentity = i;
                break;
            }
        }

        int byEquals = NOT_FOUND;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(needle)) {        // correct
                byEquals = i;
                break;
            }
        }

        System.out.println("── Searching objects ──");
        System.out.println("  names  = " + Arrays.toString(names));
        System.out.println("  needle = new String(\"bob\")");
        System.out.println("  using ==      -> " + byIdentity + "   <- not found, though it is there");
        System.out.println("  using equals  -> " + byEquals);
        System.out.println();
        System.out.println("  Safer still: Objects.equals(a, b), which tolerates null");
        System.out.println("  on either side.");
        System.out.println();
    }

    /**
     * Arrays.binarySearch is far faster, but its contract REQUIRES a sorted
     * array. On unsorted input the result is undefined — and undefined here
     * means a plausible wrong answer, not an exception.
     */
    private static void binarySearchNeedsSortedInput() {
        int[] unsorted = { 5, 3, 9, 1, 7 };
        int[] sorted = unsorted.clone();
        Arrays.sort(sorted);

        System.out.println("── binarySearch needs sorted input ──");
        System.out.println("  unsorted " + Arrays.toString(unsorted));
        System.out.printf("  %8s %16s %16s%n", "target", "binarySearch", "true index");
        for (int target : new int[] { 9, 1, 5 }) {
            System.out.printf("  %8d %16d %16d%n",
                    target, Arrays.binarySearch(unsorted, target), indexOf(unsorted, target));
        }

        System.out.println();
        System.out.println("  Searching for 1 returns -1 — yet 1 IS present, at index 3.");
        System.out.println("  No exception. The contract was broken, so the answer is");
        System.out.println("  meaningless, and nothing says so.");
        System.out.println();
        System.out.println("  sorted   " + Arrays.toString(sorted));
        System.out.println("  binarySearch(sorted, 1) = " + Arrays.binarySearch(sorted, 1) + "   <- correct");
        System.out.println();
        System.out.println("  A negative result on sorted input encodes the insertion");
        System.out.println("  point as -(insertion point) - 1, so it is still useful.");
        System.out.println("  binarySearch(sorted, 4) = " + Arrays.binarySearch(sorted, 4)
                + "  -> would insert at index " + (-Arrays.binarySearch(sorted, 4) - 1));
        System.out.println();
    }

    /**
     * indexOf is simple enough to check exhaustively: for every array of a
     * small size and every target, compare against an independent scan.
     */
    private static void verifyAgainstBruteForce() {
        int checks = 0;
        int failures = 0;

        for (int len = 0; len <= 6; len++) {
            int[] a = new int[len];
            for (int i = 0; i < len; i++) {
                a[i] = i % 3;                      // deliberate duplicates
            }

            for (int target = -1; target <= 3; target++) {
                int expectedFirst = NOT_FOUND;
                int expectedLast = NOT_FOUND;
                int expectedCount = 0;
                for (int i = 0; i < len; i++) {
                    if (a[i] == target) {
                        if (expectedFirst == NOT_FOUND) {
                            expectedFirst = i;
                        }
                        expectedLast = i;
                        expectedCount++;
                    }
                }

                if (indexOf(a, target) != expectedFirst
                        || lastIndexOf(a, target) != expectedLast
                        || countOf(a, target) != expectedCount
                        || contains(a, target) != (expectedCount > 0)) {
                    failures++;
                    System.out.println("  FAIL len=" + len + " target=" + target);
                }
                checks++;
            }
        }

        System.out.println("── Verified ──");
        System.out.println("  lengths 0..6 (including empty), targets -1..3");
        System.out.println("  " + checks + " cases, failures: " + failures);
        System.out.println(failures == 0
                ? "  first, last, count and contains all agree with a brute-force scan."
                : "  SOMETHING IS WRONG.");
    }
}
