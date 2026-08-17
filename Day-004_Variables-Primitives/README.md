# Day 4 — Variables & Primitive Data Types

**17 Aug 2026 (Mon) · Ch 2 — Variables**

## The three kinds of variable

| Kind | Declared | Lives | Default |
| --- | --- | --- | --- |
| **Instance** | in the class body | one copy per object | yes |
| **Static** (class) | in the class body, `static` | one copy per class | yes |
| **Local** | inside a method or block | one per invocation | **no** |

The default column is the important one. Fields are zeroed by the JVM during
object initialisation. Local variables are not — the compiler's
*definite-assignment* analysis rejects any read of a local that has not been
assigned on every path to that point:

```java
int local;
System.out.println(local);   // error: variable local might not have been initialized
```

This is a compile error, not a runtime surprise. Java has no "uninitialised
memory" bug class for locals.

## The eight primitives

Sizes are fixed by the language spec, not the platform — an `int` is 32 bits
everywhere. That is a deliberate departure from C.

| Type | Bits | Bytes | Range | Default |
| --- | --- | --- | --- | --- |
| `byte` | 8 | 1 | −128 .. 127 | `0` |
| `short` | 16 | 2 | −32,768 .. 32,767 | `0` |
| `int` | 32 | 4 | −2,147,483,648 .. 2,147,483,647 | `0` |
| `long` | 64 | 8 | ±9.22 × 10¹⁸ | `0L` |
| `float` | 32 | 4 | ~7 significant decimal digits | `0.0f` |
| `double` | 64 | 8 | ~15 significant decimal digits | `0.0d` |
| `char` | 16 | 2 | 0 .. 65,535 (**unsigned**) | `'\u0000'` |
| `boolean` | JVM-defined | — | `true` / `false` | `false` |

Three things worth pinning down:

- **`char` is the only unsigned type.** It is a 16-bit integer that happens to
  print as text.
- **`boolean` has no specified size.** The spec leaves it to the JVM, which
  typically uses a full word on the stack and a byte inside arrays.
- **`String` is not a primitive.** It is a reference type, which is why its
  default is `null` rather than a zero value.

### MIN_VALUE means two different things

For integral types `MIN_VALUE` is the most negative value. For `float` and
`double` it is the smallest **positive** value:

```
Double.MIN_VALUE = 4.9E-324          <- smallest positive, not lowest
lowest double    = -1.7976931348623157E308   (= -Double.MAX_VALUE)
```

A frequent exam trap.

## Literals

```java
int decimal = 1_000_000;
int hex     = 0xFF;           // 255
int binary  = 0b1010_1010;    // 170
int octal   = 0777;           // 511 — a LEADING ZERO means octal
long big    = 9_000_000_000L; // L required, the value exceeds int range
float f     = 3.14F;          // F required, 3.14 alone is a double
double sci  = 1.5e3;          // 1500.0
```

Underscores are stripped by the lexer and are purely a readability aid. They may
not lead, trail, or sit adjacent to the decimal point.

`0777` is the one to watch: a leading zero silently changes the base, so a
zero-padded value like `018` is not "18 padded", it is a compile error (8 is not
an octal digit).

## Conversions

**Widening** is implicit, **narrowing** requires an explicit cast:

```
byte -> short -> int -> long -> float -> double
        char  -> int
```

```java
double pi = 3.99;
int n = (int) pi;    // 3 — casts TRUNCATE, they never round
Math.round(pi);      // 4 — rounding is a method call, not a cast
```

Widening is not the same as lossless. `int -> float` and `long -> float/double`
widen the *range* while losing *precision*, because the target has fewer
significant digits:

```
int 123456789 -> float = 1.2345679E8
```

## var — type inference, not dynamic typing

```java
var count = 10;        // inferred int
var name  = "Divesh";  // inferred String
```

The variable is still statically typed; `count = "text"` does not compile. `var`
works only on locals with an initialiser — never on fields, parameters, return
types, or with `null`.

(`var` is a *contextual* keyword, per Day 3 — `int var = 5;` is still legal.)

## Gotchas

`PrimitiveGotchas.java` collects five behaviours that are correct Java and also
recurring sources of real bugs.

### 1. Overflow wraps silently

```
Integer.MAX_VALUE + 1  = -2147483648
1_000_000 * 1_000_000  = -727379968      <- int maths, overflowed
1_000_000L * 1_000_000 = 1000000000000   <- one L fixes it
```

The multiplication overflows **before** any widening to `long`, so assigning the
result to a `long` does not save you. Promote an operand instead, or use
`Math.addExact` / `multiplyExact`, which throw on overflow.

### 2. Floating point is binary

```
0.1 + 0.2        = 0.30000000000000004
0.1 + 0.2 == 0.3 ? false
NaN == NaN       ? false
```

Compare with a tolerance, and use `BigDecimal` for money — never `double`.
Note `1/0` throws `ArithmeticException` but `1.0/0` returns `Infinity`;
floating-point division has no zero-divisor error.

### 3. Integer division truncates

```
7 / 2   = 3      <- both operands int
7 / 2.0 = 3.5    <- one double promotes both
-7 / 2  = -3     <- truncates toward zero
-7 % 2  = -1     <- sign follows the dividend
```

### 4. Compound assignment hides a narrowing cast

```java
byte b = 10;
b = b + 300;   // error: possible lossy conversion from int to byte
b += 300;      // compiles — and silently gives 54
```

`b += 300` is *defined* as `b = (byte)(b + 300)`. The shorthand is not
equivalent to the long form; it carries a free cast.

### 5. `==` on wrappers compares references

```
Integer 127 == 127 ? true    <- served from the -128..127 cache
Integer 128 == 128 ? false   <- distinct objects
c.equals(d)        ? true    <- always use equals()
```

Mixing an `int` with an `Integer` unboxes and compares by value, so `==` flips
behaviour depending on the static types. Unboxing a `null` wrapper throws
`NullPointerException`.

### javac agrees

Three of these are visible to the compiler under `-Xlint:all`:

```
warning: [divzero] division by zero
warning: [lossy-conversions] implicit cast from int to byte in compound assignment is possibly lossy
warning: [lossy-conversions] implicit cast from int to short in compound assignment is possibly lossy
```

Worth running with `-Xlint:all -Werror` on real code.

## Run

```bash
javac VariablesAndPrimitives.java && java VariablesAndPrimitives
javac PrimitiveGotchas.java && java PrimitiveGotchas
```

## Output

`VariablesAndPrimitives`:

```
Day 4 — Variables & primitive data types

The eight primitives
  type      bits  bytes  range
  byte         8      1  -128 .. 127
  short       16      2  -32768 .. 32767
  int         32      4  -2147483648 .. 2147483647
  long        64      8  -9223372036854775808 .. 9223372036854775807
  float       32      4  ~7 decimal digits of precision
  double      64      8  ~15 decimal digits of precision
  char        16      2  0 .. 65535 (unsigned)
  boolean    JVM    JVM  true / false

Float/double MIN_VALUE is the smallest POSITIVE value:
  Double.MIN_VALUE = 4.9E-324
  lowest double    = -1.7976931348623157E308

Default values of uninitialised FIELDS
  byte    : 0
  short   : 0
  int     : 0
  long    : 0
  float   : 0.0
  double  : 0.0
  char    : code point 0 (NUL)
  boolean : false
  String  : null (reference, not a primitive)

Locals get NO default — reading one unassigned is a compile error.
  days in a week (static final) = 7

Literal forms
  1_000_000     = 1000000
  0xFF          = 255
  0b1010_1010   = 170
  0777 (octal!) = 511
  9_000_000_000L= 9000000000
  3.14F         = 3.14
  3.14          = 3.14
  1.5e3         = 1500.0

char is a 16-bit unsigned integer
  'A'            = A
  (int) 'A'      = 65
  'A' + 1        = 66   <- promoted to int
  (char)('A'+1)  = B
  '\u0041'       = A

Conversions
  widening : byte -> short -> int -> long -> float -> double
             char -> int is widening too
  int 42 -> long -> double : 42.0
  (int) 3.99               = 3   <- truncated, not rounded
  Math.round(3.99)         = 4

  int 123456789 -> float   = 1.2345679E8   <- widening, but precision lost

var — local variable type inference (Java 10+)
  var count = 10     -> Integer
  var name  = "Divesh" -> String
  var ratio = 2.5    -> Double
  var flag  = true   -> Boolean

  count is fixed at int — count = "text" would not compile.
```

`PrimitiveGotchas`:

```
1. Overflow wraps silently
   Integer.MAX_VALUE     = 2147483647
   Integer.MAX_VALUE + 1 = -2147483648   <- now negative
   1_000_000 * 1_000_000       = -727379968   <- int maths
   1_000_000L * 1_000_000      = 1000000000000   <- long maths
   Math.addExact throws instead:      integer overflow
   Math.multiplyExact catches the mul: integer overflow

2. Floating point is binary, not decimal
   0.1 + 0.2            = 0.30000000000000004
   0.1 + 0.2 == 0.3     ? false
   difference           = 5.551115123125783E-17
   compare with a tolerance instead:
     |sum - 0.3| < 1e-9 ? true
   or use BigDecimal for money — never double.
   special values: Infinity, -Infinity, NaN
   NaN == NaN           ? false   <- use Double.isNaN()

3. Integer division truncates
   7 / 2       = 3     <- both operands int
   7 / 2.0     = 3.5   <- one double promotes both
   7 % 2       = 1
   -7 / 2      = -3    <- truncates toward zero
   -7 % 2      = -1    <- sign follows the dividend
   1 / 0       throws java.lang.ArithmeticException: / by zero
   1.0 / 0     = Infinity   <- floating point does NOT throw

4. Compound assignment hides a narrowing cast
   byte b = 10; b += 300   -> 54   (b = (byte)(b + 300))
   short s = 1; s *= 100000 -> -31072
   b = b + 300 does NOT compile — the shorthand is not equivalent.

5. == on wrappers compares references
   Integer 127 == 127   ? true   <- cached -128..127
   Integer 128 == 128   ? false  <- new objects
   c.equals(d)          ? true   <- always use equals()
   int 128 == Integer 128 ? true   <- unboxes, so value compare
   unboxing null throws NullPointerException
```

Note: the em dashes above render as `?` on a console whose encoding is not
UTF-8. That is a terminal setting, not the program — run with
`-Dstdout.encoding=UTF-8` if your locale is ASCII.

## Takeaway

A primitive's size and default are fixed by the spec, but *when* a default is
supplied is not uniform: fields get one, locals never do.

Most primitive bugs come from conversions you did not write — the implicit
promotion to `int` before arithmetic, the free narrowing cast inside `+=`, and
the boxing that turns `==` from a value comparison into a reference comparison.
