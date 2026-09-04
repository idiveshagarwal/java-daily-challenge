import java.util.Arrays;

/**
 * Day 22, part 2 — what a 2D array really is.
 *
 * Java has no two-dimensional array type. `int[][]` is an array whose elements
 * are `int[]` references — an array of arrays. Every surprise below follows
 * from that one fact, and from Day 19's rule that arrays are objects held by
 * reference.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-04
 */
public class TwoDArrays {

    public static void main(String[] args) {
        System.out.println("Day 22 — 2D arrays");
        System.out.println();

        arrayOfArrays();
        rowsCanBeMissing();
        jaggedIsLegal();
        printingNeedsDeepToString();
        cloneIsShallow();
        sharedRowLiteral();
        fillDoesNotWorkOnTwoD();
        traversalOrder();
    }

    /** The lengths are per-row, because the rows are separate objects. */
    private static void arrayOfArrays() {
        int[][] m = new int[3][4];

        System.out.println("── An array of arrays ──");
        System.out.println("  int[][] m = new int[3][4];");
        System.out.println("  m.getClass()    = " + m.getClass().getSimpleName());
        System.out.println("  m[0].getClass() = " + m[0].getClass().getSimpleName()
                + "     <- each row is its own int[]");
        System.out.println("  m.length        = " + m.length + "        (number of ROWS)");
        System.out.println("  m[0].length     = " + m[0].length + "        (columns of THAT row)");
        System.out.println();
        System.out.println("  There is no m.length for columns, because columns are");
        System.out.println("  not a property of the outer array.");
        System.out.println();
    }

    /** new int[3][] allocates the outer array only — rows start as null. */
    private static void rowsCanBeMissing() {
        int[][] partial = new int[3][];

        System.out.println("── Rows can be missing ──");
        System.out.println("  new int[3][]  -> " + Arrays.toString(partial) + "   <- nulls, not empty arrays");

        try {
            System.out.println(partial[0][0]);
        } catch (NullPointerException e) {
            System.out.println("  partial[0][0] -> NullPointerException");
        }

        partial[0] = new int[2];
        System.out.println("  after partial[0] = new int[2] -> " + Arrays.deepToString(partial));
        System.out.println();
        System.out.println("  `new int[3][4]` is shorthand for allocating the outer");
        System.out.println("  array AND all three rows. The two-step form lets rows");
        System.out.println("  differ — or stay absent.");
        System.out.println();
    }

    /** Rows may have different lengths. Nothing forbids it. */
    private static void jaggedIsLegal() {
        int[][] triangle = new int[4][];
        for (int i = 0; i < triangle.length; i++) {
            triangle[i] = new int[i + 1];               // row i has i+1 slots
            Arrays.fill(triangle[i], i + 1);
        }

        System.out.println("── Jagged arrays ──");
        for (int[] row : triangle) {
            System.out.println("   length " + row.length + "  " + Arrays.toString(row));
        }
        System.out.println();
        System.out.println("  A jagged array is the natural shape for triangular data");
        System.out.println("  — Pascal's triangle (Day 18) wastes half a rectangle.");
        System.out.println();
        System.out.println("  So ALWAYS write the inner loop as m[i].length, never a");
        System.out.println("  captured `cols`. The captured version is a latent");
        System.out.println("  ArrayIndexOutOfBoundsException.");
        System.out.println();
    }

    /** toString sees only the row references; deepToString recurses. */
    private static void printingNeedsDeepToString() {
        int[][] m = { { 1, 2 }, { 3, 4 } };

        System.out.println("── Printing ──");
        System.out.println("  Arrays.toString     " + Arrays.toString(m));
        System.out.println("  Arrays.deepToString " + Arrays.deepToString(m));
        System.out.println();
        System.out.println("  toString prints each ROW's default Object.toString —");
        System.out.println("  type plus hashcode — because the elements are references.");
        System.out.println("  Same reason Arrays.equals needs deepEquals for 2D.");
        System.out.println();
    }

    /**
     * THE trap. clone() copies the outer array — but its elements are
     * references, so both copies point at the SAME rows.
     */
    private static void cloneIsShallow() {
        int[][] original = { { 1, 2 }, { 3, 4 } };
        int[][] shallow = original.clone();
        int[][] deep = deepCopy(original);

        shallow[0][0] = 99;

        System.out.println("── clone() is shallow ──");
        System.out.println("  int[][] shallow = original.clone();");
        System.out.println("  shallow[0][0] = 99;");
        System.out.println();
        System.out.println("  original " + Arrays.deepToString(original) + "   <- CHANGED");
        System.out.println("  shallow  " + Arrays.deepToString(shallow));
        System.out.println("  rows shared? original[0] == shallow[0] : "
                + (original[0] == shallow[0]));
        System.out.println();
        System.out.println("  deep copy " + Arrays.deepToString(deep) + "   <- unaffected");
        System.out.println();
        System.out.println("  clone() duplicated the outer array of REFERENCES. The");
        System.out.println("  rows were never copied. For a real copy, clone each row.");
        System.out.println();
    }

    /** Copies every row, so the result shares nothing with the input. */
    private static int[][] deepCopy(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();                // row-by-row
        }
        return copy;
    }

    /** The same aliasing, created deliberately by reusing one row. */
    private static void sharedRowLiteral() {
        int[] row = { 0, 0, 0 };
        int[][] shared = { row, row, row };             // three refs, ONE array

        shared[0][0] = 7;

        System.out.println("── One row used three times ──");
        System.out.println("  int[] row = {0,0,0};  int[][] m = { row, row, row };");
        System.out.println("  m[0][0] = 7  ->  " + Arrays.deepToString(shared));
        System.out.println("  All three \"rows\" changed — they are one object.");
        System.out.println();
    }

    /**
     * Arrays.fill(int[][], int) resolves to fill(Object[], Object): an int[][]
     * IS an Object[], and the int is boxed to Integer. It compiles, then throws
     * — Day 19's covariance failure, arriving through a library method.
     */
    private static void fillDoesNotWorkOnTwoD() {
        int[][] m = new int[2][3];

        System.out.println("── Arrays.fill on a 2D array ──");
        System.out.println("  Arrays.fill(m, 5);   // compiles!");

        try {
            Arrays.fill(m, 5);
            System.out.println("  filled: " + Arrays.deepToString(m));
        } catch (ArrayStoreException e) {
            System.out.println("  -> ArrayStoreException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  int[][] IS an Object[], so this binds to");
        System.out.println("  fill(Object[], Object) and tries to store an Integer");
        System.out.println("  where an int[] belongs — Day 19's covariance hole,");
        System.out.println("  reached through the standard library.");
        System.out.println();
        System.out.println("  Correct: loop the rows.");
        for (int[] r : m) {
            Arrays.fill(r, 5);
        }
        System.out.println("  for (int[] r : m) Arrays.fill(r, 5);  -> "
                + Arrays.deepToString(m));
        System.out.println();
    }

    /** Row-major is the natural order; it is also the faster one. */
    private static void traversalOrder() {
        int[][] m = { { 1, 2, 3 }, { 4, 5, 6 } };

        StringBuilder rowMajor = new StringBuilder();
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                rowMajor.append(m[i][j]).append(' ');
            }
        }

        StringBuilder colMajor = new StringBuilder();
        for (int j = 0; j < m[0].length; j++) {
            for (int i = 0; i < m.length; i++) {
                colMajor.append(m[i][j]).append(' ');
            }
        }

        System.out.println("── Traversal order ──");
        System.out.println("  row-major (i outer): " + rowMajor.toString().trim());
        System.out.println("  col-major (j outer): " + colMajor.toString().trim());
        System.out.println();
        System.out.println("  Row-major walks each row's memory in sequence, so it is");
        System.out.println("  cache-friendlier for large matrices. Column-major also");
        System.out.println("  breaks on jagged input: m[0].length is not every row's.");
    }
}
