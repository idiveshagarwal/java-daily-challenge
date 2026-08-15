# Day 2 — JDK, JRE, JVM & the Structure of a Java File

**15 Aug 2026 (Sat) · Ch 1**

## Topic

Two things that are easy to confuse early on: what the three-letter acronyms
actually contain, and what the compiler expects to find inside a `.java` file.

## JDK vs JRE vs JVM

They nest — each one contains the next.

```
┌─ JDK — Java Development Kit ────────────────┐
│  javac, javadoc, jar, jshell, jdb           │
│  ┌─ JRE — Java Runtime Environment ───────┐ │
│  │  core class libraries (java.lang, …)   │ │
│  │  ┌─ JVM — Java Virtual Machine ──────┐ │ │
│  │  │  class loader, bytecode verifier, │ │ │
│  │  │  interpreter + JIT, garbage coll. │ │ │
│  │  └───────────────────────────────────┘ │ │
│  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

| | Contains | You need it to |
| --- | --- | --- |
| **JVM** | Execution engine only | Run bytecode |
| **JRE** | JVM + standard libraries | Run Java programs |
| **JDK** | JRE + compiler and dev tools | Write and compile Java |

**Rule of thumb:** to *write* Java you need the JDK; to only *run* it, the JRE
suffices. Since Java 11 a separate JRE download is no longer published — you
install the JDK and use `jlink` if you want a trimmed runtime.

## Why Java is "write once, run anywhere"

```
Hello.java  ──javac──▶  Hello.class  ──JVM──▶  native execution
 (source)              (bytecode,              (platform-specific)
                     platform-neutral)
```

`javac` does not produce machine code. It produces **bytecode**, which is the
same on every platform. The JVM is the part that differs per OS, and it
translates bytecode into native instructions at runtime — interpreting first,
then JIT-compiling the hot paths.

## Structure of a Java file

Order matters for the first two; everything else lives inside the type.

1. **Package declaration** — optional, at most one, must be the first statement.
2. **Imports** — after the package, before any type.
3. **Type declaration** — the class, interface, enum or record.
   - Fields (static and instance)
   - Constructors
   - Methods
   - `main` as the entry point

### Rules worth memorising

- A file may contain **at most one `public` top-level type**, and its name must
  match the file name exactly — `JavaFileStructure` ⇒ `JavaFileStructure.java`.
- It may contain **any number of non-public top-level types**.
- `javac` emits **one `.class` file per type**, not per source file. This file
  produces both `JavaFileStructure.class` and `Helper.class`.
- The entry point signature is fixed: `public static void main(String[] args)`.
  - `static` so the JVM can call it without constructing an object.
  - `void` because the exit code comes from `System.exit`, not a return value.

## Run

```bash
javac JavaFileStructure.java && java JavaFileStructure
```

Note that `java JavaFileStructure` takes the **class name**, not the file name —
no `.class` extension.

## Output

```
Java Daily Challenge — JDK, JRE, JVM
Day 2 — 2026-08-15

JDK  = JRE + compiler & tools (javac, javadoc, jar)
JRE  = JVM + core class libraries
JVM  = the engine that executes .class bytecode

Running on : 25.0.4
JVM        : OpenJDK 64-Bit Server VM

javac produced two .class files from one .java file.
```

## Packages

`JavaFileStructure.java` above leaves the package slot commented out so it runs
with a bare `javac`. Real code always declares one. The `packaged/` folder is the
same anatomy done properly:

```
packaged/
└── com/solifein/day02/
    ├── PackagedApp.java          package com.solifein.day02;
    └── util/
        └── Greeter.java          package com.solifein.day02.util;
```

**The directory rule:** the package declaration must mirror the folder path
exactly. `com/solifein/day02/util/Greeter.java` must declare
`package com.solifein.day02.util;`. A mismatch fails at compile time.

**Imports:** `Greeter` sits in a different package from `PackagedApp`, so it has
to be imported — or written out in full every time. These are equivalent:

```java
import com.solifein.day02.util.Greeter;   // then: Greeter g = new Greeter(…)
com.solifein.day02.util.Greeter g = …;    // no import needed
```

An import is only an alias so you can use the short name. It does **not** copy
code into your file, and it costs nothing at runtime.

**Access across packages:** `public` crosses package boundaries; no modifier
("package-private") does not. `Greeter.internalOnly()` has no modifier, so
un-commenting the call in `PackagedApp` is rejected by the compiler:

```
error: internalOnly() is not public in Greeter;
       cannot be accessed from outside package
```

### Running the packaged version

```bash
cd packaged && javac -d out $(find com -name "*.java") && java -cp out com.solifein.day02.PackagedApp
```

Three things change once packages are involved:

- `-d out` sends `.class` files to a separate output tree, keeping source clean.
- `-cp out` tells the JVM where the package root is — **the root, not the class's
  own folder**. This is the single most common beginner error.
- You launch the **fully-qualified name** `com.solifein.day02.PackagedApp`, not
  the bare class name.

Output:

```
Hello, Divesh! — from com.solifein.day02.util.Greeter

Short name     : Greeter
Qualified name : com.solifein.day02.util.Greeter
Written in full: java.time.LocalDate -> 2026-08-15

This class     : com.solifein.day02.PackagedApp
Its package    : com.solifein.day02
```

## Takeaway

One `.java` file is a *compilation unit*, not a *class*. The file name is bound
only to its public type; the compiler is free to emit as many `.class` files as
there are types inside.

A package is a **namespace enforced by directory layout**, and the classpath is
how the JVM is told where that layout begins.
