/**
 * Day 4 — variables and the eight primitive data types.
 *
 * Covers the three kinds of variable (instance, static, local), the size and
 * range of every primitive, the literal forms each one accepts, and the rules
 * governing conversion between them.
 *
 * @author  Divesh Agarwal
 * @version 1.0
 * @since   2026-08-17
 */
public class VariablesAndPrimitives {

    /** Static (class) variable — one copy shared by the whole class. */
    private static final String LESSON = "Variables & primitive data types";

    /** A constant: {@code static final}, SCREAMING_SNAKE_CASE by convention. */
    private static final int DAYS_IN_WEEK = 7;

    // Instance variables, deliberately left unassigned. Fields are given a
    // default by the JVM during object initialisation — unlike local variables,
    // which the compiler refuses to read before assignment.
    private byte defaultByte;
    private short defaultShort;
    private int defaultInt;
    private long defaultLong;
    private float defaultFloat;
    private double defaultDouble;
    private char defaultChar;
    private boolean defaultBoolean;
    private String defaultReference;

    public static void main(String[] args) {
        System.out.println("Day 4 — " + LESSON);
        System.out.println();

        theEightPrimitives();
        defaultValues();
        literalForms();
        charIsANumber();
        wideningAndNarrowing();
        typeInferenceWithVar();
    }

    /**
     * The eight primitives are the only types in Java that are not objects.
     * Their sizes are fixed by the language spec, not by the platform — an
     * {@code int} is 32 bits everywhere, which is a deliberate contrast with C.
     */
    private static void theEightPrimitives() {
        System.out.println("The eight primitives");
        System.out.printf("  %-8s %5s %6s  %s%n", "type", "bits", "bytes", "range");
        System.out.printf("  %-8s %5d %6d  %d .. %d%n",
                "byte", Byte.SIZE, Byte.BYTES, Byte.MIN_VALUE, Byte.MAX_VALUE);
        System.out.printf("  %-8s %5d %6d  %d .. %d%n",
                "short", Short.SIZE, Short.BYTES, Short.MIN_VALUE, Short.MAX_VALUE);
        System.out.printf("  %-8s %5d %6d  %d .. %d%n",
                "int", Integer.SIZE, Integer.BYTES, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.printf("  %-8s %5d %6d  %d .. %d%n",
                "long", Long.SIZE, Long.BYTES, Long.MIN_VALUE, Long.MAX_VALUE);
        System.out.printf("  %-8s %5d %6d  ~7 decimal digits of precision%n",
                "float", Float.SIZE, Float.BYTES);
        System.out.printf("  %-8s %5d %6d  ~15 decimal digits of precision%n",
                "double", Double.SIZE, Double.BYTES);
        System.out.printf("  %-8s %5d %6d  %d .. %d (unsigned)%n",
                "char", Character.SIZE, Character.BYTES,
                (int) Character.MIN_VALUE, (int) Character.MAX_VALUE);
        System.out.printf("  %-8s %5s %6s  true / false%n",
                "boolean", "JVM", "JVM");
        System.out.println();

        // boolean has no guaranteed size: the spec leaves it to the JVM, which
        // typically uses a full int on the stack and a byte inside arrays.

        // Trap: for the floating-point types MIN_VALUE is the smallest positive
        // value, NOT the most negative one. The lowest double is -MAX_VALUE.
        System.out.println("Float/double MIN_VALUE is the smallest POSITIVE value:");
        System.out.println("  Double.MIN_VALUE = " + Double.MIN_VALUE);
        System.out.println("  lowest double    = " + (-Double.MAX_VALUE));
        System.out.println();
    }

    /**
     * Fields are default-initialised; local variables are not. Reading a local
     * before assigning it is a compile error, not a runtime surprise — the
     * compiler's definite-assignment analysis rejects it.
     */
    private static void defaultValues() {
        VariablesAndPrimitives o = new VariablesAndPrimitives();

        System.out.println("Default values of uninitialised FIELDS");
        System.out.println("  byte    : " + o.defaultByte);
        System.out.println("  short   : " + o.defaultShort);
        System.out.println("  int     : " + o.defaultInt);
        System.out.println("  long    : " + o.defaultLong);
        System.out.println("  float   : " + o.defaultFloat);
        System.out.println("  double  : " + o.defaultDouble);
        System.out.println("  char    : code point " + (int) o.defaultChar + " (NUL)");
        System.out.println("  boolean : " + o.defaultBoolean);
        System.out.println("  String  : " + o.defaultReference + " (reference, not a primitive)");
        System.out.println();

        // A local variable gets no such courtesy:
        //     int local;
        //     System.out.println(local);   // error: variable local might not
        //                                  // have been initialized
        System.out.println("Locals get NO default — reading one unassigned is a compile error.");
        System.out.println("  days in a week (static final) = " + DAYS_IN_WEEK);
        System.out.println();
    }

    /**
     * The same value can be written in several bases, and underscores may be
     * placed between digits to group them. Suffixes decide the literal's type.
     */
    private static void literalForms() {
        int decimal = 1_000_000;
        int hex = 0xFF;            // 255
        int binary = 0b1010_1010;  // 170
        int octal = 0777;          // 511 — a leading zero means octal

        long big = 9_000_000_000L; // L required: the literal exceeds int range
        float f = 3.14F;           // F required: 3.14 alone is a double
        double d = 3.14;           // no suffix needed, double is the default
        double sci = 1.5e3;        // 1500.0

        System.out.println("Literal forms");
        System.out.println("  1_000_000     = " + decimal);
        System.out.println("  0xFF          = " + hex);
        System.out.println("  0b1010_1010   = " + binary);
        System.out.println("  0777 (octal!) = " + octal);
        System.out.println("  9_000_000_000L= " + big);
        System.out.println("  3.14F         = " + f);
        System.out.println("  3.14          = " + d);
        System.out.println("  1.5e3         = " + sci);
        System.out.println();

        // Underscores are a readability aid only — they are stripped by the
        // lexer. They may not lead, trail, or sit next to the decimal point.
    }

    /**
     * {@code char} is an unsigned 16-bit integer that happens to print as text.
     * Arithmetic on it promotes to {@code int}, so the result prints as a number
     * unless cast back.
     */
    private static void charIsANumber() {
        char letter = 'A';
        int asNumber = letter;          // widening, no cast needed
        char next = (char) (letter + 1); // cast back or you get an int

        System.out.println("char is a 16-bit unsigned integer");
        System.out.println("  'A'            = " + letter);
        System.out.println("  (int) 'A'      = " + asNumber);
        System.out.println("  'A' + 1        = " + (letter + 1) + "   <- promoted to int");
        System.out.println("  (char)('A'+1)  = " + next);
        System.out.println("  '\\u0041'       = " + 'A');
        System.out.println();
    }

    /**
     * Widening conversions are implicit and lossless in magnitude. Narrowing
     * conversions require an explicit cast because they can lose data.
     */
    private static void wideningAndNarrowing() {
        System.out.println("Conversions");
        System.out.println("  widening : byte -> short -> int -> long -> float -> double");
        System.out.println("             char -> int is widening too");

        int i = 42;
        long l = i;      // implicit widening
        double dd = l;   // implicit widening

        double pi = 3.99;
        int truncated = (int) pi;   // explicit narrowing: truncates, never rounds

        System.out.println("  int 42 -> long -> double : " + dd);
        System.out.println("  (int) 3.99               = " + truncated + "   <- truncated, not rounded");
        System.out.println("  Math.round(3.99)         = " + Math.round(pi));
        System.out.println();

        // int -> float and long -> float/double are widening yet still lossy:
        // the target has more range but fewer significant digits.
        int precise = 123_456_789;
        float lossy = precise;
        System.out.println("  int 123456789 -> float   = " + lossy
                + "   <- widening, but precision lost");
        System.out.println();
    }

    /**
     * {@code var} infers the type of a LOCAL variable from its initialiser. The
     * variable is still statically typed — this is inference, not dynamism.
     */
    private static void typeInferenceWithVar() {
        var count = 10;              // inferred int
        var name = "Divesh";         // inferred String
        var ratio = 2.5;             // inferred double
        var flag = true;             // inferred boolean

        System.out.println("var — local variable type inference (Java 10+)");
        System.out.println("  var count = 10     -> " + ((Object) count).getClass().getSimpleName());
        System.out.println("  var name  = \"Divesh\" -> " + name.getClass().getSimpleName());
        System.out.println("  var ratio = 2.5    -> " + ((Object) ratio).getClass().getSimpleName());
        System.out.println("  var flag  = true   -> " + ((Object) flag).getClass().getSimpleName());
        System.out.println();

        // Not allowed: var on fields, method parameters, or return types; var
        // with no initialiser; var initialised to null (nothing to infer from).
        System.out.println("  count is fixed at int — count = \"text\" would not compile.");
    }
}
