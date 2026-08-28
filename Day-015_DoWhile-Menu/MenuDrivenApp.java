import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Day 15 — a menu-driven program with do-while.
 *
 * The canonical do-while use case: a menu must be shown at least once, then
 * repeated until the user chooses to leave. The exit condition is only known
 * AFTER the body has run, which is precisely what do-while is for.
 *
 * The options are this fortnight's exercises, so the menu doubles as a
 * capstone for Ch 5 and Ch 6:
 *   1  reverse a number      (Day 14)
 *   2  largest of three      (Day 11)
 *   3  multiplication table  (Day 13)
 *   4  calculator            (Day 12)
 *
 * Run:  printf '1\n12345\n0\n' | java MenuDrivenApp
 *
 * @author  Divesh Agarwal
 * @since   2026-08-28
 */
public class MenuDrivenApp {

    private static final int EXIT = 0;

    public static void main(String[] args) {
        System.out.println("Day 15 — Menu-driven program (do-while)");

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            showMenu();
            choice = readChoice(sc);

            switch (choice) {
                case 1 -> reverseNumber(sc);
                case 2 -> largestOfThree(sc);
                case 3 -> multiplicationTable(sc);
                case 4 -> calculator(sc);
                case EXIT -> System.out.println("\nGoodbye.");
                default -> System.out.println("\n  " + choice + " is not on the menu.");
            }
        } while (choice != EXIT);
        //       ^^^^^^^^^^^^^^^ the semicolon here is REQUIRED — see DoWhileBasics

        // Deliberately not closing sc: it wraps System.in (Day 9).
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("──────────────────────────────");
        System.out.println("  1  Reverse a number");
        System.out.println("  2  Largest of three");
        System.out.println("  3  Multiplication table");
        System.out.println("  4  Calculator");
        System.out.println("  0  Exit");
        System.out.println("──────────────────────────────");
        System.out.print("  Choice: ");
    }

    /**
     * Reads the menu choice defensively.
     *
     * Two failures have to be handled or the loop misbehaves:
     *   - a non-numeric token must be CONSUMED, or hasNextInt keeps failing on
     *     it and the menu spins forever (Day 9)
     *   - the stream can END before the user picks 0, which would otherwise
     *     throw NoSuchElementException and crash the program
     *
     * @return the chosen option, or EXIT if input is exhausted
     */
    private static int readChoice(Scanner sc) {
        while (true) {
            if (!sc.hasNext()) {
                System.out.println();
                System.out.println("  (input ended — exiting)");
                return EXIT;                     // treat EOF as "quit"
            }
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();                   // flush the newline (Day 9)
                return value;
            }
            System.out.print("  \"" + sc.next() + "\" is not a number. Choice: ");
        }
    }

    /** Reads one int, or returns the fallback if input has run out. */
    private static int readInt(Scanner sc, String prompt, int fallback) {
        System.out.print(prompt);
        while (true) {
            if (!sc.hasNext()) {
                System.out.println("(no input)");
                return fallback;
            }
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            }
            System.out.print("  not a number, try again: " + sc.next() + " -> ");
        }
    }

    // ── the options ────────────────────────────────────────────────────────

    /** Day 14's algorithm: while loop, guarded with != 0 so negatives work. */
    private static void reverseNumber(Scanner sc) {
        int n = readInt(sc, "\n  Number to reverse: ", 0);

        long rev = 0;
        long value = n;
        while (value != 0) {                     // long accumulator: no overflow
            rev = rev * 10 + value % 10;
            value /= 10;
        }
        System.out.println("  " + n + " reversed is " + rev);
    }

    /** Day 11's algorithm, using Math.max — correct for every input. */
    private static void largestOfThree(Scanner sc) {
        int a = readInt(sc, "\n  First:  ", 0);
        int b = readInt(sc, "  Second: ", 0);
        int c = readInt(sc, "  Third:  ", 0);

        System.out.println("  Largest is " + Math.max(a, Math.max(b, c)));
    }

    /** Day 13's nested-loop table, trimmed to a single row set. */
    private static void multiplicationTable(Scanner sc) {
        int n = readInt(sc, "\n  Table of: ", 1);

        for (int i = 1; i <= 10; i++) {
            System.out.printf("  %2d x %2d = %3d%n", n, i, n * i);
        }
    }

    /** Day 12's switch calculator. */
    private static void calculator(Scanner sc) {
        int a = readInt(sc, "\n  Left:  ", 0);

        System.out.print("  Op (+ - * /): ");
        char op = sc.hasNext() ? sc.next().charAt(0) : '+';

        int b = readInt(sc, "\n  Right: ", 0);

        String result = switch (op) {
            case '+' -> String.valueOf(a + b);
            case '-' -> String.valueOf(a - b);
            case '*' -> String.valueOf(a * b);
            case '/' -> b == 0 ? "undefined (division by zero)" : String.valueOf(a / b);
            default -> "unknown operator '" + op + "'";
        };

        System.out.println("  " + a + " " + op + " " + b + " = " + result);
    }
}
