/**
 * Day 23 — String basics and the common methods.
 *
 * One property governs the whole class: a String is IMMUTABLE. Nothing can
 * change the characters of an existing String, so every method that looks like
 * it edits one actually RETURNS A NEW String and leaves the original alone.
 *
 * Day 6 covered the string pool and why == is not .equals(); this day is about
 * the methods themselves.
 *
 * @author  Divesh Agarwal
 * @since   2026-09-05
 */
public class StringBasics {

    public static void main(String[] args) {
        System.out.println("Day 23 — String basics");
        System.out.println();

        immutability();
        inspecting();
        searching();
        extracting();
        transforming();
        comparing();
        buildingAndSplitting();
    }

    /**
     * The single most common String bug: calling a method and discarding the
     * result. `s.trim();` is a no-op — it computes a trimmed String and throws
     * it away.
     */
    private static void immutability() {
        String s = "  hello  ";

        s.trim();                       // result discarded — does nothing
        s.toUpperCase();                // likewise
        s.concat(" world");             // likewise

        String fixed = s.trim().toUpperCase();   // assign the RESULT

        System.out.println("── Immutability ──");
        System.out.println("  after s.trim(); s.toUpperCase(); s.concat(...)");
        System.out.println("    s     = [" + s + "]   <- completely unchanged");
        System.out.println("    fixed = [" + fixed + "]        <- the returned value");
        System.out.println();
        System.out.println("  Every \"modifying\" method returns a NEW String.");
        System.out.println("  If you do not assign the result, nothing happens.");
        System.out.println();
    }

    /** Size and single characters. */
    private static void inspecting() {
        String s = "Java";

        System.out.println("── Inspecting ──");
        System.out.println("  \"Java\".length()      = " + s.length() + "     (a METHOD — arrays use .length, Day 19)");
        System.out.println("  charAt(0)            = " + s.charAt(0));
        System.out.println("  charAt(length()-1)   = " + s.charAt(s.length() - 1));
        System.out.println("  isEmpty()            = " + s.isEmpty() + "  (length == 0)");
        System.out.println("  \"   \".isEmpty()      = " + "   ".isEmpty() + "  <- spaces are characters");
        System.out.println("  \"   \".isBlank()      = " + "   ".isBlank() + "   (Java 11+, whitespace-only)");

        try {
            s.charAt(s.length());
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("  charAt(length())     -> StringIndexOutOfBoundsException");
        }
        System.out.println();
    }

    /** Finding things. All return -1 or false rather than throwing (Day 20). */
    private static void searching() {
        String s = "banana";

        System.out.println("── Searching ──");
        System.out.println("  \"banana\"");
        System.out.println("  indexOf(\"an\")        = " + s.indexOf("an") + "    (first)");
        System.out.println("  lastIndexOf(\"an\")    = " + s.lastIndexOf("an") + "    (last)");
        System.out.println("  indexOf(\"an\", 2)     = " + s.indexOf("an", 2) + "    (from index 2)");
        System.out.println("  indexOf(\"xyz\")       = " + s.indexOf("xyz") + "   <- the -1 sentinel again");
        System.out.println("  contains(\"nan\")      = " + s.contains("nan"));
        System.out.println("  startsWith(\"ban\")    = " + s.startsWith("ban"));
        System.out.println("  endsWith(\"ana\")      = " + s.endsWith("ana"));
        System.out.println();
    }

    /** substring — the end index is EXCLUSIVE, like Arrays.copyOfRange (Day 19). */
    private static void extracting() {
        String s = "programming";

        System.out.println("── Extracting ──");
        System.out.println("  \"programming\"");
        System.out.println("  substring(3)         = " + s.substring(3) + "        (to the end)");
        System.out.println("  substring(0, 7)      = " + s.substring(0, 7) + "     (end EXCLUSIVE)");
        System.out.println("  substring(3, 3)      = [" + s.substring(3, 3) + "]           <- empty, not an error");
        System.out.println("  substring(length())  = [" + s.substring(s.length()) + "]           <- also empty and legal");

        try {
            s.substring(5, 2);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("  substring(5, 2)      -> " + e.getMessage());
        }
        System.out.println();
        System.out.println("  substring(a, b) has length b - a. That subtraction is");
        System.out.println("  the easiest way to remember which index is excluded.");
        System.out.println();
    }

    /** Every one of these returns a new String. */
    private static void transforming() {
        String s = "  Hello, World  ";

        System.out.println("── Transforming ──");
        System.out.println("  original        [" + s + "]");
        System.out.println("  trim()          [" + s.trim() + "]");
        System.out.println("  strip()         [" + s.strip() + "]        (Java 11+, Unicode-aware)");
        System.out.println("  toUpperCase()   [" + s.toUpperCase() + "]");
        System.out.println("  replace('l','L')[" + s.replace('l', 'L') + "]");
        System.out.println("  \"ab\".repeat(3)   " + "ab".repeat(3) + "        (Java 11+)");
        System.out.println();
    }

    /** Ordering, and why compareTo returns more than -1/0/1. */
    private static void comparing() {
        System.out.println("── Comparing ──");
        System.out.println("  equals(\"JAVA\")           = " + "Java".equals("JAVA"));
        System.out.println("  equalsIgnoreCase(\"JAVA\") = " + "Java".equalsIgnoreCase("JAVA"));
        System.out.println();
        System.out.println("  compareTo returns a DIFFERENCE, not just a sign:");
        System.out.println("    \"a\".compareTo(\"b\")           = " + "a".compareTo("b") + "     (one apart)");
        System.out.println("    \"a\".compareTo(\"z\")           = " + "a".compareTo("z") + "    (25 apart)");
        System.out.println("    \"apple\".compareTo(\"apricot\") = " + "apple".compareTo("apricot")
                + "     ('p' - 'r' at the first difference)");
        System.out.println("    \"abc\".compareTo(\"ab\")        = " + "abc".compareTo("ab")
                + "      (no difference found -> length difference)");
        System.out.println();
        System.out.println("  Only the SIGN is contractual. Test `< 0`, `> 0`, `== 0`,");
        System.out.println("  never `== -1`.");
        System.out.println();
    }

    /** Turning a String into parts and back. */
    private static void buildingAndSplitting() {
        String csv = "red,green,blue";
        String[] parts = csv.split(",");

        System.out.println("── Splitting and joining ──");
        System.out.println("  split(\",\")   -> " + java.util.Arrays.toString(parts));
        System.out.println("  String.join(\" | \", parts) -> " + String.join(" | ", parts));
        System.out.println();
        System.out.println("  split takes a REGULAR EXPRESSION, not a literal —");
        System.out.println("  see StringPitfalls, where that goes badly wrong.");
    }
}
