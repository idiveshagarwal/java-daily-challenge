// 1. Package declaration — optional, but if present it must be the very first
//    statement in the file. Omitted here so the file compiles in place.
//    e.g. package dev.divesh.challenge;

// 2. Imports — come after the package, before the type declaration.
import java.time.LocalDate;

/**
 * 3. Type declaration. The file is named JavaFileStructure.java because this
 *    class is public: a public type's name must match the file name exactly.
 *
 * A file may hold only ONE public top-level type, but any number of
 * package-private ones (see Helper at the bottom).
 */
public class JavaFileStructure {

    // 4. Static field — one copy shared by the whole class, created when the
    //    JVM loads the class.
    private static final String COURSE = "Java Daily Challenge";

    // 5. Instance field — one copy per object, created by `new`.
    private final String topic;

    // 6. Constructor — same name as the class, no return type.
    public JavaFileStructure(String topic) {
        this.topic = topic;
    }

    // 7. Instance method — needs an object to be called on.
    public String describe() {
        return COURSE + " — " + topic;
    }

    // 8. Static method — belongs to the class, called without an object.
    private static void printToolchain() {
        System.out.println("JDK  = JRE + compiler & tools (javac, javadoc, jar)");
        System.out.println("JRE  = JVM + core class libraries");
        System.out.println("JVM  = the engine that executes .class bytecode");
    }

    // 9. Entry point. The JVM looks for exactly this signature: public, static,
    //    void, named main, taking a String array.
    public static void main(String[] args) {
        JavaFileStructure day = new JavaFileStructure("JDK, JRE, JVM");

        System.out.println(day.describe());
        System.out.println("Day 2 — " + LocalDate.of(2026, 8, 15));
        System.out.println();

        printToolchain();
        System.out.println();

        // Reading back the runtime we are actually executing on.
        System.out.println("Running on : " + System.getProperty("java.version"));
        System.out.println("JVM        : " + System.getProperty("java.vm.name"));

        System.out.println();
        System.out.println(Helper.note());
    }
}

/**
 * A second, non-public top-level class in the same file. Legal, and it compiles
 * to its own Helper.class — one .class file is produced per type, not per file.
 */
class Helper {

    static String note() {
        return "javac produced two .class files from one .java file.";
    }
}
