import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Day 36 — the child, overriding rather than replacing.
 *
 * Every override here calls super first. `super.annualPay()` runs Employee's
 * version of the method the child has just replaced, which is the only way to
 * reach it — `this.annualPay()` would call the override again, forever.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-18
 */
public class Manager extends Employee {

    private final double bonusRate;
    private final List<String> reports = new ArrayList<>();

    public Manager(String name, double monthlySalary, double bonusRate) {
        super(name, monthlySalary);            // super(...) : the constructor
        this.bonusRate = bonusRate;
    }

    public void addReport(String employeeName) {
        reports.add(employeeName);
    }

    public List<String> getReports() {
        return Collections.unmodifiableList(reports);
    }

    /** Same signature as Employee.annualPay(), so it replaces it at runtime. */
    @Override
    public double annualPay() {
        return super.annualPay() * (1 + bonusRate);   // super. : the method
    }

    @Override
    public String describe() {
        return super.describe() + ", manages " + reports.size();
    }

    /**
     * COVARIANT RETURN: the parent declares Employee, this narrows it to
     * Manager. Legal since Java 5, and it saves every caller a cast.
     */
    @Override
    public Manager copy() {
        Manager copy = new Manager(getName(), getMonthlySalary(), bonusRate);
        copy.reports.addAll(reports);
        return copy;
    }

    @Override
    public String toString() {
        return "Manager(" + getName() + ")";
    }
}
