/**
 * Day 12, part 3 — how a classic switch goes wrong.
 *
 * Every trap here belongs to the CLASSIC form. The arrow form (Java 14+) is
 * immune to the first, which is the main argument for preferring it.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-25
 */
public class SwitchTraps {

    public static void main(String[] args) {
        System.out.println("Day 12 — Switch traps");
        System.out.println();

        trap1_fallThrough();
        trap2_intentionalFallThrough();
        trap3_stringSwitchIsCaseSensitive();
        trap4_nullSelector();
        trap5_defaultIsNotAFallback();
    }

    /**
     * TRAP 1 — a missing break does not end the switch; execution runs on into
     * the NEXT case body, ignoring its label entirely.
     */
    private static void trap1_fallThrough() {
        System.out.println("── Trap 1: missing break ──");
        System.out.println("  case 1: print(\"one\");     // no break");
        System.out.println("  case 2: print(\"two\");     // no break");
        System.out.println("  case 3: print(\"three\"); break;");
        System.out.println();

        for (int n = 1; n <= 3; n++) {
            System.out.print("  n=" + n + " -> ");
            switch (n) {
                case 1: System.out.print("one ");
                case 2: System.out.print("two ");
                case 3: System.out.print("three ");
                    break;
                default: System.out.print("other ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("  n=1 printed all three. Once a label matches, the");
        System.out.println("  remaining labels are NOT tested — only break stops it.");
        System.out.println();
        System.out.println("  javac is SILENT by default. Turn the check on with:");
        System.out.println("    javac -Xlint:fallthrough SwitchTraps.java");
        System.out.println("    warning: [fallthrough] possible fall-through into case");
        System.out.println();
    }

    /** Fall-through is occasionally what you want — say so in a comment. */
    private static void trap2_intentionalFallThrough() {
        System.out.println("── Trap 2: when fall-through is deliberate ──");

        for (char grade : new char[] { 'A', 'C', 'F' }) {
            System.out.print("  grade " + grade + " -> ");
            switch (grade) {
                case 'A':
                case 'B':
                case 'C':
                    System.out.println("pass");
                    break;
                case 'D':
                case 'F':
                    System.out.println("fail");
                    break;
                default:
                    System.out.println("unknown");
            }
        }

        System.out.println();
        System.out.println("  Stacked empty labels share one body — legitimate, but");
        System.out.println("  indistinguishable from a forgotten break at a glance.");
        System.out.println("  Prefer `case 'A', 'B', 'C' ->` which cannot be misread.");
        System.out.println();
    }

    /** TRAP 3 — String cases match exactly, including case. */
    private static void trap3_stringSwitchIsCaseSensitive() {
        System.out.println("── Trap 3: String switch is case-sensitive ──");

        for (String cmd : new String[] { "add", "ADD" }) {
            String result = switch (cmd) {
                case "add" -> "matched";
                default -> "fell through to default";
            };
            System.out.printf("  \"%s\" -> %s%n", cmd, result);
        }

        System.out.println("  Normalise first: switch (cmd.toLowerCase()) { ... }");
        System.out.println();
    }

    /**
     * TRAP 4 — switching on a null String throws. `default` does NOT catch it:
     * the switch must read the value to compare it, and that read is the NPE.
     */
    private static void trap4_nullSelector() {
        String command = null;

        System.out.println("── Trap 4: null selector ──");
        try {
            switch (command) {
                case "add" -> System.out.println("  add");
                default -> System.out.println("  default");
            }
        } catch (NullPointerException e) {
            System.out.println("  switch (null) -> NullPointerException");
            System.out.println("  default did NOT catch it — the NPE happens before");
            System.out.println("  any label is considered.");
        }
        System.out.println("  Guard with `if (cmd == null)` before the switch.");
        System.out.println();
    }

    /** TRAP 5 — default runs when nothing matched, wherever it is written. */
    private static void trap5_defaultIsNotAFallback() {
        System.out.println("── Trap 5: default is position-independent ──");

        System.out.print("  n=99 with default written FIRST -> ");
        int n = 99;
        switch (n) {
            default:
                System.out.print("default ");
                break;
            case 1:
                System.out.print("one ");
                break;
        }
        System.out.println();
        System.out.println("  It still runs last-resort, not first. But a default");
        System.out.println("  written mid-switch WILL fall through into the case");
        System.out.println("  below it if you forget the break — put it last.");
    }
}
