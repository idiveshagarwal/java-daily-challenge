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

## Takeaway

One `.java` file is a *compilation unit*, not a *class*. The file name is bound
only to its public type; the compiler is free to emit as many `.class` files as
there are types inside.
