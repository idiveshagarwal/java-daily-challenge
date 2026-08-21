# Day 8 — Relational, Logical & Ternary Operators

**21 Aug 2026 (Fri) · Ch 3 — Operators**

## Relational: six operators, all yielding `boolean`

| Operator | Works on |
| --- | --- |
| `<` `<=` `>` `>=` | numeric types and `char` only |
| `==` `!=` | those, plus `boolean` and any reference |

`char` compares by code point, so `'a' < 'b'` is `true`. You cannot order
booleans or objects — only test them for equality.

### NaN is unordered

Every comparison involving `NaN` is **false**, including `NaN == NaN`. The only
one that returns `true` is `!=`.

| Expression | Result |
| --- | --- |
| `NaN < 1` | `false` |
| `NaN > 1` | `false` |
| `NaN <= 1` | `false` |
| `NaN == NaN` | `false` — not even reflexive |
| `NaN != NaN` | **`true`** |

The practical consequence is a silent routing bug:

```java
if (x < 1) { /* A */ } else { /* B */ }
```

A `NaN` goes to branch **B** — not because it's `>= 1`, but because the test was
false. Both branches of a comparison can be "wrong" at once. Test with
`Double.isNaN(x)`.

### Negative zero: three comparisons, two answers

IEEE-754 has both `+0.0` and `-0.0`, and Java's three ways of comparing them
disagree:

| Comparison | Result |
| --- | --- |
| `-0.0 == 0.0` | `true` — equal |
| `Double.compare(-0.0, 0.0)` | `-1` — less than |
| `Double.valueOf(-0.0).equals(0.0)` | `false` — not equal |

This is not academic: a `TreeMap` orders keys with `compare()` while a `HashMap`
buckets them with `equals()`, so the same pair of values can be one key or two
depending on the collection.

### `==` on references

`==` asks **"same object?"**, never "same contents?".

```java
"java" == new String("java")      // false — different objects
"java".equals(new String("java")) // true  — same contents

Integer a = 127, b = 127;   a == b   // true  — Integer cache (-128..127)
Integer c = 128, d = 128;   c == d   // false — outside the cache
```

Identical code, different answer purely by value. Wrapper caching is covered in
Day 4 and the String pool in Day 6; the rule here is the operator's:
**`==` for primitives, `.equals()` for objects.**

## Logical: `&&` `||` `!` and `&` `|` `^`

```
  a      b      a&&b   a||b   a^b
  true   true   true   true   false
  true   false  false  true   true
  false  true   false  true   true
  false  false  false  false  false
```

### Short-circuiting is the whole point

`&&` and `&` compute the **same answer**. They differ in whether the right
operand is evaluated at all:

| Expression | Right side ran? |
| --- | --- |
| `false && f()` | **no** — skipped |
| `false & f()` | yes |
| `true \|\| f()` | **no** — skipped |
| `true \| f()` | yes |

Verified with a call counter. The result is identical every time; only side
effects differ.

This is what makes the standard guard work:

```java
if (s != null && s.length() > 0)      // safe
if (arr.length > 0 && arr[0] == 1)    // safe
```

With `&` instead of `&&`, the first line throws `NullPointerException` and the
second `ArrayIndexOutOfBoundsException`. **The guard must come first** — `&&`
protects what's to its right, not its left.

### `^` — exclusive or

True when the operands differ. It has **no short-circuit form**, because neither
operand alone can decide the answer. Reads naturally as "exactly one of these":

```java
boolean valid = hasEmail ^ hasPhone;   // exactly one contact method
```

## Ternary: `condition ? a : b`

Java's only three-operand operator. Like `&&`, **only one branch is evaluated**.

Its defining property is that it is an **expression** — it produces a value, so
it fits where an `if/else` cannot:

```java
System.out.println(count + " item" + (count == 1 ? "" : "s"));
Math.abs(count > 0 ? -5 : 5);
```

### Surprise 1: both branches decide the type

The type of the whole expression is computed from **both** branches *before*
either is chosen. Different numeric types trigger binary numeric promotion
(Day 5) — so the branch you don't take changes the value of the one you do:

```java
Object result = true ? 1 : 2.0;    // → 1.0, a Double
```

The condition is `true`, so the taken branch is the `int` `1`. The result is
still a `double`, because `int` and `double` promote to `double`. With wrappers
it's worse — they get unboxed, promoted, and re-boxed:

```java
true ? Integer.valueOf(1) : Double.valueOf(2.0)   // → 1.0, a Double
```

**Fix: keep both branches the same type.**

### Surprise 2: unboxing can throw NPE

If one branch is a wrapper and the other a primitive, the expression's type is
the *primitive* — so the wrapper is unboxed:

```java
Integer maybeNull = null;
int v = (1 > 0) ? maybeNull : 0;   // NullPointerException
```

The `null` is unboxed even though nothing asked for its value as an `int`. Make
both branches wrappers and it's harmless:

```java
Integer safe = (1 > 0) ? maybeNull : Integer.valueOf(0);   // → null
```

### Nesting

`?:` is right-associative, so chains read top to bottom:

```java
String grade = score >= 90 ? "A"
             : score >= 80 ? "B"
             : score >= 70 ? "C"
             : "F";
```

Two levels is about the limit before `if/else` or a switch expression is
clearer.

## Run

```bash
javac RelationalOperators.java && java RelationalOperators
javac LogicalOperators.java   && java LogicalOperators
javac TernaryOperator.java    && java TernaryOperator
```

## Takeaway

Comparison is not as total as it looks: `NaN` fails every ordering test
including equality with itself, and `-0.0` is equal or not depending on which
of three mechanisms you ask.

`&&` and `&` agree on the answer and disagree on what runs — which is why the
short-circuit forms are the ones that make null guards safe.

The ternary's type comes from **both** branches, so the untaken one can promote
your result to `double` or force an unboxing `NullPointerException`.
