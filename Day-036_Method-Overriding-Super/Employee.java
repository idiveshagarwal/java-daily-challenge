/**
 * Day 36 — the parent, written so a child can extend its behaviour.
 *
 * Note that describe() calls annualPay(). Once a child overrides annualPay(),
 * this method starts calling the child's version — the parent's code changes
 * behaviour without being edited. That is the whole point of overriding, and
 * also the reason a constructor must never do it (Day 35, trap 3).
 *
 * @author  Divesh Agarwal
 * @since   2026-09-18
 */
public class Employee {

    private final String name;
    private final double monthlySalary;

    public Employee(String name, double monthlySalary) {
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    public String getName() {
        return name;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    /** Overridable: a subclass can change how pay is worked out. */
    public double annualPay() {
        return monthlySalary * 12;
    }

    public String describe() {
        return name + " earns " + annualPay();
    }

    /** Covariant returns: Manager narrows this to Manager. */
    public Employee copy() {
        return new Employee(name, monthlySalary);
    }

    /**
     * final: subclasses may not change this. Anything the class must be able to
     * rely on — an ID format, an invariant check — belongs here.
     */
    public final String payrollId() {
        return "EMP-" + Math.abs(name.hashCode() % 1000);
    }

    /** Overriding a method inherited from Object, like any other override. */
    @Override
    public String toString() {
        return "Employee(" + name + ")";
    }
}
