import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Day 6 — final, and what "constant" actually means in Java.
 *
 * Java has no `const` keyword (it is reserved but unused — see Day 3). The job
 * is done by `final`, which means "assigned exactly once", not "immutable".
 *
 * @author  Divesh Agarwal
 * @since   2026-08-19
 */
public class ConstantsAndFinal {

    /**
     * A true constant: static + final + a compile-time constant expression.
     * By convention UPPER_SNAKE_CASE. javac inlines this value into every class
     * that reads it — see the ConstantValue attribute note in the README.
     */
    public static final int MAX_USERS = 100;

    /** static final, but NOT a compile-time constant: the value needs a call. */
    public static final long STARTED_AT = System.currentTimeMillis();

    /** A blank final: declared without a value, assigned in the constructor. */
    private final String name;

    public ConstantsAndFinal(String name) {
        this.name = name;   // must happen exactly once, in every constructor
    }

    public static void main(String[] args) {
        System.out.println("Day 6 — Constants, final and literals");
        System.out.println();

        theFourPlaces();
        finalIsNotImmutable(new ArrayList<>());
        compileTimeVsRuntime();
    }

    /** final applies to locals, fields, parameters and (not shown) methods. */
    private static void theFourPlaces() {
        final int local = 10;          // local final
        // local = 11;                 // error: cannot assign a value to final variable local

        ConstantsAndFinal obj = new ConstantsAndFinal("blank-final assigned in ctor");

        System.out.println("── Where final can appear ──");
        System.out.println("static final constant : MAX_USERS = " + MAX_USERS);
        System.out.println("blank final field     : " + obj.name);
        System.out.println("local final           : " + local);
        System.out.println("final parameter       : see finalIsNotImmutable(final List)");
        System.out.println("also: final method (cannot override), final class (cannot extend)");
        System.out.println();
    }

    /**
     * The most common misunderstanding. `final` freezes the REFERENCE, not the
     * object it points at. You cannot repoint the variable; you can freely
     * mutate what it points to.
     *
     * @param items a final parameter — cannot be reassigned inside this method
     */
    private static void finalIsNotImmutable(final List<String> items) {
        items.add("added to a final List");     // allowed — mutating the object
        // items = new ArrayList<>();           // error — repointing the reference

        final int[] numbers = { 1, 2, 3 };
        numbers[0] = 99;                        // allowed — array contents are not final
        // numbers = new int[] { 4, 5 };        // error

        System.out.println("── final freezes the reference, not the object ──");
        System.out.println("final List  : " + items);
        System.out.println("final int[] : " + Arrays.toString(numbers) + "   <- element changed");
        System.out.println("For real immutability use List.of(), records, or defensive copies.");
        System.out.println();
    }

    /**
     * Only a final whose initialiser is a compile-time constant expression gets
     * a ConstantValue attribute in the class file and is inlined at every use
     * site. Anything requiring work at runtime does not.
     */
    private static void compileTimeVsRuntime() {
        System.out.println("── Compile-time constant vs runtime final ──");
        System.out.println("MAX_USERS  = " + MAX_USERS + "   (inlined; has ConstantValue)");
        System.out.println("STARTED_AT = " + STARTED_AT + " (computed at class init)");
        System.out.println();
        System.out.println("Verify with:  javap -p -v ConstantsAndFinal.class");
        System.out.println("  MAX_USERS  shows -> ConstantValue: int 100");
        System.out.println("  STARTED_AT shows -> no ConstantValue line");
    }
}
