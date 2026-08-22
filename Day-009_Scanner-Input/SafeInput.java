import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Day 9, part 3 — reading input that might be wrong.
 *
 * Real input is hostile: users type letters where numbers belong, press enter
 * on empty lines, and close the stream early. Three failures matter:
 *
 *   InputMismatchException  next token is not the requested type
 *   NoSuchElementException  no token at all (end of input / closed stream)
 *   infinite loops          the classic consequence of catching the first
 *                           exception WITHOUT consuming the bad token
 *
 * Run:  printf 'abc\n-5\n30\nDivesh\n' | java SafeInput
 *
 * @author  Divesh Agarwal
 * @since   2026-08-22
 */
public class SafeInput {

    public static void main(String[] args) {
        System.out.println("Day 9 — Reading input safely");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        whyNaiveLoopsSpin();

        int age = readIntInRange(sc, "Age (1-120)", 1, 120);
        String name = readNonEmptyLine(sc, "Name");

        System.out.println();
        System.out.println("── Accepted ──");
        System.out.println("age  = " + age);
        System.out.println("name = " + name);
    }

    private static void whyNaiveLoopsSpin() {
        System.out.println("── Why naive retry loops spin forever ──");
        System.out.println("  while (true) {");
        System.out.println("      try { return sc.nextInt(); }");
        System.out.println("      catch (InputMismatchException e) { /* retry */ }");
        System.out.println("  }");
        System.out.println();
        System.out.println("InputMismatchException does NOT consume the offending");
        System.out.println("token. \"abc\" is still sitting there, so the next");
        System.out.println("nextInt() fails on the same token — forever.");
        System.out.println("The catch block MUST discard it, e.g. with sc.nextLine().");
        System.out.println();
    }

    /**
     * Reads an int, re-prompting until it parses and falls in range.
     *
     * Uses hasNextInt() rather than catching InputMismatchException — testing
     * without consuming is simpler than cleaning up afterwards.
     *
     * @param sc    the scanner to read from
     * @param label prompt text
     * @param min   inclusive lower bound
     * @param max   inclusive upper bound
     * @return a validated int
     */
    private static int readIntInRange(Scanner sc, String label, int min, int max) {
        while (true) {
            System.out.print(label + ": ");

            if (!sc.hasNext()) {                      // stream ended
                throw new NoSuchElementException("input ended while reading " + label);
            }
            if (!sc.hasNextInt()) {
                String bad = sc.next();               // consume it, or we spin
                System.out.println("  not a whole number: \"" + bad + "\" — try again");
                continue;
            }

            int value = sc.nextInt();
            sc.nextLine();                            // flush the newline (Fix 1)

            if (value < min || value > max) {
                System.out.println("  out of range (" + min + "-" + max + ") — try again");
                continue;
            }
            return value;
        }
    }

    /** Reads a line, re-prompting while it is blank. */
    private static String readNonEmptyLine(Scanner sc, String label) {
        while (true) {
            System.out.print(label + ": ");

            if (!sc.hasNextLine()) {
                throw new NoSuchElementException("input ended while reading " + label);
            }

            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("  cannot be blank — try again");
                continue;
            }
            return line;
        }
    }

    /**
     * The catch-based equivalent, kept for reference. Note the sc.nextLine()
     * inside the catch — without it this loop never terminates.
     */
    @SuppressWarnings("unused")
    private static int readIntByCatching(Scanner sc, String label) {
        while (true) {
            System.out.print(label + ": ");
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                sc.nextLine();          // ESSENTIAL — discards the bad token
                System.out.println("  not a number — try again");
            }
        }
    }
}
