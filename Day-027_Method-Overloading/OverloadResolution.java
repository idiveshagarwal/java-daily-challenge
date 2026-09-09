import java.util.ArrayList;
import java.util.List;

/**
 * Day 27, part 2 — how the compiler chooses.
 *
 * Overload resolution happens entirely at COMPILE TIME, from the DECLARED
 * types of the arguments. The runtime type of the object is never consulted —
 * which is the whole difference between overloading and overriding.
 *
 * When several overloads could accept a call, javac tries progressively more
 * permissive rules and stops at the first phase that finds a match. That
 * ordering is the source of every surprise below.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-09
 */
public class OverloadResolution {

    public static void main(String[] args) {
        System.out.println("Day 27 - Overload resolution");
        System.out.println();

        theLadder();
        boundAtCompileTime();
        nullPicksTheMostSpecific();
        ambiguity();
        theListRemoveTrap();
    }

    // ── the resolution ladder ──────────────────────────────────────────────

    static void show(long x)    { System.out.println("    long      - phase 1: widening"); }
    static void show(Integer x) { System.out.println("    Integer   - phase 2: boxing"); }
    static void show(Object x)  { System.out.println("    Object    - phase 2: boxing then widening"); }
    static void show(int... x)  { System.out.println("    int...    - phase 3: varargs"); }

    /**
     * All four of these can accept show(5). javac picks the first phase that
     * works, so `long` wins — the most specific rung is the least permissive
     * rule, not the closest-looking type.
     */
    private static void theLadder() {
        System.out.println("-- The resolution ladder --");
        System.out.println("  four overloads all able to accept show(5):");
        System.out.print("  show(5) picks:");
        System.out.println();
        show(5);

        System.out.println();
        System.out.println("  Removing candidates one at a time gives the full order:");
        System.out.println("    long, Integer, Object, int...   -> long");
        System.out.println("    Integer, Object, int...         -> Integer");
        System.out.println("    Object, int...                  -> Object");
        System.out.println("    int...                          -> int...");
        System.out.println();
        System.out.println("  Phase 1  widening only        int -> long -> float -> double");
        System.out.println("  Phase 2  boxing allowed       int -> Integer -> Object");
        System.out.println("  Phase 3  varargs allowed      int -> int...");
        System.out.println();
        System.out.println("  WIDENING BEATS BOXING. That ordering exists so that code");
        System.out.println("  written before Java 5 kept its old meaning when autoboxing");
        System.out.println("  was introduced — adding a wrapper overload must not");
        System.out.println("  silently steal calls from a primitive one.");
        System.out.println();
    }

    // ── compile-time binding ───────────────────────────────────────────────

    static void render(Object o) { System.out.println("    render(Object)"); }
    static void render(String s) { System.out.println("    render(String)"); }

    /**
     * THE conceptual point. Both calls pass the same object; only the DECLARED
     * type differs, and that alone decides the overload.
     */
    private static void boundAtCompileTime() {
        String asString = "hello";
        Object asObject = "hello";               // runtime type is still String

        System.out.println("-- Bound at compile time --");
        System.out.println("  String s = \"hello\";  render(s):");
        render(asString);
        System.out.println("  Object o = \"hello\";  render(o):");
        render(asObject);
        System.out.println("    (o.getClass() is actually "
                + asObject.getClass().getSimpleName() + ")");
        System.out.println();
        System.out.println("  Same object, different overload. The compiler used the");
        System.out.println("  DECLARED type and never asked what the object really is.");
        System.out.println();
        System.out.println("  This is the line between the two mechanisms:");
        System.out.println("    overloading  chosen at COMPILE time by declared type");
        System.out.println("    overriding   chosen at RUNTIME by actual type");
        System.out.println();
    }

    // ── null ───────────────────────────────────────────────────────────────

    static void accept(Object o) { System.out.println("    accept(Object)"); }
    static void accept(String s) { System.out.println("    accept(String)"); }

    private static void nullPicksTheMostSpecific() {
        System.out.println("-- null --");
        System.out.print("  accept(null) picks:");
        System.out.println();
        accept(null);
        System.out.println();
        System.out.println("  null is assignable to every reference type, so ALL");
        System.out.println("  overloads apply. javac picks the most specific — String");
        System.out.println("  is a subtype of Object, so String wins.");
        System.out.println();
    }

    private static void ambiguity() {
        System.out.println("-- Ambiguity --");
        System.out.println("  static void f(String s)");
        System.out.println("  static void f(Integer i)");
        System.out.println("  f(null);");
        System.out.println();
        System.out.println("    error: reference to f is ambiguous");
        System.out.println("      both method f(String) and method f(Integer) match");
        System.out.println();
        System.out.println("  String and Integer are UNRELATED, so neither is more");
        System.out.println("  specific. There is no most-specific candidate and javac");
        System.out.println("  refuses to guess.");
        System.out.println();
        System.out.println("  Fix by casting the null to say which you meant:");
        System.out.println("    f((String) null);");
        System.out.println();
    }

    /**
     * The famous real-world case. List has BOTH remove(int index) and
     * remove(Object o), so for a List<Integer> the two are constantly
     * confusable — and phase 1 (no boxing) means the int overload wins.
     */
    private static void theListRemoveTrap() {
        List<Integer> byIndex = new ArrayList<>(List.of(10, 20, 30));
        List<Integer> byValue = new ArrayList<>(List.of(10, 20, 30));

        byIndex.remove(1);                        // remove(int) - phase 1
        byValue.remove(Integer.valueOf(1));       // remove(Object) - value 1 absent

        System.out.println("-- The List.remove trap --");
        System.out.println("  List<Integer> starts as        [10, 20, 30]");
        System.out.println("  remove(1)                   -> " + byIndex
                + "    <- removed INDEX 1");
        System.out.println("  remove(Integer.valueOf(1))  -> " + byValue
                + "   <- removed VALUE 1 (absent, no change)");
        System.out.println();
        System.out.println("  remove(1) does NOT remove the value 1. The int overload");
        System.out.println("  matches in phase 1 without boxing, so it wins — and it");
        System.out.println("  means \"index\".");
        System.out.println();
        System.out.println("  This is overload resolution producing a silently wrong");
        System.out.println("  result in the standard library. To remove by value from a");
        System.out.println("  List<Integer>, box explicitly:");
        System.out.println("    list.remove(Integer.valueOf(1));");
        System.out.println();
        System.out.println("  General lesson: overloads that differ only between a");
        System.out.println("  primitive and its wrapper are a design smell. Give them");
        System.out.println("  different names — removeAt and removeValue would have");
        System.out.println("  cost nothing and prevented this entirely.");
    }
}
