import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Day 28 — pass by value, through the swapping problem.
 *
 * Day 26 established the rule: Java copies every argument, so a method can
 * change what an object CONTAINS but never which object the caller's variable
 * POINTS AT. The direct consequence is that
 *
 *     static void swap(int a, int b)
 *
 * cannot work, in any language where arguments are passed this way.
 *
 * This day takes that as the starting point and asks the practical question:
 * given that limitation, how do you actually swap two values?
 *
 * @author  Divesh Agarwal
 * @since   2026-09-10
 */
public class SwapWorkarounds {

    public static void main(String[] args) {
        System.out.println("Day 28 - Swapping under pass by value");
        System.out.println();

        whyItCannotWork();
        option1_swapInsideAnArray();
        option2_returnBoth();
        option3_aMutableHolder();
        option4_fieldsOnAnObject();
        option5_libraryHelpers();
        aGenericSwap();
        whatOtherLanguagesDo();
    }

    /** The method everyone writes first. It works perfectly on the copies. */
    static void swapAttempt(int a, int b) {
        int temp = a;
        a = b;
        b = temp;
        // a and b are correct HERE, then the frame is discarded
    }

    private static void whyItCannotWork() {
        int x = 1;
        int y = 2;
        swapAttempt(x, y);

        System.out.println("-- Why swap(int, int) cannot work --");
        System.out.println("  x=1 y=2, then swapAttempt(x, y)");
        System.out.println("  after: x=" + x + " y=" + y + "   <- unchanged");
        System.out.println();
        System.out.println("  The method DID swap — its own two copies. They were");
        System.out.println("  correct right up to the closing brace, then discarded.");
        System.out.println();
        System.out.println("  Nothing is wrong with the code. The signature is simply");
        System.out.println("  unable to express the intent: it has no way to reach the");
        System.out.println("  caller's variables (Day 26).");
        System.out.println();
    }

    /**
     * OPTION 1 — the standard one. Swap by INDEX inside a shared object.
     * The array is the thing both sides can see, so mutating it is visible.
     */
    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static void option1_swapInsideAnArray() {
        int[] values = { 1, 2, 3 };
        swap(values, 0, 2);

        System.out.println("-- Option 1: swap by index inside an array --");
        System.out.println("  swap(values, 0, 2) -> " + Arrays.toString(values) + "   <- works");
        System.out.println();
        System.out.println("  This is how every sorting algorithm does it (Day 21). The");
        System.out.println("  array is shared, so the swap happens where both sides can");
        System.out.println("  see it. Nothing is being passed by reference — the array");
        System.out.println("  reference is copied, and it still points at one array.");
        System.out.println();
    }

    /** OPTION 2 — return both values. A record makes this readable. */
    record Pair(int first, int second) {
        Pair swapped() {
            return new Pair(second, first);
        }
    }

    private static void option2_returnBoth() {
        Pair p = new Pair(1, 2);
        Pair q = p.swapped();

        int[] asArray = { 1, 2 };
        int[] reversed = { asArray[1], asArray[0] };

        System.out.println("-- Option 2: return both values --");
        System.out.println("  record Pair(int first, int second)");
        System.out.println("    " + p + " -> " + q);
        System.out.println("  or an array: " + Arrays.toString(asArray)
                + " -> " + Arrays.toString(reversed));
        System.out.println();
        System.out.println("  Usually the honest design. If a method computes two");
        System.out.println("  results, returning two results says so; reaching back");
        System.out.println("  into the caller's variables would not.");
        System.out.println();
    }

    /** OPTION 3 — a one-element holder, the crudest form of an out-parameter. */
    static void swap(int[] holderA, int[] holderB) {
        int temp = holderA[0];
        holderA[0] = holderB[0];
        holderB[0] = temp;
    }

    private static void option3_aMutableHolder() {
        int[] a = { 1 };
        int[] b = { 2 };
        swap(a, b);

        System.out.println("-- Option 3: one-element holders --");
        System.out.println("  int[] a = {1}; int[] b = {2}; swap(a, b);");
        System.out.println("    a[0]=" + a[0] + " b[0]=" + b[0] + "   <- works");
        System.out.println();
        System.out.println("  This is a hand-rolled out-parameter, and it is ugly on");
        System.out.println("  purpose: the array exists only to be a mutable box. It");
        System.out.println("  shows up in real code mainly to escape the effectively-");
        System.out.println("  final rule inside lambdas. Prefer option 2.");
        System.out.println();
    }

    /** OPTION 4 — if the values are fields, swap the fields. */
    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void swapCoordinates() {                  // operates on its OWN state
            int temp = x;
            x = y;
            y = temp;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    private static void option4_fieldsOnAnObject() {
        Point p = new Point(3, 7);
        System.out.println("-- Option 4: swap fields on an object --");
        System.out.print("  " + p);
        p.swapCoordinates();
        System.out.println(" -> " + p + "   <- works");
        System.out.println();
        System.out.println("  No parameters at all. The method is on the object that");
        System.out.println("  owns the data, so there is nothing to pass.");
        System.out.println();
    }

    /** OPTION 5 — the library already has this for lists and arrays. */
    private static void option5_libraryHelpers() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        Collections.swap(list, 0, 2);

        System.out.println("-- Option 5: library helpers --");
        System.out.println("  Collections.swap(list, 0, 2) -> " + list);
        System.out.println();
        System.out.println("  Collections.swap works on any List. There is no");
        System.out.println("  Arrays.swap — write the three-line version, or use");
        System.out.println("  the generic one below.");
        System.out.println();
    }

    /** One generic swap that covers every reference type. */
    static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static void aGenericSwap() {
        String[] names = { "alice", "bob", "carol" };
        swap(names, 0, 2);

        Integer[] boxed = { 1, 2, 3 };
        swap(boxed, 0, 1);

        System.out.println("-- A generic swap --");
        System.out.println("  static <T> void swap(T[] array, int i, int j)");
        System.out.println("    String[]  -> " + Arrays.toString(names));
        System.out.println("    Integer[] -> " + Arrays.toString(boxed));
        System.out.println();
        System.out.println("  Works for every reference type, but NOT for int[] —");
        System.out.println("  generics need a reference type, so primitives each need");
        System.out.println("  their own overload. This is the same limitation behind");
        System.out.println("  IntStream existing separately from Stream<Integer>.");
        System.out.println();
    }

    private static void whatOtherLanguagesDo() {
        System.out.println("-- Why Java is like this --");
        System.out.println("  C++     void swap(int& a, int& b)     reference parameter");
        System.out.println("  C#      void Swap(ref int a, ref int b)  ref parameter");
        System.out.println("  C       void swap(int *a, int *b)     explicit pointer");
        System.out.println("  Java    not expressible");
        System.out.println();
        System.out.println("  Java deliberately has no out or ref parameters. A method");
        System.out.println("  call can therefore never repoint a caller's variable,");
        System.out.println("  which makes call sites easier to reason about — at the");
        System.out.println("  cost of this one exercise being impossible.");
    }
}
