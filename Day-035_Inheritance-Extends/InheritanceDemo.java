import java.util.List;

/**
 * Day 35, part 2 — what `extends` gives the child, and what "is-a" means.
 *
 * Compile with both classes:
 *     javac Employee.java Manager.java InheritanceDemo.java && java InheritanceDemo
 *
 * @author  Divesh Agarwal
 * @since   2026-09-17
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        System.out.println("Day 35 - Inheritance with extends");
        System.out.println();

        theChildGetsTheParentsMethods();
        aManagerIsAnEmployee();
        theReferenceTypeDecidesWhatYouCanCall();
        everyChainEndsAtObject();
    }

    private static void theChildGetsTheParentsMethods() {
        Manager priya = new Manager("Priya", 90_000);
        priya.addReport("Arjun");
        priya.addReport("Meera");

        System.out.println("-- 1. Inherited for free --");
        System.out.println("  inherited  getName()        : " + priya.getName());
        System.out.println("  inherited  getMonthlySalary(): " + priya.getMonthlySalary());
        System.out.println("  inherited  annualPay()      : " + priya.annualPay());
        System.out.println("  inherited  badge()          : " + priya.badge());
        System.out.println("  own        teamSummary()    : " + priya.teamSummary());
        System.out.println();
        System.out.println("  Manager.java declares none of the first four. It also never");
        System.out.println("  touches `name` directly: that field is private to Employee,");
        System.out.println("  so the child reads it through getName() like anyone else.");
        System.out.println();
    }

    /** Anywhere an Employee is expected, a Manager is accepted. */
    private static void aManagerIsAnEmployee() {
        Manager priya = new Manager("Priya", 90_000);
        List<Employee> payroll = List.of(
                new Employee("Arjun", 50_000),
                new Employee("Meera", 55_000),
                priya);                                  // no cast needed

        double total = 0;
        for (Employee e : payroll) {
            total += e.annualPay();
        }

        System.out.println("-- 2. A Manager IS an Employee --");
        System.out.println("  payroll size        : " + payroll.size());
        System.out.println("  total annual pay    : " + total);
        System.out.println("  priya instanceof Employee : " + (priya instanceof Employee));
        System.out.println();
        System.out.println("  The payroll loop was written for Employee and never mentions");
        System.out.println("  Manager, yet handles one. The reverse is not true: an");
        System.out.println("  Employee is not a Manager.");

        Employee plain = new Employee("Arjun", 50_000);
        System.out.println("  plain instanceof Manager  : " + (plain instanceof Manager));
        System.out.println();
    }

    private static void theReferenceTypeDecidesWhatYouCanCall() {
        Employee asEmployee = new Manager("Priya", 90_000);

        System.out.println("-- 3. The variable's type decides what you may call --");
        System.out.println("  Employee asEmployee = new Manager(\"Priya\", 90_000);");
        System.out.println("  asEmployee.getName()  -> " + asEmployee.getName());
        System.out.println("  asEmployee.addReport(\"Arjun\")  -> does not compile:");
        System.out.println("      error: cannot find symbol");
        System.out.println("        symbol:   method addReport(String)");
        System.out.println("        location: variable asEmployee of type Employee");
        System.out.println();

        // The OBJECT is still a Manager. Checking and narrowing gets it back.
        if (asEmployee instanceof Manager m) {
            m.addReport("Arjun");
            System.out.println("  after instanceof Manager m : " + m.teamSummary());
        }
        System.out.println("  runtime class              : " + asEmployee.getClass().getName());
        System.out.println();
        System.out.println("  The compiler only knows the declared type. The object never");
        System.out.println("  stopped being a Manager; the reference just hid the extras.");
        System.out.println();
    }

    /** Every class without `extends` silently extends Object. */
    private static void everyChainEndsAtObject() {
        System.out.println("-- 4. Every chain ends at Object --");
        Class<?> c = Manager.class;
        StringBuilder chain = new StringBuilder();
        while (c != null) {
            chain.append(c.getName());
            c = c.getSuperclass();
            if (c != null) {
                chain.append(" -> ");
            }
        }
        System.out.println("  " + chain);
        System.out.println();
        System.out.println("  Employee never wrote `extends Object`; javac added it. That is");
        System.out.println("  why toString(), equals() and hashCode() exist on every object.");

        Manager priya = new Manager("Priya", 90_000);
        String defaultToString = priya.toString();
        System.out.println("  priya.toString() starts with : "
                + defaultToString.substring(0, defaultToString.indexOf('@') + 1)
                + "   <- Object's version, inherited twice");
    }
}
