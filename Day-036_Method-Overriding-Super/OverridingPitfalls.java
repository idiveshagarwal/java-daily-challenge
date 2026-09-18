import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Day 36, part 3 — the ways an "override" turns out not to be one.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-18
 */
public class OverridingPitfalls {

    public static void main(String[] args) {
        System.out.println("Day 36 - Overriding pitfalls");
        System.out.println();

        trap1_overloadingInsteadOfOverriding();
        trap2_staticMethodsAreHidden();
        trap3_privateMethodsAreNeverOverridden();
        trap4_theRulesTheCompilerEnforces();
        trap5_equalsIsTheClassicVictim();
        trap6_superIsOneHopOnly();
    }

    static class Printer {
        String print(Object value) {
            return "Printer.print(Object): " + value;
        }
    }

    /** Looks like an override. Different parameter type, so it is an overload. */
    static class LoudPrinter extends Printer {
        String print(String value) {
            return "LoudPrinter.print(String): " + value.toUpperCase();
        }
    }

    private static void trap1_overloadingInsteadOfOverriding() {
        Printer printer = new LoudPrinter();
        LoudPrinter loud = new LoudPrinter();
        Object asObject = "hello";

        System.out.println("-- Trap 1: an overload is not an override --");
        System.out.println("  printer.print(\"hello\")       : " + printer.print("hello"));
        System.out.println("  loud.print(\"hello\")          : " + loud.print("hello"));
        System.out.println("  loud.print((Object) \"hello\") : " + loud.print(asObject));
        System.out.println();
        System.out.println("  LoudPrinter has TWO print methods, and nothing was replaced.");
        System.out.println("  Overloads are chosen by the compiler from the static types");
        System.out.println("  (Day 27); overrides are chosen at runtime from the object.");
        System.out.println("  @Override would have rejected it at compile time:");
        System.out.println("    error: method does not override or implement a method from a supertype");
        System.out.println();
    }

    static class Site {
        static String region() {
            return "Site.region";
        }

        String instanceRegion() {
            return "Site.instanceRegion";
        }
    }

    static class EuSite extends Site {
        static String region() {                       // hides, does not override
            return "EuSite.region";
        }

        @Override
        String instanceRegion() {
            return "EuSite.instanceRegion";
        }
    }

    private static void trap2_staticMethodsAreHidden() {
        Site site = new EuSite();

        System.out.println("-- Trap 2: static methods are hidden, not overridden --");
        System.out.println("  Site site = new EuSite();");
        System.out.println("  site.region()          : " + site.region()
                + "        <- the REFERENCE type wins");
        System.out.println("  site.instanceRegion()  : " + site.instanceRegion()
                + "  <- the OBJECT wins");
        System.out.println("  EuSite.region()        : " + EuSite.region());
        System.out.println();
        System.out.println("  Static methods belong to the class, so there is no object to");
        System.out.println("  dispatch on. javac even warns about the call that misleads:");
        System.out.println("    warning: [static] static method should be qualified by type");
        System.out.println("             name, Site, instead of by an expression");
        System.out.println("  @Override on a static method is itself an error:");
        System.out.println("    error: static methods cannot be annotated with @Override");
        System.out.println();
    }

    static class Report {
        /** A template method: public, and calls a private helper. */
        public String render() {
            return "render -> " + header();
        }

        private String header() {
            return "Report.header";
        }
    }

    static class SalesReport extends Report {
        /** Same name, same signature — and completely unrelated to Report's. */
        private String header() {
            return "SalesReport.header";
        }

        public String callOwnHeader() {
            return header();
        }
    }

    private static void trap3_privateMethodsAreNeverOverridden() {
        SalesReport report = new SalesReport();

        System.out.println("-- Trap 3: private methods are never overridden --");
        System.out.println("  report.render()          : " + report.render()
                + "   <- still the parent's");
        System.out.println("  report.callOwnHeader()   : " + report.callOwnHeader());
        System.out.println();
        System.out.println("  A private method is not inherited, so there is nothing to");
        System.out.println("  override and no dispatch: Report.render() is bound to");
        System.out.println("  Report.header() at compile time. Adding @Override to the");
        System.out.println("  child's header() is an error — it overrides nothing. A method");
        System.out.println("  meant to be replaced must be at least package-private.");
        System.out.println();
    }

    private static void trap4_theRulesTheCompilerEnforces() {
        System.out.println("-- Trap 4: four rules, four error messages --");
        System.out.println();
        System.out.println("  access may widen, never narrow:");
        System.out.println("    public String greet()  ->  protected String greet()");
        System.out.println("    error: greet() in C cannot override greet() in P");
        System.out.println("      attempting to assign weaker access privileges; was public");
        System.out.println();
        System.out.println("  checked exceptions may narrow, never widen:");
        System.out.println("    void run()  ->  void run() throws IOException");
        System.out.println("    error: run() in C cannot override run() in P");
        System.out.println("      overridden method does not throw IOException");
        System.out.println();
        System.out.println("  the return type must be the same or a subtype:");
        System.out.println("    int size()  ->  long size()");
        System.out.println("    error: size() in C cannot override size() in P");
        System.out.println("      return type long is not compatible with int");
        System.out.println();
        System.out.println("  a final method cannot be overridden at all:");
        System.out.println("    error: greet() in C cannot override greet() in P");
        System.out.println("      overridden method is final");
        System.out.println();
        System.out.println("  Each rule keeps the parent's promise usable: code holding an");
        System.out.println("  Employee reference must not be surprised by a method that is");
        System.out.println("  suddenly inaccessible, throws more, or returns something else.");
        System.out.println();
    }

    /** equals(Point) instead of equals(Object): compiles, and quietly does nothing. */
    static final class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /** NOT an override — Object declares equals(Object). */
        public boolean equals(Point other) {
            return other != null && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static final class GoodPoint {
        final int x;
        final int y;

        GoodPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof GoodPoint p && x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static void trap5_equalsIsTheClassicVictim() {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        Object bAsObject = b;

        Set<Point> points = new HashSet<>(List.of(a));
        Set<GoodPoint> goodPoints = new HashSet<>(List.of(new GoodPoint(1, 2)));

        System.out.println("-- Trap 5: equals(Point) is an overload --");
        System.out.println("  a.equals(b)                   : " + a.equals(b)
                + "   <- the overload runs");
        System.out.println("  a.equals(bAsObject)           : " + a.equals(bAsObject)
                + "  <- Object.equals, identity only");
        System.out.println("  List.of(a).contains(b)        : " + List.of(a).contains(b));
        System.out.println("  HashSet.contains(b)           : " + points.contains(b));
        System.out.println("  GoodPoint HashSet.contains(..): "
                + goodPoints.contains(new GoodPoint(1, 2)));
        System.out.println();
        System.out.println("  Every collection calls equals(Object), so the overload is");
        System.out.println("  never reached and the object compares by identity. The");
        System.out.println("  parameter must be Object — and @Override is what tells you so.");
        System.out.println();
    }

    static class Level1 {
        String name() {
            return "Level1";
        }
    }

    static class Level2 extends Level1 {
        @Override
        String name() {
            return "Level2 (parent says " + super.name() + ")";
        }
    }

    static class Level3 extends Level2 {
        @Override
        String name() {
            return "Level3 (parent says " + super.name() + ")";
        }
    }

    private static void trap6_superIsOneHopOnly() {
        System.out.println("-- Trap 6: super goes up exactly one level --");
        System.out.println("  new Level3().name():");
        System.out.println("    " + new Level3().name());
        System.out.println();
        System.out.println("  Level3 reaches Level1 only because Level2 chose to call super");
        System.out.println("  as well. Skipping a level is not expressible:");
        System.out.println("    super.super.name()   ->  error: <identifier> expected");
        System.out.println();
        System.out.println("  So an override that forgets super breaks the chain for");
        System.out.println("  everyone below it — which is why a parent cannot force one.");
    }
}
