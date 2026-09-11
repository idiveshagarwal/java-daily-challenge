# Day 28 — Pass by Value: The Swapping Demo

**10 Sep 2026 (Thu) · Ch 10 — Methods**

> Day 26 already established the pass-by-value rule and showed that
> `swap(int, int)` fails. Rather than repeat that, this day takes it as the
> starting point and asks the two follow-up questions: **given the limitation,
> how do you actually swap?** and **are the no-temp tricks any good?**

## Why `swap(int, int)` cannot work

```java
static void swapAttempt(int a, int b) {
    int temp = a; a = b; b = temp;
}

x=1 y=2  →  swapAttempt(x, y)  →  x=1 y=2
```

Nothing is wrong with the code. The method **did** swap — its own two copies,
correctly, right up to the closing brace. Then the frame was discarded.

The signature simply cannot express the intent: it has no way to reach the
caller's variables.

## Five things that do work

| # | Approach | When |
| --- | --- | --- |
| 1 | `swap(int[] a, int i, int j)` | **the standard** — every sort uses it |
| 2 | return both (record / array) | usually the honest design |
| 3 | one-element holders `int[]{v}` | hand-rolled out-parameter; ugly |
| 4 | swap fields on an object | when the values are already fields |
| 5 | `Collections.swap(list, i, j)` | lists — there is no `Arrays.swap` |

```java
swap(values, 0, 2)            -> [3, 2, 1]
Pair(1, 2).swapped()          -> Pair[first=2, second=1]
Collections.swap(list, 0, 2)  -> [c, b, a]
```

Option 1 is what Day 21's bubble sort does. Note what's happening: **nothing is
passed by reference** — the array reference is copied, and the copy still points
at the same array.

A generic version covers all reference types:

```java
static <T> void swap(T[] array, int i, int j)
```

…but **not `int[]`** — generics need a reference type, so primitives each need
their own overload. Same limitation that makes `IntStream` exist separately from
`Stream<Integer>`.

### Why Java is like this

| Language | Mechanism |
| --- | --- |
| C++ | `void swap(int& a, int& b)` — reference parameter |
| C# | `void Swap(ref int a, ref int b)` — `ref` parameter |
| C | `void swap(int *a, int *b)` — explicit pointer |
| **Java** | **not expressible** |

Java deliberately has no `out`/`ref` parameters. A call can therefore never
repoint a caller's variable — easier to reason about, at the cost of this one
exercise being impossible.

## Swapping without a temp

Two tricks, both offered as clever:

```java
a ^= b;  b ^= a;  a ^= b;        // XOR
a += b;  b = a - b;  a = a - b;  // arithmetic
```

Both produce `[20, 10]` from `[10, 20]`. Both have a failure mode — **and it
isn't the one usually cited.**

### The real failure: `i == j`

```
xor          swap(a, 0, 0) -> [0, 20]   ← DESTROYED
arithmetic   swap(a, 0, 0) -> [0, 20]   ← DESTROYED
temp         swap(a, 0, 0) -> [10, 20]
```

Swapping an element with itself should be a no-op. Both tricks **zero it**,
because each operates on one storage location twice:

```
a ^= a               is always 0
a = a + a; a = a - a; is also 0
```

This is not hypothetical: **sorting algorithms call `swap(i, i)` routinely** —
quicksort partitioning hits it constantly. A temp-based swap handles it for
free; the tricks silently corrupt the array.

### The overflow objection is wrong

The usual complaint about the arithmetic version is "`a + b` overflows, so it
fails". I tested it rather than repeating it:

```
tested 200025 pairs, including every combination of
MIN_VALUE, MAX_VALUE, 0, -1 and 1:
  arithmetic swap failures: 0
  xor swap failures:        0
```

**False for Java ints.** Two's-complement addition and subtraction are
*modular*, so `(a + b) - b == a` holds even when `a + b` wraps past
`MAX_VALUE` (Day 4).

The objection *would* be correct where overflow is undefined or throws — C
signed arithmetic, or Java's own `Math.addExact`.

### But floating point genuinely does break it

```
input                    after arithmetic swap    ok?
(1.000e+16, 1.000)       (0.000, 1.000e+16)       NO
(0.1000,    0.2000)      (0.2000, 0.1000)         NO
(1.798e+308, 1.000)      (0.000, 1.798e+308)      NO
```

Floating-point `+` and `-` **round** rather than wrap. `1e16 + 1.0` can't be
represented, so the `1.0` is lost and never comes back. Even `0.1`/`0.2` fail to
round-trip exactly — they *look* swapped but aren't equal to the originals.

### Verdict

| | `i == j` | int overflow | double |
| --- | --- | --- | --- |
| xor | **DESTROYS** | safe | n/a |
| arithmetic | **DESTROYS** | safe | **BREAKS** |
| **temp variable** | fine | safe | fine |

Use the temp variable. It's correct in every column, it reads as what it does,
and on any real JVM it isn't slower — the tricks save one local slot the
register allocator was going to reuse anyway.

The tricks are worth knowing only so you can explain why they're a bad idea —
and the honest reason is **aliasing, not the overflow everyone cites**.

## Run

```bash
javac SwapWorkarounds.java && java SwapWorkarounds
```

```bash
javac SwapTricks.java && java SwapTricks
```

## Takeaway

`swap(int, int)` is impossible in Java for a structural reason, not a syntactic
one — and the five workarounds are all really the same move: **put the values
somewhere both sides can see**, or hand them back as a return value.

The no-temp tricks are a good example of received wisdom being wrong in the
details. They *are* a bad idea, but not because of overflow — which I could only
establish by testing 200,025 pairs across the full int range.
