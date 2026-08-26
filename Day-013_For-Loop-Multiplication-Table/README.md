# Day 13 — for Loop: Multiplication Table

**26 Aug 2026 (Wed) · Ch 6 — Loops**

## Anatomy

```
for (init ; condition ; update) body
     │        │           │
     │        │           └─ runs AFTER each body execution
     │        └─ tested BEFORE each body execution
     └─ runs ONCE, before anything else
```

Traced by making each part print (`ForLoopAnatomy`):

```
init
condition i=1 -> true
  body  i=1
  update 1 -> 2
condition i=2 -> true
  body  i=2
  update 2 -> 3
condition i=3 -> true
  body  i=3
  update 3 -> 4
condition i=4 -> false
```

**The condition is checked before the first body run**, so a loop can execute
zero times — `for (int i = 5; i <= 3; i++)` runs 0 iterations. That's the
difference between `for`/`while` and `do-while`.

### All three parts are optional

```java
for (; i < 3; )   // no init, no update
for (;;)          // no parts at all — an empty condition means TRUE
```

`for (;;)` is infinite; `break` is the only way out.

### Commas in init and update

```java
for (int lo = 0, hi = 10; lo < hi; lo++, hi--)
```

Init and update accept comma-separated lists. The **condition is a single
boolean expression** — combine with `&&`.

### Counter scope

Declared in `init`, the counter doesn't exist after the loop. Declare it outside
only if you need the final value — and note it ends **one past** the last
iteration (`j = 3` after `j < 3`).

## The exercise

### One table — one loop

```java
for (int i = 1; i <= 10; i++) {
    System.out.printf("  %2d x %2d = %3d%n", n, i, n * i);
}
```

Starting at 1 rather than 0 because a times table has no zeroth row — **the
bound should express the problem, not a habit**.

### The full grid — nested loops

```
        1   2   3   4   5   6   7   8   9  10
     ----------------------------------------
  1 |   1   2   3   4   5   6   7   8   9  10
  2 |   2   4   6   8  10  12  14  16  18  20
  ...
 10 |  10  20  30  40  50  60  70  80  90 100
```

The inner loop runs **to completion for every single step** of the outer one —
the body executed 100 times, counted at runtime. Nested loops multiply: two
10-step loops are 100 steps; two 1000-step loops are 1,000,000.

`printf("%4d", …)` pads each cell to a fixed width so columns align regardless
of digit count.

## Four traps

### 1. Stray semicolon — *and javac will not help you*

```java
for (int i = 1; i <= 5; i++);
    total += 100;                // runs ONCE, not five times
```

Result: `total = 100`, not 500. The `;` is the loop body; the indented block is
just a bare block that executes after the loop finishes.

**This is worse than the `if` version of the same typo.** javac's `[empty]` lint
covers `if` only — verified on 25.0.4 with both mistakes in one file:

```
warning: [empty] empty statement after if
1 warning
```

The `for` version produced **no diagnostic at all**, even under `-Xlint:all`.

### 2. Off by one

| Header | Iterations |
| --- | --- |
| `i = 1; i < 10` | 9 (1..9) |
| `i = 1; i <= 10` | 10 (1..10) |

Convention worth keeping consistent:

- **start at 0 with `<`** — array indexing
- **start at 1 with `<=`** — counting things

Mixing the two is where off-by-one bugs come from.

### 3. Floating-point counter — infinite loop

```java
for (double d = 0; d != 1.0; d += 0.1)   // NEVER ENDS
```

`0.1` has no exact binary representation (Day 4), so adding it ten times does
not land on 1.0:

```
step  9: 0.89999999999999990
step 10: 0.99999999999999990   ← should be exactly 1.0
step 11: 1.09999999999999990
```

The condition `d != 1.0` never becomes false, and the loop steps straight past
its target. **Fix: count with an `int` and derive the double.**

```java
for (int i = 0; i <= 10; i++) {
    double d = i / 10.0;        // 0.0 0.1 0.2 … 1.0, exactly as intended
}
```

### 4. Overflow in the condition — infinite loop

```java
for (int i = 0; i <= Integer.MAX_VALUE; i++)   // NEVER ENDS
```

When `i` reaches `MAX_VALUE`, `i++` overflows to `MIN_VALUE` (Day 4) — which is
still `<= MAX_VALUE`. The condition **can never be false**. Verified by starting
two below the maximum and watching `i` wrap to `-2147483647`.

Fix: use a `long` counter, or test `i < Integer.MAX_VALUE`.

## Run

```bash
javac MultiplicationTable.java && printf '7\n' | java MultiplicationTable
```

```bash
javac ForLoopAnatomy.java && java ForLoopAnatomy
```

```bash
javac LoopTraps.java && java LoopTraps
```

## Takeaway

A `for` loop is init once, then condition–body–update repeating, with the
condition checked first — which is why zero iterations is a normal outcome.

Nested loops multiply rather than add, and the multiplication grid makes that
concrete at 100 body executions.

Two of the four traps produce **infinite loops from conditions that look
perfectly reasonable** — one because `0.1` isn't representable, the other
because `i++` wraps. Both share a root cause: the condition assumes the counter
moves in a way the type doesn't guarantee.
