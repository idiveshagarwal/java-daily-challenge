// 1. Package first — matches the folder path dev/divesh/day02.
package dev.divesh.day02;

// 2. Imports second. Greeter lives in a DIFFERENT package (…day02.util), so it
//    must be imported — or written out in full as
//    dev.divesh.day02.util.Greeter every time it is used.
import dev.divesh.day02.util.Greeter;

/**
 * Same anatomy as Day 2's JavaFileStructure, but placed in a real package so
 * the directory rule, imports and access modifiers are all visible.
 */
public class PackagedApp {

    public static void main(String[] args) {
        Greeter greeter = new Greeter("Divesh");

        System.out.println(greeter.greet());
        System.out.println();

        // Imported — short name works.
        System.out.println("Short name     : Greeter");
        System.out.println("Qualified name : " + greeter.qualifiedName());

        // Fully-qualified name used inline; no import needed for this one.
        java.time.LocalDate today = java.time.LocalDate.of(2026, 8, 15);
        System.out.println("Written in full: java.time.LocalDate -> " + today);

        System.out.println();
        System.out.println("This class     : " + PackagedApp.class.getName());
        System.out.println("Its package    : " + PackagedApp.class.getPackageName());

        // Not allowed — internalOnly() is package-private and we are in
        // dev.divesh.day02, not dev.divesh.day02.util. Uncommenting this
        // fails at COMPILE time, not runtime:
        //     error: internalOnly() is not public in Greeter;
        //            cannot be accessed from outside package
        //
        // System.out.println(greeter.internalOnly());
    }
}
