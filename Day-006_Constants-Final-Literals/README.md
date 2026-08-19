# Day 6 — Constants, `final` and Literals

**19 Aug 2026 (Wed) · Ch 2**

## Java has no `const`

`const` is reserved but unused (Day 3). Constants are built from `final`, and
the word means **"assigned exactly once"** — not "immutable", and not
"compile-time value". Those are three separate ideas that `final` is often
assumed to cover all at once.

## Where `final` can appear

| Applied to | Means |
| --- | --- |
| local variable | cannot be reassigned after its one assignment |
| field | must be assigned by the end of every constructor |
| parameter | cannot be reassigned inside the method body |
| method | cannot be overridden by a subclass |
| class | cannot be extended |

A **blank final** is a field declared without a value and assigned in the
constructor. Every constructor must assign it exactly once — the compiler
tracks this per path.

```java
private final String name;
public ConstantsAndFinal(String name) { this.name = name; }
```

## `final` freezes the reference, not the object

The single most common misunderstanding:

```java
final List<String> items = new ArrayList<>();
items.add("this is fine");        // ✅ mutating the object
items = new ArrayList<>();        // ❌ repointing the reference

final int[] numbers = { 1, 2, 3 };
numbers[0] = 99;                  // ✅ array contents are never final
```

Verified output:

```
final List  : [added to a final List]
final int[] : [99, 2, 3]   <- element changed
```

For genuine immutability you need `List.of(...)`, a `record`, or defensive
copies. `final` alone gives you none of that.

## Compile-time constant vs runtime final

A `static final` is only a **compile-time constant** if its initialiser is a
constant expression. That distinction is visible in the class file:

```java
static final int  MAX_USERS  = 100;                        // constant
static final long STARTED_AT = System.currentTimeMillis(); // not a constant
```

```bash
javap -p -v ConstantsAndFinal.class
```

```
public static final int MAX_USERS;
  descriptor: I
  flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL
  ConstantValue: int 100        ← present

public static final long STARTED_AT;
  descriptor: J
  flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL
                                ← no ConstantValue line
```

### Why this matters: the stale constant trap

A compile-time constant is **inlined into every class that reads it**. The
reader never looks it up at runtime, so changing the constant and recompiling
only its own file leaves every other class holding the old value.

`stale-constant/` reproduces it:

```bash
cd stale-constant
javac Config.java App.java && java App     # Config.VERSION = 1

# now edit Config.java, set VERSION = 2, then:
javac Config.java && java App              # Config.VERSION = 1  ← still stale!
javac Config.java App.java && java App     # Config.VERSION = 2  ← fixed
```

This is a genuine source of "I changed it but nothing happened" bugs. A full
rebuild fixes it; incremental builds may not. If a constant is expected to
change, make it non-constant (a method call or a config lookup) so it is read at
runtime instead.

## Literals

A literal has a type **before** it is assigned to anything.

- integer literal → `int`
- floating-point literal → `double`

Most literal errors come from that default.

### Integer bases

| Written | Value | Note |
| --- | --- | --- |
| `31` | 31 | decimal |
| `0x1F` | 31 | hex, `0x` or `0X` |
| `0b1_1111` | 31 | binary, Java 7+ |
| `013` | **11** | **octal** — a leading zero changes the base |
| `1_000_000` | 1000000 | underscores are ignored by the compiler |

The octal rule is a real trap: `013` is 11, not 13. It bites most often in
zero-padded values like dates and IDs.

### Suffixes

```java
long  big = 10_000_000_000L;   // without L: error: integer number too large
float f   = 3.14f;             // without f: possible lossy conversion from double to float
double sci = 1.5e3;            // 1500.0
```

`10_000_000_000` fails **before** any assignment happens — the literal itself is
an `int` and overflows, which is why a cast doesn't help and only the `L`
suffix does. Always write `L` uppercase; lowercase `l` is easily misread as `1`.

### char and String

`char` uses single quotes, `String` uses double. `'A'` and `'\u0041'` are the
same character — Unicode escapes are resolved before lexing (Day 3).

Text blocks (Java 15+) preserve line breaks without `\n`, and produce an
ordinary `String`.

### The string pool

Identical String literals are the **same object**, and javac folds
concatenations of compile-time constants into literals. So whether `==` succeeds
depends on whether the parts were `final`:

```java
String a = "hello";
String b = "hello";                       // a == b  -> true   (pooled)
String c = new String("hello");           // a == c  -> false  (new allocates)

final String P = "hel";
String folded = P + "lo";                 // a == folded -> true   (constant-folded)

String q = "hel";                         // not final
String notFolded = q + "lo";              // a == notFolded -> false (runtime)
```

All four verified. This is exactly why `==` on Strings appears to work in small
tests and then fails in production — **always use `.equals()`**.

## Run

```bash
javac ConstantsAndFinal.java && java ConstantsAndFinal
javac Literals.java && java Literals
```

## Takeaway

`final` means assigned once. Whether the value is a compile-time constant is a
*separate* property that depends on the initialiser, and it changes how the
value is compiled — inlined into readers, with the stale-value hazard that
follows. Immutability is a *third* property that `final` does not provide at all.
