# Day 26 — Writing Your First Method

**08 Sep 2026 (Tue) · Ch 10 — Methods**

Every earlier day used methods to organise its output. This is the day they
become the subject.

## Anatomy

```
static int square(int n) { return n * n; }
 ──┬──  ─┬─ ──┬──  ─┬──     ────┬─────
modifier │  name  parameter    body
      return type
```

A **parameter** is the variable in the declaration (`int n`); an **argument** is
the value at the call site (`5`). They match by **position**, not name:

```java
power(2, 10)   // 1024  — 2^10
power(10, 2)   // 100   — 10^2
```

A `void` method may still write a bare `return;` to exit early. That guard-clause
style often removes a whole level of nesting.

## `static` vs instance

The distinction behind the most common first-week compile error.

| | Belongs to | Called as |
| --- | --- | --- |
| `static` | the **class** | `MethodBasics.square(5)` |
| instance | an **object** | `obj.recordCall()` |

`main` is `static`, so it can call other static methods directly. Calling an
instance method from it does not compile:

```
error: non-static method recordCall() cannot be referenced from a static context
```

The error is really asking **"which object's field did you mean?"** — and there
is no answer, because `main` isn't running on one. Each object has its own state:

```
instanceOne.recordCall() -> 1
instanceOne.recordCall() -> 2
instanceTwo.recordCall() -> 1   ← its own counter
```

## Four things javac enforces

Unusually for this course so far, **all four are compile errors** rather than
silent runtime surprises:

```java
static int f(int n) { if (n > 0) return 1; }
// error: missing return statement

return 1; System.out.println("never");
// error: unreachable statement

static void f() { return 1; }
// error: incompatible types: unexpected return value

recordCall();   // from main
// error: non-static method ... cannot be referenced from a static context
```

## Pass by value — the important part

**Java is always pass by value. There is no exception for objects.**

The confusion arises because for an object the value being copied is a
*reference*. The method gets its own copy of the arrow:

```
caller:  arr ──────┐
                   ▼
               [1, 2, 3]      mutating through either arrow is visible
                   ▲
method:  arr ──────┘          repointing this one changes nothing above
```

### Primitives are copied

```java
static void swap(int a, int b) { int t = a; a = b; b = t; }

swap(a, b);   // a=1 b=2 — unchanged
```

The method swapped its copies perfectly, then they went out of scope. **You
cannot write a swap method for two ints in Java** — return them, or pass an
array.

### Mutation is visible

```java
mutateArray(numbers)  ->  [99, 2, 3]            ← CHANGED
mutateBuilder(text)   ->  [original changed]    ← CHANGED
```

The copied reference points at the **same object**, so changes made through it
are changes to the caller's object — the aliasing of Days 19 and 22.

### Reassignment is not

```java
static void reassignArray(int[] values) {
    values = new int[] { 7, 7, 7 };
    values[0] = 100;
}

reassignArray(numbers)  ->  [1, 2, 3]   ← unchanged
```

**This is the proof that Java is not pass by reference.** If it were,
reassigning the parameter would repoint the caller's variable too. It doesn't.

### Immutable objects make it moot

```java
tryToChange(text)  ->  [original]   ← unchanged
```

Two independent reasons: reassigning the parameter can't escape, *and* `String`
has no mutating method to call in the first place (Day 23).

### The rule

| argument type | mutate it? | reassign it? |
| --- | --- | --- |
| primitive | n/a | no effect |
| **mutable object** | **VISIBLE** | no effect |
| immutable object | impossible | no effect |

In one sentence: **a method can change what the object contains, never which
object your variable points at.**

"Java passes objects by reference" is the most common wrong statement about the
language. It passes references **by value** — a different thing, and the middle
row is where the difference shows.

Practical consequence: passing a `String` is always safe; passing an array or a
`StringBuilder` is not, because the callee can edit what you handed it. Pass a
copy when that matters.

## Run

```bash
javac MethodBasics.java && java MethodBasics
```

```bash
javac PassByValue.java && java PassByValue
```

## Takeaway

A method names an idea, so the call site reads as intent (`isEven(n)`) rather
than mechanism (`n % 2 == 0`) — and gives you one place to fix a bug and one
place to test.

The `static`/instance error is asking which object you meant. The pass-by-value
rule is one sentence with three consequences, and the middle row of that table —
mutation visible, reassignment not — is the whole reason the "by reference"
myth persists.
