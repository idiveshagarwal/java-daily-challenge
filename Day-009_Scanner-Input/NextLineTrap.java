import java.util.Scanner;

/**
 * Day 9, part 2 — the nextInt()/nextLine() trap, and three ways out.
 *
 * THE CAUSE, in one sentence: nextInt() consumes the digits but not the
 * newline after them, so the very next nextLine() finds that leftover newline,
 * returns the empty string, and appears to have been "skipped".
 *
 * Input "25\nDivesh Agarwal\n" sits in the buffer like this:
 *
 *     2 5 \n D i v e s h ...
 *          ^
 *          nextInt() stops HERE, leaving \n unread
 *
 * Run:  printf '25\nDivesh Agarwal\n25\nDivesh Agarwal\n' | java NextLineTrap
 *
 * @author  Divesh Agarwal
 * @since   2026-08-22
 */
public class NextLineTrap {

    public static void main(String[] args) {
        System.out.println("Day 9 — The nextLine() trap");
        System.out.println();

        Scanner sc = new Scanner(System.in);

        theBug(sc);
        fixOne(sc);

        System.out.println();
        explainTheOtherFixes();

        // Deliberately NOT closing sc — see the README. Closing a Scanner
        // over System.in closes System.in permanently for this JVM.
    }

    /** Reproduces the bug: name comes back empty. */
    private static void theBug(Scanner sc) {
        System.out.println("── The bug ──");
        System.out.print("Age  : ");
        int age = sc.nextInt();

        System.out.print("Name : ");
        String name = sc.nextLine();     // gets the leftover "" after 25

        System.out.println();
        System.out.println("age  = [" + age + "]");
        System.out.println("name = [" + name + "]   <- EMPTY, and it never waited for input");
        System.out.println();

        sc.nextLine();   // discard the real name line so fixOne starts clean
    }

    /** Fix 1: consume the dangling newline with an extra nextLine(). */
    private static void fixOne(Scanner sc) {
        System.out.println("── Fix 1: flush the newline ──");
        System.out.print("Age  : ");
        int age = sc.nextInt();
        sc.nextLine();                   // <-- the fix: eat the leftover newline

        System.out.print("Name : ");
        String name = sc.nextLine();

        System.out.println();
        System.out.println("age  = [" + age + "]");
        System.out.println("name = [" + name + "]   <- correct");
    }

    private static void explainTheOtherFixes() {
        System.out.println("── Fix 2: read lines only, parse yourself ──");
        System.out.println("  int age = Integer.parseInt(sc.nextLine().trim());");
        System.out.println("  Never mix token methods with line methods, and the");
        System.out.println("  problem cannot occur. Best default for line-based input.");
        System.out.println();
        System.out.println("── Fix 3: use tokens only ──");
        System.out.println("  int age = sc.nextInt();  String name = sc.next();");
        System.out.println("  But next() stops at whitespace, so \"Divesh Agarwal\"");
        System.out.println("  would read as just \"Divesh\". Only safe for single words.");
        System.out.println();
        System.out.println("Rule: pick line-based OR token-based and stay consistent.");
    }
}
