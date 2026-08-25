/**
 * Day 12, part 2 — the same calculator in three switch forms.
 *
 *   1. classic statement    case X:  ...  break;      falls through
 *   2. arrow statement      case X -> ...;            never falls through
 *   3. switch expression    var r = switch (x) {...}  produces a value
 *
 * The arrow form (Java 14+) exists precisely because fall-through was the
 * wrong default: correct code needed a break on every branch, and forgetting
 * one was silent.
 *
 * @author  Divesh Agarwal
 * @since   2026-08-25
 */
public class SwitchForms {

    public static void main(String[] args) {
        System.out.println("Day 12 — Three forms of switch");
        System.out.println();

        double a = 12, b = 4;
        for (char op : new char[] { '+', '-', '*', '/', '?' }) {
            System.out.printf("  %s %c %s   classic=%-6s arrow=%-6s expression=%s%n",
                    fmt(a), op, fmt(b),
                    classicForm(a, op, b),
                    arrowForm(a, op, b),
                    expressionForm(a, op, b));
        }

        System.out.println();
        groupedCases();
        System.out.println();
        whatYouCanSwitchOn();
    }

    /** Form 1 — classic. Needs break (or return) on every branch. */
    private static String classicForm(double l, char op, double r) {
        switch (op) {
            case '+': return fmt(l + r);
            case '-': return fmt(l - r);
            case '*': return fmt(l * r);
            case '/': return r == 0 ? "undef" : fmt(l / r);
            default:  return "?";
        }
    }

    /** Form 2 — arrow statement. No break needed; fall-through impossible. */
    private static String arrowForm(double l, char op, double r) {
        switch (op) {
            case '+' -> { return fmt(l + r); }
            case '-' -> { return fmt(l - r); }
            case '*' -> { return fmt(l * r); }
            case '/' -> { return r == 0 ? "undef" : fmt(l / r); }
            default  -> { return "?"; }
        }
    }

    /**
     * Form 3 — switch EXPRESSION. The whole switch produces a value, so it can
     * be assigned. A block branch uses `yield` to supply its value.
     *
     * Because it must produce a value on every path, the compiler enforces
     * exhaustiveness: omit `default` here and it will not compile.
     */
    private static String expressionForm(double l, char op, double r) {
        return switch (op) {
            case '+' -> fmt(l + r);
            case '-' -> fmt(l - r);
            case '*' -> fmt(l * r);
            case '/' -> {
                if (r == 0) {
                    yield "undef";      // `yield` supplies the block's value
                }
                yield fmt(l / r);
            }
            default -> "?";
        };
    }

    /** Multiple labels on one branch — the readable replacement for stacking cases. */
    private static void groupedCases() {
        System.out.println("── Grouping cases ──");
        for (int month : new int[] { 2, 4, 7 }) {
            String days = switch (month) {
                case 1, 3, 5, 7, 8, 10, 12 -> "31 days";
                case 4, 6, 9, 11 -> "30 days";
                case 2 -> "28 or 29 days";
                default -> "invalid month";
            };
            System.out.println("  month " + month + " -> " + days);
        }
        System.out.println("  Comma-separated labels replace the old trick of");
        System.out.println("  stacking empty `case` lines to share a body.");
    }

    private static void whatYouCanSwitchOn() {
        System.out.println("── Allowed selector types ──");
        System.out.println("  byte, short, char, int   and their wrappers");
        System.out.println("  String                   (Java 7+)");
        System.out.println("  enum                     (Java 5+)");
        System.out.println();
        System.out.println("  Traditionally NOT: long, float, double, boolean.");
        System.out.println("  On this JDK (" + System.getProperty("java.version") + ") those report");
        System.out.println("  \"primitive patterns are a preview feature\" rather than a");
        System.out.println("  flat type error — see the README.");
    }

    private static String fmt(double v) {
        return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
