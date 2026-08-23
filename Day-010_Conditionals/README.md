# Day 10 — if, if-else & the else-if Ladder

**23 Aug 2026 (Sun) · Ch 5 — Conditionals**

## The condition must be a `boolean`

Java's one strict departure from C. There is no truthiness — `0` is not false
and a non-null reference is not true.

```java
int count = 3;
if (count) { }        // error: incompatible types: int cannot be converted to boolean
```

This deletes the most famous C typo **for numeric types**:

```java
if (x = 1)            // error: int cannot be converted to boolean
```

But it does **not** protect booleans, because a boolean assignment *is* a valid
boolean expression:

```java
boolean loggedIn = false;
if (loggedIn = true) { ... }    // compiles, assigns, always true
```

Verified: `loggedIn` is `true` afterwards — the check changed the value it was
checking. Prefer `if (flag)` over `if (flag == true)`; then there's no `=` to
mistype.

## if / else

```java
if (temperature > 30) {
    ...
} else {
    ...
}
```

Exactly one branch runs; `else` is optional.

### Conditions on objects

```java
input != null && input.equals("yes")   // && guard prevents the NPE (Day 8)
"yes".equals(input)                    // Yoda form — null-safe with no guard
```

Never `==` on Strings in a condition — that compares identity, not contents
(Day 6).

### Simplify boolean returns

```java
if (age >= 18) return true; else return false;   // redundant
return age >= 18;                                // the condition IS the answer
```

## The else-if ladder

There is no `elseif` keyword. A ladder is just an `if` inside the previous
`else`; the flat formatting is convention, not syntax.

```java
if (score >= 90)      return "A";
else if (score >= 80) return "B";
else if (score >= 70) return "C";
else if (score >= 60) return "D";
else                  return "F";
```

### First match wins

Once a branch matches, the remaining conditions are **never tested**. So later
branches don't need to re-exclude earlier ranges — reaching `score >= 80`
already proves `score < 90`, making `&& score < 90` redundant.

```
score  95 -> A     score  73 -> C     score  40 -> F
score  85 -> B     score  61 -> D
```

### Order is part of the logic

The same four conditions written loosest-first collapse to one answer:

```java
if (score >= 60)      return "D";   // catches EVERYTHING from 60 up
else if (score >= 70) return "C";   // never reached in practice
else if (score >= 80) return "B";   // never reached
else if (score >= 90) return "A";   // never reached
```

Verified side by side:

```
score  95 -> D   (correct: A)
score  85 -> D   (correct: B)
score  73 -> D   (correct: C)
score  61 -> D   (correct: D)
```

**javac does not flag this.** The branches are reachable in principle — only
unreachable given the earlier test — so there is no dead-code error. Order
overlapping ranges **most specific first**.

### End with a final `else`

A ladder with no final `else` silently does nothing when nothing matches. When
the ladder assigns a value that is later read, javac catches the omission for
you (definite assignment); in a `void` ladder it does not. Add the `else` and
make the unhandled case explicit.

### Ladder or switch?

| Use | For |
| --- | --- |
| else-if ladder | **ranges** and arbitrary boolean tests — `score >= 90`, `a > b && c`, `s.isEmpty()` |
| `switch` | one variable against **discrete constants** — `day == MONDAY`, `code == 404` |

The grading example must be a ladder; you cannot `switch` on a range.

## Four silent traps

None are compile errors. Only the first produces a warning, and only with
`-Xlint:all` (Day 3).

### 1. Stray semicolon — *warned*

```java
if (x > 0);                    // ← the empty statement IS the body
    System.out.println(...);   // unguarded
```

Printed with `x = -5`. javac reports:

```
warning: [empty] empty statement after if
```

### 2. Missing braces — *silent*

```java
if (authorised)
    log("granted");
    grantAccess();     // runs ALWAYS — indentation is not syntax
```

Verified: `ACCESS GRANTED` printed with `authorised = false`. This is the shape
of Apple's 2014 "goto fail" TLS vulnerability. **Always use braces.**

### 3. Dangling else — *silent*

An `else` binds to the **nearest unmatched `if`**, whatever the indentation
suggests:

```java
if (p > 0)
    if (q > 0) ...
else ...          // pairs with the INNER if, not the outer
```

Verified with `p = 5, q = -1`: the else branch ran even though `p > 0` was true
— proving it belongs to `if (q > 0)`. Braces remove the ambiguity.

### 4. `=` instead of `==` — *silent for booleans*

Covered above. Numeric types are protected by the type system; booleans are not.

## Run

```bash
javac IfBasics.java && java IfBasics
javac ElseIfLadder.java && java ElseIfLadder
javac -Xlint:all ConditionalTraps.java && java ConditionalTraps
```

The `-Xlint:all` on the third is deliberate — it's what surfaces the empty-
statement warning.

## Takeaway

Requiring a `boolean` condition removes C's truthiness bugs for numbers but
leaves `if (flag = true)` intact.

In a ladder the first match wins, so **order is logic** — loosest-first silently
makes later branches unreachable, and the compiler will not tell you.

Of the four classic traps, javac warns about exactly one, and only with
`-Xlint:all`. Braces on every branch defuse two of the remaining three.
