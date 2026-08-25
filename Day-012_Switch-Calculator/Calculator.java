import java.util.Scanner;

/**
 * Day 12 — a simple calculator with switch-case.
 *
 * The chapter exercise. A calculator is the natural fit for switch: one
 * variable (the operator) tested against a fixed set of discrete constants —
 * exactly what a ladder handles badly and switch handles well (Day 10).
 *
 * Run:  printf '12 + 8\n' | java Calculator
 *       printf '7 / 0\n'  | java Calculator
 *
 * @author  Divesh Agarwal
 * @since   2026-08-25
 */
public class Calculator {

    public static void main(String[] args) {
        System.out.println("Day 12 — Calculator (switch-case)");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter: <number> <op> <number>   (op is + - * / %): ");

            double left = sc.nextDouble();
            char op = sc.next().charAt(0);      // one token, first character
            double right = sc.nextDouble();

            System.out.println();
            System.out.println("  " + format(left) + " " + op + " " + format(right));
            System.out.println("  = " + calculate(left, op, right));
        }
    }

    /**
     * Classic switch with explicit breaks — the form the chapter teaches.
     *
     * Every case ends in break or return. Skipping one causes fall-through
     * into the next case, which is the topic's signature bug (SwitchTraps).
     *
     * @param left  left operand
     * @param op    one of + - * / %
     * @param right right operand
     * @return the result, or an error message for bad input
     */
    private static String calculate(double left, char op, double right) {
        switch (op) {
            case '+':
                return format(left + right);

            case '-':
                return format(left - right);

            case '*':
                return format(left * right);

            case '/':
                // Integer division by zero throws, but these are doubles, so
                // Java would hand back Infinity or NaN instead (Day 7).
                // Neither is a useful answer for a calculator.
                if (right == 0) {
                    return "undefined (division by zero)";
                }
                return format(left / right);

            case '%':
                if (right == 0) {
                    return "undefined (modulo by zero)";
                }
                // Remember: % takes the sign of the DIVIDEND (Day 7).
                return format(left % right);

            default:
                return "unknown operator '" + op + "' — use + - * / %";
        }
    }

    /** Trims the trailing .0 so whole results read as integers. */
    private static String format(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
