import java.util.Arrays;

/**
 * Day 19, part 2 — five ways arrays surprise you.
 *
 * All five follow from the same root: an array is an OBJECT with a fixed
 * length, and the variable holds a reference to it rather than the data.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-01
 */
public class ArrayPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 19 — Array pitfalls");
        System.out.println();

        lengthIsAFieldNotAMethod();
        equalityComparesReferences();
        assignmentSharesTheArray();
        indexBounds();
        covarianceDefersTheCheckToRuntime();
    }

    /**
     * The asymmetry that catches everyone: arrays expose `length` as a FIELD,
     * Strings expose `length()` as a METHOD. Getting it backwards fails to
     * compile in BOTH directions.
     */
    private static void lengthIsAFieldNotAMethod() {
        int[] array = { 1, 2, 3 };
        String text = "abc";

        System.out.println("── length: field or method? ──");
        System.out.println("  array.length   = " + array.length + "    (field, no parens)");
        System.out.println("  text.length()  = " + text.length() + "    (method, parens)");
        System.out.println();
        System.out.println("  Both mistakes are compile errors:");
        System.out.println("    array.length()  -> error: cannot find symbol");
        System.out.println("    text.length     -> error: cannot find symbol");
        System.out.println();
        System.out.println("  Verified on " + System.getProperty("java.version")
                + ". At least it fails loudly.");
        System.out.println();
    }

    /** == on arrays asks "same object?", exactly as for any reference (Day 8). */
    private static void equalityComparesReferences() {
        int[] p = { 1, 2, 3 };
        int[] q = { 1, 2, 3 };

        System.out.println("── Equality ──");
        System.out.println("  p == q              : " + (p == q) + "   <- different objects");
        System.out.println("  p.equals(q)         : " + p.equals(q) + "   <- Object.equals, identity AGAIN");
        System.out.println("  Arrays.equals(p, q) : " + Arrays.equals(p, q) + "    <- compares contents");
        System.out.println();
        System.out.println("  Arrays do NOT override equals(), so p.equals(q) is just");
        System.out.println("  p == q wearing a method call. Always Arrays.equals.");
        System.out.println("  (Arrays.deepEquals for nested arrays.)");
        System.out.println();
    }

    /**
     * Assigning an array variable copies the REFERENCE, not the data — so both
     * names then refer to one array. The same rule as Day 6's `final List`.
     */
    private static void assignmentSharesTheArray() {
        int[] original = { 1, 2, 3 };
        int[] alias = original;                     // NOT a copy
        int[] copy = Arrays.copyOf(original, original.length);

        alias[0] = 99;

        System.out.println("── Assignment shares, it does not copy ──");
        System.out.println("  int[] alias = original;  alias[0] = 99;");
        System.out.println("    original " + Arrays.toString(original) + "   <- changed too");
        System.out.println("    alias    " + Arrays.toString(alias));
        System.out.println("    copy     " + Arrays.toString(copy) + "    <- unaffected");
        System.out.println();
        System.out.println("  Use Arrays.copyOf, clone(), or System.arraycopy for a");
        System.out.println("  real copy. Passing an array to a method shares it the");
        System.out.println("  same way — the method can modify your array.");
        System.out.println();
    }

    /** Both ends of the range throw, and the message names the offending index. */
    private static void indexBounds() {
        int[] a = { 1, 2, 3 };

        System.out.println("── Index bounds ──");
        System.out.println("  valid indices: 0 .. " + (a.length - 1) + "  (length is " + a.length + ")");

        try {
            System.out.println(a[a.length]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  a[a.length] -> " + e.getMessage());
        }
        try {
            System.out.println(a[-1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  a[-1]       -> " + e.getMessage());
        }
        try {
            int[] bad = new int[-1];
            System.out.println(bad.length);
        } catch (NegativeArraySizeException e) {
            System.out.println("  new int[-1] -> NegativeArraySizeException: " + e.getMessage());
        }

        System.out.println("  new int[0] is legal, length = " + new int[0].length
                + "  <- empty, not null");
        System.out.println();
        System.out.println("  `i <= a.length` is the classic off-by-one (Day 13):");
        System.out.println("  the last valid index is length - 1.");
        System.out.println();
    }

    /**
     * Java arrays are COVARIANT: String[] is usable as an Object[]. That makes
     * the assignment below compile, and the type error only surfaces when a
     * value is actually stored — as an ArrayStoreException at runtime.
     *
     * Generics were deliberately made invariant to avoid exactly this, which
     * is why List<String> is NOT a List<Object>.
     */
    private static void covarianceDefersTheCheckToRuntime() {
        Object[] objects = new String[2];           // compiles: String[] IS-A Object[]

        System.out.println("── Covariance ──");
        System.out.println("  Object[] objects = new String[2];   // compiles fine");

        try {
            objects[0] = 42;                        // an Integer into a String[]
            System.out.println("  stored 42");
        } catch (ArrayStoreException e) {
            System.out.println("  objects[0] = 42  ->  ArrayStoreException: " + e.getMessage());
        }

        objects[0] = "this is fine";
        System.out.println("  objects[0] = \"...\" -> " + objects[0]);
        System.out.println();
        System.out.println("  The array knows its real component type and checks");
        System.out.println("  every store. The compiler cannot catch this, which is");
        System.out.println("  why generics are invariant: List<String> is NOT a");
        System.out.println("  List<Object>, precisely so the error arrives earlier.");
    }
}
