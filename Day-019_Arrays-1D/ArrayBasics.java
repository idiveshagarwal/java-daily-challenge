import java.util.Arrays;
import java.util.Scanner;

/**
 * Day 19 — one-dimensional arrays: declare, store, traverse.
 *
 * The first data structure. Three properties define everything that follows:
 *
 *   1. an array is an OBJECT, so a variable holds a reference to it
 *   2. its length is FIXED at creation and exposed as a field, not a method
 *   3. its elements are pre-filled with the type's default value
 *
 * Run:  printf '5\n10 20 30 40 50\n' | java ArrayBasics
 *
 * @author  Divesh Agarwal
 * @since   2026-09-01
 */
public class ArrayBasics {

    public static void main(String[] args) {
        System.out.println("Day 19 — 1D arrays");
        System.out.println();

        declaring();
        defaultValues();
        readFromInput();
        traversing();
        commonOperations();
    }

    /** Four ways to bring an array into existence. */
    private static void declaring() {
        int[] sized = new int[5];                    // 1. sized, filled with defaults
        int[] literal = { 10, 20, 30 };              // 2. initialiser — only at declaration
        int[] anonymous = new int[] { 1, 2, 3 };     // 3. initialiser away from declaration
        int cStyle[] = { 7, 8 };                     // 4. legal, but do not use

        System.out.println("── Declaring ──");
        System.out.println("  new int[5]           " + Arrays.toString(sized));
        System.out.println("  { 10, 20, 30 }       " + Arrays.toString(literal));
        System.out.println("  new int[]{ 1, 2, 3 } " + Arrays.toString(anonymous));
        System.out.println("  int cStyle[]         " + Arrays.toString(cStyle));
        System.out.println();
        System.out.println("  `int cStyle[]` compiles — it is inherited C syntax — but");
        System.out.println("  `int[] a` keeps the type together and is the convention.");
        System.out.println();
        System.out.println("  The bare { ... } form works ONLY at declaration:");
        System.out.println("    int[] a;  a = { 1, 2 };      // error");
        System.out.println("    int[] a;  a = new int[]{1,2}; // fine");
        System.out.println();
    }

    /**
     * Elements are never uninitialised — unlike local variables, which javac
     * refuses to read before assignment. An array is zeroed on creation.
     */
    private static void defaultValues() {
        System.out.println("── Default values ──");
        System.out.println("  int[]     " + Arrays.toString(new int[3]));
        System.out.println("  double[]  " + Arrays.toString(new double[3]));
        System.out.println("  boolean[] " + Arrays.toString(new boolean[3]));
        System.out.println("  String[]  " + Arrays.toString(new String[3]) + "   <- null, not \"\"");
        System.out.println("  char[]    prints blank; the value is '\\u0000', code "
                + (int) (new char[1])[0]);
        System.out.println();
        System.out.println("  A String[] full of nulls is the usual source of a");
        System.out.println("  surprise NullPointerException on first use.");
        System.out.println();
    }

    /** Filling an array from input — the size must be known first. */
    private static void readFromInput() {
        Scanner sc = new Scanner(System.in);

        System.out.print("How many values? ");
        int n = sc.hasNextInt() ? sc.nextInt() : 5;
        if (n < 0) {
            System.out.println("  (negative size — using 5)");
            n = 5;
        }

        int[] values = new int[n];                   // size fixed here, forever

        System.out.print("Enter " + n + " values: ");
        for (int i = 0; i < values.length; i++) {    // length is a FIELD
            values[i] = sc.hasNextInt() ? sc.nextInt() : (i + 1) * 10;
        }

        System.out.println();
        System.out.println("── Stored ──");
        System.out.println("  " + Arrays.toString(values));
        System.out.println("  length = " + values.length + "   (a field: no parentheses)");
        System.out.println();

        traverseThreeWays(values);
    }

    /** The three traversals, and what each one can and cannot do. */
    private static void traverseThreeWays(int[] values) {
        System.out.println("── Traversing ──");

        System.out.print("  indexed for : ");
        for (int i = 0; i < values.length; i++) {
            System.out.print(values[i] + " ");
        }
        System.out.println("   <- has the index, can write");

        System.out.print("  for-each    : ");
        for (int v : values) {
            System.out.print(v + " ");
        }
        System.out.println("   <- no index, read only");

        System.out.println("  Arrays      : " + Arrays.toString(values)
                + "   <- for printing, not iterating");
        System.out.println();
    }

    /**
     * The for-each loop copies each element into the loop variable, so writing
     * to that variable changes nothing. To modify an array you need the index.
     */
    private static void traversing() {
        int[] a = { 1, 2, 3 };

        for (int v : a) {
            v = 99;                                  // writes to the copy
        }
        String afterForEach = Arrays.toString(a);

        for (int i = 0; i < a.length; i++) {
            a[i] = 99;                               // writes to the array
        }

        System.out.println("── for-each cannot modify ──");
        System.out.println("  after `for (int v : a) v = 99;`  -> " + afterForEach);
        System.out.println("  after `for (i..) a[i] = 99;`     -> " + Arrays.toString(a));
        System.out.println();
        System.out.println("  for-each hands you a COPY of each element. Use it to");
        System.out.println("  read; use an indexed loop to write.");
        System.out.println();
    }

    /** The java.util.Arrays helpers worth knowing on day one. */
    private static void commonOperations() {
        int[] data = { 5, 3, 9, 1, 7 };

        int[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);

        int[] filled = new int[5];
        Arrays.fill(filled, -1);

        int[] slice = Arrays.copyOfRange(data, 1, 4);   // [1, 4)

        System.out.println("── Arrays utilities ──");
        System.out.println("  original          " + Arrays.toString(data));
        System.out.println("  copyOf + sort     " + Arrays.toString(copy));
        System.out.println("  fill(-1)          " + Arrays.toString(filled));
        System.out.println("  copyOfRange(1,4)  " + Arrays.toString(slice) + "   <- end is EXCLUSIVE");
        System.out.println();

        int sum = 0;
        int max = data[0];
        for (int v : data) {
            sum += v;
            max = Math.max(max, v);
        }
        System.out.println("  sum = " + sum + ", max = " + max + ", avg = " + (double) sum / data.length);
        System.out.println("  note the (double) cast — sum/length would be integer");
        System.out.println("  division and truncate (Day 7).");
    }
}
