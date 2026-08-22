import java.util.Scanner;

/**
 * Day 9 — reading user input with Scanner.
 *
 * Scanner wraps an input source and hands back TOKENS. A token is a run of
 * characters bounded by the delimiter, which defaults to whitespace. Almost
 * every Scanner confusion comes from one fact: the token-reading methods stop
 * at the delimiter and LEAVE IT in the buffer.
 *
 * Run it interactively, or feed it from a pipe:
 *   printf 'Divesh\n25\n1.75\ntrue\n' | java ScannerBasics
 *
 * @author  Divesh Agarwal
 * @since   2026-08-22
 */
public class ScannerBasics {

    public static void main(String[] args) {
        System.out.println("Day 9 — Scanner basics");
        System.out.println();

        // try-with-resources closes the Scanner automatically.
        // NOTE: closing a Scanner over System.in also closes System.in —
        // see NextLineTrap and the README. Fine here: we read once and exit.
        try (Scanner sc = new Scanner(System.in)) {

            System.out.print("Name        : ");
            String name = sc.nextLine();          // whole line, newline consumed

            System.out.print("Age         : ");
            int age = sc.nextInt();               // one token, newline LEFT BEHIND

            System.out.print("Height (m)  : ");
            double height = sc.nextDouble();

            System.out.print("Subscribed? : ");
            boolean subscribed = sc.nextBoolean();

            System.out.println();
            System.out.println("── Read back ──");
            System.out.printf("name=%s  age=%d  height=%.2f  subscribed=%b%n",
                    name, age, height, subscribed);

            System.out.println();
            describeTheMethods();
        }
    }

    private static void describeTheMethods() {
        System.out.println("── The methods ──");
        System.out.println("nextLine()    reads to end of line, CONSUMES the newline");
        System.out.println("next()        reads ONE token, leaves the delimiter");
        System.out.println("nextInt()     one token parsed as int, leaves the delimiter");
        System.out.println("nextDouble()  likewise for double");
        System.out.println("nextBoolean() accepts \"true\"/\"false\", case-insensitive");
        System.out.println();
        System.out.println("Each next*() has a matching hasNext*() that tests");
        System.out.println("WITHOUT consuming — the basis of every validation loop.");
        System.out.println();
        System.out.println("Only nextLine() consumes its own line ending.");
        System.out.println("That asymmetry is the whole source of the classic bug.");
    }
}
