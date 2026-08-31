# Day 18 — Number Patterns & Floyd's Triangle

**31 Aug 2026 (Mon) · Ch 7 — Patterns**

## The new idea: state across rows

Day 17's star patterns were **stateless**. Row `i` was computed from `i` and `n`
alone, so any row could be printed in isolation.

Floyd's triangle is the first pattern where that stops being true:

```java
int counter = 1;                        // declared OUTSIDE both loops
for (int row = 1; row <= n; row++) {
    for (int col = 1; col <= row; col++) {
        System.out.print(counter + " ");
        counter++;                      // never resets
    }
    System.out.println();
}
```

```
  1
  2  3
  4  5  6
  7  8  9 10
 11 12 13 14 15
```

The value printed depends on **everything already printed**. Move `int counter
= 1` inside the outer loop and you get a different pattern entirely — the
"count up each row" one below.

That contrast is the whole day:

| Pattern | Counter |
| --- | --- |
| Floyd's triangle | declared **outside**, never resets |
| Count up each row | declared **inside**, resets every row |

## The formulas

Row `i` holds `i` values, so the last value in row `i` is `1 + 2 + … + i` — the
**i-th triangular number**:

| row | count | starts | ends |
| --- | --- | --- | --- |
| 1 | 1 | 1 | 1 |
| 2 | 2 | 2 | 3 |
| 3 | 3 | 4 | 6 |
| 4 | 4 | 7 | 10 |
| 5 | 5 | 11 | 15 |

```
row i starts at  i(i-1)/2 + 1
row i ends at    i(i+1)/2
n rows contain   n(n+1)/2 values
```

Verified by walking the real counter against the closed form for rows 1–200 —
**20,100 values emitted, matching `n(n+1)/2` exactly, zero failures** at every
row boundary.

## Alignment becomes a real problem

Stars are one character wide. Numbers aren't, and they grow:

```java
int last = n * (n + 1) / 2;                  // biggest value we will print
int width = String.valueOf(last).length();
System.out.printf("%" + (width + 1) + "d", counter);
```

Compute the width from the largest value rather than guessing a `%3d` that
breaks at `n = 14` (where the last value passes 99). This is the first pattern
where the *formatting* needs the same arithmetic as the pattern itself.

## The other patterns

| Pattern | Rule |
| --- | --- |
| Row number repeated | print `i`, `i` times |
| Count up each row | `1..i`, counter resets |
| Number pyramid | Day 17's spacing, counting `1..i` |
| Palindromic pyramid | up then down — `2i-1` values, same odd count as the star pyramid |
| Pascal's triangle | each entry = sum of the two above |

```
      1
    1 2 1
  1 2 3 2 1
1 2 3 4 3 2 1
```

The palindromic pyramid has `2i - 1` values per row — **the same odd count as
Day 17's star pyramid**, which is why it forms the identical silhouette.

## Pascal's triangle

Built by updating one row in place:

```java
for (int k = i; k > 0; k--) {       // RIGHT to left
    row[k] = row[k] + row[k - 1];
}
row[0] = 1;
```

**The direction matters.** Going left to right would overwrite `row[k-1]`
before the next entry needs to read it.

Three properties checked on rows 0–19, all holding:

- row `i` sums to **2^i**
- row `i` is **symmetric**
- entry `(i,k)` equals **C(i,k)**, computed independently

The binomial check computes `C(n,k)` multiplicatively, dividing as it goes —
computing `n!` first would overflow by `n = 21`, before it could verify
anything.

### Pascal outgrows `int` at row 34

```
Integer.MAX_VALUE = 2147483647
first row exceeding it: row 34, largest entry 2333606220
```

An `int[]` triangle **wraps silently** from row 34 on — no exception, just
wrong numbers, the same failure as Day 14's reverse.

`long` pushes the limit to **row 67**. Worth noticing: doubling the bit width
buys about 33 more rows, not twice as many, because the entries grow
exponentially. `BigInteger` is the only real answer for deep triangles.

## Run

```bash
javac NumberPatterns.java && printf '6\n' | java NumberPatterns
```

```bash
javac PatternMath.java && java PatternMath
```

## Takeaway

Number patterns split into two kinds, and the difference is one line: **where
the counter is declared**. Outside the loops it accumulates across rows
(Floyd's); inside, it restarts.

Once values replace stars, two new concerns arrive that stars never had —
**field width**, which needs the same arithmetic as the pattern, and
**overflow**, which arrives at row 34 for `int` and row 67 for `long`.
