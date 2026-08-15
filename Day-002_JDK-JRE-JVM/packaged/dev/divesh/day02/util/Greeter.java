// The package declaration must mirror the folder path exactly:
//   dev/divesh/day02/util/Greeter.java  ⇒  package dev.divesh.day02.util;
// Get this wrong and javac fails before it ever looks at your code.
package dev.divesh.day02.util;

/**
 * Public, so it is visible outside its own package. This is the only reason
 * PackagedApp (in the parent package) is allowed to import it.
 */
public class Greeter {

    private final String name;

    public Greeter(String name) {
        this.name = name;
    }

    /** public — reachable from any package. */
    public String greet() {
        return "Hello, " + name + "! — from " + qualifiedName();
    }

    /** public — used below to show the fully-qualified name at runtime. */
    public String qualifiedName() {
        return getClass().getName();
    }

    /**
     * No modifier = package-private. Visible only inside
     * dev.divesh.day02.util. PackagedApp CANNOT call this — uncomment the
     * call in PackagedApp.main and javac will reject it.
     */
    String internalOnly() {
        return "package-private: only dev.divesh.day02.util can see me";
    }
}
