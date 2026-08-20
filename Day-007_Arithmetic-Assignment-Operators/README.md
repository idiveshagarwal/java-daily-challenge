# Day 7 — Arithmetic & Assignment Operators

**20 Aug 2026 (Thu) · Ch 3 — Operators**

## Arithmetic: five operators, three surprises

| Operator | Name | `17 op 5` |
| --- | --- | --- |
| `+` | addition | 22 |
| `-` | subtraction | 12 |
| `*` | multiplication | 85 |
| `/` | division | 3 — truncates (Day 4) |
| `%` | remainder | 2 |

`%` also works on floating-point: `5.5 % 2` is `1.5`.

Everything else — power, square root, absolute value — is a method on `Math`,
not an operator. Java has no `**`.

### Surprise 1: `%` takes the sign of the *dividend*

The left operand decides the sign. The right operand's sign is ignored entirely.

| Expression | Result |
| --- | --- |
| `7 % 3` | `1` |
| `-7 % 3` | `-1` — **not** 2 |
| `7 % -3` | `1` |
| `-7 % -3` | `-1` |

This quietly breaks the most common odd-number test:

```java
-7 % 2 == 1    // false  ← broken for negatives
-7 % 2 != 0    // true   ← correct
```

When you want the mathematical modulo (always non-negative for a positive
divisor), use `Math.floorMod`, which takes the sign of the **divisor**:

```java
-7 % 3               // -1
Math.floorMod(-7, 3) //  2
```

`floorMod` is what you want for wrap-around indexing — clock arithmetic, ring
buffers, circular lists.

### Surprise 2: division by zero behaves differently by type

| Expression | Result |
| --- | --- |
| `1 / 0` | throws `ArithmeticException: / by zero` |
| `1 % 0` | throws `ArithmeticException: / by zero` |
| `1.0 / 0` | `Infinity` |
| `-1.0 / 0` | `-Infinity` |
| `0.0 / 0.0` | `NaN` |

Integer division **throws**; floating-point division **returns an IEEE-754
special value** and keeps going. Note that `%` by zero reports `/ by zero` too —
the message names the wrong operator.

`NaN` is not equal to anything, including itself:

```java
0.0/0.0 == 0.0/0.0    // false
Double.isNaN(x)       // the only correct test
```

A `NaN` propagates silently through every subsequent calculation, which is why a
float pipeline can produce `NaN` at the end with no exception anywhere.

### Surprise 3: `+` is overloaded, and left-associative

`+` means concatenation if *either* operand is a `String`, otherwise addition.
Since it evaluates left to right, position changes the meaning:

```java
1 + 2 + "a"     // "3a"   ← 1+2 added first, then concatenated
"a" + 1 + 2     // "a12"  ← concatenation starts immediately
'a' + 1         // 98     ← char promotes to int (Day 5)
"" + 'a' + 1    // "a1"   ← leading "" forces concatenation
```

Hence the classic `println` bug:

```java
"sum: " + 1 + 2      // sum: 12
"sum: " + (1 + 2)    // sum: 3
```

## Increment and decrement

Both forms change the variable by one. They differ **only** in what the
expression evaluates to:

```java
int i = 5;  int p = i++;   // p = 5 (old value), i = 6
int j = 5;  int q = ++j;   // q = 6 (new value), j = 6
```

As a standalone statement `i++` and `++i` are identical. The distinction only
matters when the value is used.

### `k = k++` leaves k unchanged

```java
int k = 5;
k = k++;      // k is still 5
```

Step by step:

1. `k++` is evaluated → yields `5`, and sets `k` to `6`
2. the assignment writes the saved `5` back over it

The increment genuinely happens, then is immediately overwritten. `m = ++m`
"works" (gives 6) but is equally pointless. Write `k++;` on its own line.

## Assignment

`=` is an **expression**, not a statement — it produces a value. That one fact
explains the rest.

```java
x = y = 5;        // parses as x = (y = 5); right-associative
(n = 20)          // evaluates to 20
```

**Hazard:** because assignment yields a value, `if (flag = true)` compiles when
`flag` is a `boolean`. It assigns and is always true. Only `boolean` is
vulnerable — for other types the compiler rejects it.

### Compound operators

```
+=  -=  *=  /=  %=        arithmetic
&=  |=  ^=  <<=  >>=  >>>=    bitwise
```

Starting from 20: `+=5` → 25, `-=3` → 22, `*=2` → 44, `/=4` → 11, `%=7` → 4.

**The left side's type decides the arithmetic:**

```java
int    i = 7;  i /= 2;    // 3    ← still integer division
double d = 7;  d /= 2;    // 3.5
```

### Compound assignment hides a cast

`a op= b` is defined as `a = (T)(a op b)` where `T` is `a`'s type — the compiler
inserts a narrowing cast:

```java
byte b = 10;
b += 5;        // ✅ compiles — b = (byte)(b + 5)
b = b + 5;     // ❌ error: possible lossy conversion from int to byte
```

Covered from the casting angle in Days 4 and 5; repeated here because it is a
property of the *operator*, and it is where the silent-overflow risk lives.

### `+=` on String

Works with any right-hand type — the value is converted, not added:

```java
String s = "Day";
s += " "; s += 7; s += '!';   // "Day 7!"
```

Each `+=` allocates a new String. Fine occasionally; use `StringBuilder` in loops.

## Evaluation order is defined

Java guarantees the **left operand is fully evaluated before the right**, and
both before the operator applies:

```java
next()*100 + next()*10 + next()   // 123, always
```

This is specified behaviour. In C and C++ the equivalent is unspecified and may
differ between compilers — a rare case where Java is stricter than its ancestor.

## Precedence, highest to lowest

```
++ -- (unary)  +x -x
*  /  %
+  -
<<  >>  >>>
<  <=  >  >=  instanceof
==  !=
&  ^  |
&&  ||
?:
=  +=  -=  *=  /=  %=      (right-associative)
```

`*` `/` `%` bind tighter than `+` `-`, as in ordinary maths. When in doubt add
parentheses — they cost nothing and the compiler removes them.

## Run

```bash
javac ArithmeticOperators.java && java ArithmeticOperators
javac AssignmentOperators.java && java AssignmentOperators
```

## Takeaway

The operators are ordinary; the edge cases are not. `%` follows the dividend's
sign, integer division throws on zero while floating-point returns `Infinity` or
`NaN`, `+` changes meaning based on operand types and reads left to right, and
compound assignment silently inserts a narrowing cast.
