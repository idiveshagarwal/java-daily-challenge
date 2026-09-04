import java.util.Arrays;
import java.util.Scanner;

/**
 * Day 22 — 2D arrays via matrix addition.
 *
 * Addition is elementwise: C[i][j] = A[i][j] + B[i][j]. Trivial arithmetic,
 * which makes it a good vehicle for the part that is not trivial — Java has no
 * "matrix" type. A 2D array is an ARRAY OF ARRAYS, so "same dimensions" and
 * even "rectangular" are assumptions the code must check, not guarantees.
 *
 * Run:  printf '2 3\n1 2 3 4 5 6\n10 20 30 40 50 60\n' | java MatrixAddition
 *
 * @author  Divesh Agarwal
 * @since   2026-09-04
 */
public class MatrixAddition {

    public static void main(String[] args) {
        System.out.println("Day 22 — Matrix addition");
        System.out.println();

        int rows;
        int cols;
        int[][] a;
        int[][] b;

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Rows and columns: ");
            rows = sc.hasNextInt() ? sc.nextInt() : 2;
            cols = sc.hasNextInt() ? sc.nextInt() : 3;

            a = read(sc, rows, cols, "A");
            b = read(sc, rows, cols, "B");
        }

        System.out.println();
        print("A", a);
        print("B", b);
        print("A + B", add(a, b));

        System.out.println();
        dimensionsMustMatch();
        rectangularityIsNotGuaranteed();
    }

    /**
     * Elementwise addition.
     *
     * Both guards matter, and neither is paranoia: Java will happily hand you
     * matrices of different sizes, or a jagged one, and the loops would then
     * throw ArrayIndexOutOfBoundsException somewhere in the middle — after
     * already writing part of the result.
     *
     * @throws IllegalArgumentException if the shapes differ or either is jagged
     */
    public static int[][] add(int[][] a, int[][] b) {
        requireSameShape(a, b);

        int[][] sum = new int[a.length][];

        for (int i = 0; i < a.length; i++) {
            sum[i] = new int[a[i].length];              // per-row length
            for (int j = 0; j < a[i].length; j++) {
                sum[i][j] = a[i][j] + b[i][j];
            }
        }
        return sum;
    }

    /** Checks row count AND every row's length — a 2D array guarantees neither. */
    private static void requireSameShape(int[][] a, int[][] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "row count differs: " + a.length + " vs " + b.length);
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null || b[i] == null) {
                throw new IllegalArgumentException("row " + i + " is null");
            }
            if (a[i].length != b[i].length) {
                throw new IllegalArgumentException(
                        "row " + i + " length differs: " + a[i].length + " vs " + b[i].length);
            }
        }
    }

    private static int[][] read(Scanner sc, int rows, int cols, String name) {
        int[][] m = new int[rows][cols];
        System.out.print("Matrix " + name + " (" + rows + "x" + cols + ") values: ");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] = sc.hasNextInt() ? sc.nextInt() : 0;
            }
        }
        return m;
    }

    /** Prints with a width computed from the widest entry (Day 18's lesson). */
    private static void print(String label, int[][] m) {
        int width = 1;
        for (int[] row : m) {
            for (int v : row) {
                width = Math.max(width, String.valueOf(v).length());
            }
        }

        System.out.println("  " + label + ":");
        for (int[] row : m) {
            System.out.print("   ");
            for (int v : row) {
                System.out.printf(" %" + width + "d", v);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void dimensionsMustMatch() {
        int[][] two = { { 1, 2 }, { 3, 4 } };
        int[][] three = { { 1, 2, 3 }, { 4, 5, 6 } };

        System.out.println("── Dimensions must match ──");
        try {
            add(two, three);
        } catch (IllegalArgumentException e) {
            System.out.println("  2x2 + 2x3 -> IllegalArgumentException: " + e.getMessage());
        }
        System.out.println("  Without the guard this throws part-way through, having");
        System.out.println("  already written some of the result.");
        System.out.println();
    }

    /**
     * The assumption worth making explicit: nothing in the type system says a
     * 2D array is rectangular. Rows are separate objects with separate lengths.
     */
    private static void rectangularityIsNotGuaranteed() {
        int[][] jagged = { { 1, 2 }, { 3 } };
        int[][] rect = { { 1, 2 }, { 3, 4 } };

        System.out.println("── Rectangularity is not guaranteed ──");
        System.out.println("  jagged = " + Arrays.deepToString(jagged) + "   <- perfectly legal");
        try {
            add(jagged, rect);
        } catch (IllegalArgumentException e) {
            System.out.println("  jagged + rect -> IllegalArgumentException: " + e.getMessage());
        }
        System.out.println();
        System.out.println("  `int[][]` means \"array of int[]\", not \"matrix\". If your");
        System.out.println("  algorithm needs a rectangle, check for one.");
    }
}
