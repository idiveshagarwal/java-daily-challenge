import java.util.List;

/**
 * Day 36, part 2 — the object decides, not the variable.
 *
 * Compile with both classes:
 *     javac Employee.java Manager.java OverridingDemo.java && java OverridingDemo
 *
 * @author  Divesh Agarwal
 * @since   2026-09-18
 */
public class OverridingDemo {

    public static void main(String[] args) {
        System.out.println("Day 36 - Method overriding and super");
        System.out.println();

        theObjectDecides();
        superReachesTheReplacedVersion();
        theParentCallsTheChildsVersion();
        covariantReturns();
    }

    private static Manager priya() {
        Manager priya = new Manager("Priya", 90_000, 0.10);
        priya.addReport("Arjun");
        priya.addReport("Meera");
        return priya;
    }

    /** Day 35: the reference type decides what you may CALL. Today: which body RUNS. */
    private static void theObjectDecides() {
        List<Employee> payroll = List.of(
                new Employee("Arjun", 50_000),
                priya());

        System.out.println("-- 1. Same call, different body --");
        for (Employee e : payroll) {
            System.out.printf("  %-18s annualPay() = %-12s %s%n",
                    e.getClass().getSimpleName() + " " + e.getName(),
                    e.annualPay(),
                    e.describe());
        }
        System.out.println();
        System.out.println("  Both variables are declared Employee. Java picks the method");
        System.out.println("  body from the OBJECT's class at runtime — dynamic dispatch.");
        System.out.println("  The loop never mentions Manager, and never needs to.");
        System.out.println();
    }

    /** super.method() is the only way to reach the version you just replaced. */
    private static void superReachesTheReplacedVersion() {
        Manager priya = priya();

        System.out.println("-- 2. super.annualPay() extends instead of replacing --");
        System.out.println("  Employee.annualPay()  : " + (priya.getMonthlySalary() * 12)
                + "   <- the base calculation");
        System.out.println("  Manager.annualPay()   : " + priya.annualPay()
                + "   <- super.annualPay() * 1.10");
        System.out.println("  Manager.describe()    : " + priya.describe());
        System.out.println();
        System.out.println("  Inside Manager.annualPay(), `this.annualPay()` would call the");
        System.out.println("  override again and recurse until StackOverflowError. `super.`");
        System.out.println("  is not a variable — it is an instruction to the compiler to");
        System.out.println("  call Employee's version on this same object.");
        System.out.println();
    }

    /** The flip side: parent code silently starts running child code. */
    private static void theParentCallsTheChildsVersion() {
        Manager priya = priya();

        System.out.println("-- 3. Employee.describe() calls the child's annualPay() --");
        System.out.println("  Employee.describe() is written as:");
        System.out.println("      return name + \" earns \" + annualPay();");
        System.out.println("  and for a Manager it produces:");
        System.out.println("      " + priya.describe());
        System.out.println();
        System.out.println("  99000.0 * 12 = 1188000.0 — the bonus is in there, although");
        System.out.println("  Employee.java has never heard of a bonus. The unqualified");
        System.out.println("  call annualPay() means this.annualPay(), and `this` is a");
        System.out.println("  Manager. That is why a constructor must not call overridable");
        System.out.println("  methods (Day 35): it would run child code too early.");
        System.out.println();
    }

    private static void covariantReturns() {
        Manager priya = priya();

        Manager clone = priya.copy();          // no cast: copy() returns Manager
        clone.addReport("Sanjay");

        Employee asEmployee = priya;
        Employee employeeClone = asEmployee.copy();

        System.out.println("-- 4. Covariant return types --");
        System.out.println("  Employee.copy() returns Employee; Manager.copy() returns Manager");
        System.out.println("  Manager clone = priya.copy();   <- compiles, no cast needed");
        System.out.println("  clone reports        : " + clone.getReports());
        System.out.println("  original reports     : " + priya.getReports() + "   <- independent");
        System.out.println("  asEmployee.copy() is declared Employee, actual class : "
                + employeeClone.getClass().getSimpleName());
        System.out.println();
        System.out.println("  An override may narrow the return type to a subtype. It may");
        System.out.println("  not widen it, and the parameter list must match exactly —");
        System.out.println("  change that and you have written a different method.");
    }
}
