/**
 * Day 35 — the parent class.
 *
 * Nothing in here knows that subclasses exist. A class does not opt in to
 * being extended; any class that is not `final` can be.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-17
 */
public class Employee {

    /** Private: a Manager object HAS this field, but Manager's code cannot name it. */
    private final String name;
    private final double monthlySalary;

    public Employee(String name, double monthlySalary) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (monthlySalary < 0) {
            throw new IllegalArgumentException("salary cannot be negative: " + monthlySalary);
        }
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    public String getName() {
        return name;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public double annualPay() {
        return monthlySalary * 12;
    }

    public String badge() {
        return "[" + name + "]";
    }
}
