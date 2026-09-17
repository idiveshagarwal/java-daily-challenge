import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Day 35 — the child class.
 *
 * `extends Employee` means: a Manager IS an Employee, plus whatever is written
 * here. Everything Employee exposes comes along without being redeclared.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-17
 */
public class Manager extends Employee {

    /** Only a Manager has reports. An Employee reference cannot see this. */
    private final List<String> reports = new ArrayList<>();

    /**
     * Constructors are NOT inherited. Manager must declare its own, and its
     * first job is to build the Employee part via super(...). Leave that line
     * out and javac inserts super() — which does not exist on Employee, so it
     * would not compile.
     */
    public Manager(String name, double monthlySalary) {
        super(name, monthlySalary);
    }

    /** New behaviour that only the child has. */
    public void addReport(String employeeName) {
        reports.add(employeeName);
    }

    public List<String> getReports() {
        return Collections.unmodifiableList(reports);
    }

    /**
     * Uses inherited members. `name` itself is private to Employee, so the
     * child goes through getName() like any other class would.
     */
    public String teamSummary() {
        return getName() + " manages " + reports.size() + " " + reports;
    }
}
