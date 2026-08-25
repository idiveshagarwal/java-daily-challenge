# Day 12 — switch-case: A Simple Calculator

**25 Aug 2026 (Tue) · Ch 5 — Conditionals**

## Why a calculator fits switch

One variable (the operator) tested against a **fixed set of discrete
constants**. That's exactly the shape `switch` is for, and exactly what an
else-if ladder handles badly (Day 10). A ladder is for *ranges*; a switch is for
*values*.

```java
switch (op) {
    case '+': return format(left + right);
    case '-': return format(left - right);
    case '*': return format(left * right);
    case '/': return right == 0 ? "undefined" : format(left / right);
    case '%': return right == 0 ? "undefined" : format(left % right);
    default:  return "unknown operator";
}
```

Verified:

```
12 + 8   = 20
7  / 0   = undefined (division by zero)
17 % 5   = 2
3  ^ 4   = unknown operator '^' — use + - * / %
```

The zero guard matters because these are `double`s: without it Java returns
`Infinity` or `NaN` rather than throwing (Day 7), and neither is a useful
calculator answer.

## Three forms

| Form | Syntax | Falls through? | Produces a value? |
| --- | --- | --- | --- |
| classic statement | `case X:` … `break;` | **yes** | no |
| arrow statement | `case X -> …;` | no | no |
| switch **expression** | `var r = switch (x) { … };` | no | **yes** |

```java
// classic — break required on every branch
case '+': return fmt(l + r);

// arrow — no break, fall-through impossible
case '+' -> { return fmt(l + r); }

// expression — the whole switch yields a value
return switch (op) {
    case '+' -> fmt(l + r);
    case '/' -> {
        if (r == 0) yield "undef";     // yield supplies a block's value
        yield fmt(l / r);
    }
    default -> "?";
};
```

The arrow form (Java 14+) exists because fall-through was the wrong default:
correct code needed a `break` on every branch, and forgetting one was silent.

### Grouping cases

```java
case 1, 3, 5, 7, 8, 10, 12 -> "31 days";
case 4, 6, 9, 11           -> "30 days";
```

Comma-separated labels replace the old trick of stacking empty `case` lines.

## Selector types

Allowed: **`byte`, `short`, `char`, `int`** (and their wrappers), **`String`**
(Java 7+), **`enum`** (Java 5+).

The textbook rule is that `long`, `float`, `double` and `boolean` are *never*
allowed. On **JDK 25 that is no longer the whole story** — they compile as a
preview feature:

```
error: primitive patterns are a preview feature and are disabled by default.
```

And with preview on, this genuinely runs:

```bash
javac --release 25 --enable-preview P.java && java --enable-preview P
# two — switched on a long!
```

**In practice the old rule still holds**, since preview features are off by
default and shouldn't be relied on. But the *reason* has changed from "the
language forbids it" to "it isn't final yet", and the error message says so.

## Five traps

### 1. A missing `break` falls through — *silently*

```java
case 1: print("one");      // no break
case 2: print("two");      // no break
case 3: print("three"); break;
```

```
n=1 -> one two three
n=2 -> two three
n=3 -> three
```

Once a label matches, the remaining labels are **not tested** — execution just
continues into the next body. Only `break` (or `return`) stops it.

**javac is silent by default.** Turn the check on:

```bash
javac -Xlint:fallthrough SwitchTraps.java
```

```
warning: [fallthrough] possible fall-through into case
```

### 2. Deliberate fall-through looks identical to the bug

```java
case 'A':
case 'B':
case 'C':
    return "pass";
```

Legitimate, and indistinguishable at a glance from a forgotten `break`. Prefer
`case 'A', 'B', 'C' ->`, which cannot be misread.

### 3. String switch is case-sensitive

```
"add" -> matched
"ADD" -> fell through to default
```

Normalise first: `switch (cmd.toLowerCase())`.

### 4. A null selector throws — and `default` does not catch it

```java
String cmd = null;
switch (cmd) { case "add" -> …; default -> …; }   // NullPointerException
```

The switch must *read* the value to compare it, and that read is the NPE — it
happens before any label is considered. Guard with `if (cmd == null)` first.

### 5. `default` is position-independent

`default` runs when nothing else matched, wherever it is written — verified by
putting it first and still getting it last. But a `default` written *mid-switch*
falls through into the case below it if you forget the `break`. Put it last.

## Statement vs expression: the compiler's attitude changes

The same omission is treated completely differently:

| | Missing branch |
| --- | --- |
| switch **statement** | compiles silently, does nothing |
| switch **expression** | `error: the switch expression does not cover all possible input values` |

Because an expression must produce a value on every path, exhaustiveness is
**enforced**. That's a strong reason to prefer the expression form: the
compiler starts checking your logic instead of trusting it.

## Run

```bash
javac Calculator.java && printf '12 + 8\n' | java Calculator
```

```bash
javac SwitchForms.java && java SwitchForms
```

```bash
javac -Xlint:fallthrough SwitchTraps.java && java SwitchTraps
```

## Takeaway

`switch` matches one value against discrete constants — use it where a ladder
would need repeated equality tests, and a ladder where you need ranges.

Classic fall-through is the topic's defining bug and javac won't mention it
unless you ask with `-Xlint:fallthrough`. The arrow form removes the failure
mode entirely, and the expression form goes further by making the compiler
demand every case be covered.
