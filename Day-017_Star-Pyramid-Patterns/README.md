# Day 17 — Star Pyramid Patterns

**30 Aug 2026 (Sun) · Ch 7 — Patterns**

## Every pattern is the same skeleton

```java
for (row = 1; row <= n; row++) {     // which line
    for (...) print(' ');            // leading spaces
    for (...) print('*');            // the stars
    println();                       // end the line
}
```

Only the two inner bounds change. **Getting a pattern right isn't about the
loops** — it's about writing the spaces and stars for row `i` as a formula in
`i` and `n`.

## The formulas

| Pattern | spaces | stars |
| --- | --- | --- |
| Right triangle | 0 | `i` |
| Inverted right triangle | 0 | `n - i + 1` |
| **Pyramid** | `n - i` | `2i - 1` |
| Inverted pyramid | `i - 1` | `2(n - i) + 1` |
| Diamond | pyramid, then inverted from `n-1` down |
| Hollow pyramid | same as pyramid; star only at edges |

## Deriving the pyramid row

```
  i   spaces    stars    width   row
  1        4        1        9   [    *    ]
  2        3        3        9   [   ***   ]
  3        2        5        9   [  *****  ]
  4        1        7        9   [ ******* ]
  5        0        9        9   [*********]
```

Two observations that make the formula obvious once seen:

- Each row **loses one space and gains two stars**, so the width is constant at
  `2n - 1`.
- The star count is always **odd** — which is exactly what allows a single apex
  and a symmetric base. An even count would give a two-character tip.

## The star total is checkable

The stars per row are the odd numbers `1, 3, 5, …, 2n-1`, and the sum of the
first *n* odd numbers is **n²**:

```
  n                1 + 3 + 5 + ...      sum      n^2
  3                      1 + 3 + 5        9        9
  4                  1 + 3 + 5 + 7       16       16
  5              1 + 3 + 5 + 7 + 9       25       25
```

So a pyramid of `n` rows uses exactly `n²` stars — knowable in advance, which
turns "does my pattern look right" into something a test can answer.

## Verifying instead of eyeballing

Building each row as a `String` makes the geometry testable. `PatternFormulas`
checks five properties for `n = 1..40`:

1. every padded row is `2n - 1` wide
2. every row is a palindrome
3. every row has an **odd** star count
4. total stars = `n²`
5. a diamond has `2n - 1` rows

```
n = 1..40, 820 rows checked
failures: 0
every property held for every size.
```

### One subtlety the check exposed

To be *genuinely* palindromic a row needs padding on **both** sides:

```java
" ".repeat(n - i) + "*".repeat(2 * i - 1) + " ".repeat(n - i)
```

Printed output normally omits the right-hand padding — invisible on screen, but
it means a naive string comparison of *printed* lines would report the pyramid
as asymmetric. The pattern is symmetric; the printed text isn't.

## The two that need care

### Diamond — don't repeat the middle row

```java
for (int i = 1; i <= n; i++)      printRow(n - i, 2 * i - 1);   // upper
for (int i = n - 1; i >= 1; i--)  printRow(n - i, 2 * i - 1);   // lower
```

The lower half starts at **`n - 1`**, not `n`. Starting at `n` prints the widest
row twice and the diamond gains a flat middle. Row count is `2n - 1`, not `2n`.

### Hollow pyramid — the loops don't change

```java
boolean edge = (s == 1 || s == 2 * i - 1);
boolean lastRow = (i == n);
System.out.print(edge || lastRow ? "*" : " ");
```

Identical bounds to the solid pyramid — only the *decision inside* differs. The
base must be solid or the shape has no bottom.

```
    *
   * *
  *   *
 *     *
*********
```

## Run

```bash
javac StarPatterns.java && printf '5\n' | java StarPatterns
```

```bash
javac PatternFormulas.java && java PatternFormulas
```

## Takeaway

Pattern problems look like loop problems and are really **algebra problems**:
write the spaces and stars for row `i` in terms of `i` and `n`, and the loops
write themselves.

Because the formula makes numeric claims — width `2n-1`, odd stars, total `n²` —
a pattern can be *verified* rather than eyeballed. That's more reliable than
squinting at output, and it caught the both-sides-padding subtlety that printed
output hides.
