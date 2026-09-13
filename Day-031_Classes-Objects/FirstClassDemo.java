/**
 * Day 31, part 2 — using the class.
 *
 * Compile both files together:
 *     javac BankAccount.java FirstClassDemo.java && java FirstClassDemo
 *
 * @author  Divesh Agarwal
 * @since   2026-09-13
 */
public class FirstClassDemo {

    public static void main(String[] args) {
        System.out.println("Day 31 - Classes and objects");
        System.out.println();

        creatingObjects();
        eachObjectHasItsOwnState();
        staticIsShared();
        objectsRefuseInvalidState();
        variablesHoldReferences();
    }

    private static void creatingObjects() {
        System.out.println("-- Creating objects --");
        System.out.println("  BankAccount a = new BankAccount(\"Asha\", 500);");
        System.out.println();

        BankAccount a = new BankAccount("Asha", 500);

        System.out.println("  `new` does three things:");
        System.out.println("    1. allocates memory for the fields, zeroed (Day 19's defaults)");
        System.out.println("    2. runs the constructor to set them properly");
        System.out.println("    3. returns a REFERENCE to the object");
        System.out.println();
        System.out.println("  a = " + a);
        System.out.println();
    }

    /** Two objects from one blueprint share nothing but the code. */
    private static void eachObjectHasItsOwnState() {
        BankAccount asha = new BankAccount("Asha", 500);
        BankAccount ravi = new BankAccount("Ravi", 100);

        asha.deposit(250);
        ravi.withdraw(40);

        System.out.println("-- Each object has its own state --");
        System.out.println("  asha.deposit(250); ravi.withdraw(40);");
        System.out.println("    " + asha);
        System.out.println("    " + ravi);
        System.out.println();
        System.out.println("  Same class, same methods — separate balances. A method");
        System.out.println("  call acts on the object it is called ON.");
        System.out.println();
    }

    /** A static field exists once, however many objects there are. */
    private static void staticIsShared() {
        System.out.println("-- static is shared --");
        System.out.println("  accounts opened so far: " + BankAccount.getAccountsOpened());
        new BankAccount("Meera", 0);
        System.out.println("  after one more:         " + BankAccount.getAccountsOpened());
        System.out.println();
        System.out.println("  balance is per object; accountsOpened is per class.");
        System.out.println("  Called as BankAccount.getAccountsOpened() — no object needed");
        System.out.println("  (Day 26's static vs instance).");
        System.out.println();
    }

    /**
     * The point of putting behaviour next to state: the object can reject
     * operations that would corrupt it, instead of trusting every caller.
     */
    private static void objectsRefuseInvalidState() {
        BankAccount acc = new BankAccount("Kiran", 100);

        System.out.println("-- Objects protect their own state --");
        System.out.println("  withdraw(500) on a balance of 100 -> " + acc.withdraw(500));
        System.out.println("  balance still " + acc.getBalance());

        try {
            acc.deposit(-50);
        } catch (IllegalArgumentException e) {
            System.out.println("  deposit(-50) -> IllegalArgumentException: " + e.getMessage());
        }
        try {
            new BankAccount("", 10);
        } catch (IllegalArgumentException e) {
            System.out.println("  new BankAccount(\"\", 10) -> " + e.getMessage());
        }

        System.out.println();
        System.out.println("  balance is private, so `acc.balance = -1000` does not");
        System.out.println("  compile from outside the class. Every change goes through");
        System.out.println("  a method that checks it.");
        System.out.println();
    }

    /** Day 19 and Day 26, applied to objects you wrote yourself. */
    private static void variablesHoldReferences() {
        BankAccount original = new BankAccount("Dev", 100);
        BankAccount alias = original;              // copies the reference
        BankAccount other = new BankAccount("Dev", 100);

        alias.deposit(900);

        System.out.println("-- Variables hold references --");
        System.out.println("  BankAccount alias = original;   alias.deposit(900);");
        System.out.println("    original -> " + original + "   <- changed");
        System.out.println("    original == alias : " + (original == alias));
        System.out.println();
        System.out.println("  other has identical contents, yet:");
        System.out.println("    original == other : " + (original == other));
        System.out.println("    original.equals(other) : " + original.equals(other)
                + "   <- equals not overridden, so identity");
        System.out.println();
        System.out.println("  Like StringBuilder (Day 25), a class that does not");
        System.out.println("  override equals compares by identity.");
    }
}
