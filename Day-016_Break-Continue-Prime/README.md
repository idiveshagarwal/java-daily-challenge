# Day 16 — break and continue: Prime Number Check

**29 Aug 2026 (Sat) · Ch 6 — Loops**

## The two keywords

| | Does |
| --- | --- |
| `break` | leaves the loop entirely; execution resumes after it |
| `continue` | abandons **this** iteration and moves to the next |

Both act on the **innermost enclosing loop only**.

## The trap: `continue` means something different in a while loop

"The next iteration" is not the same thing in both loops:

```
for   : continue -> jumps to the UPDATE    (i++ still runs)
while : continue -> jumps to the CONDITION (i++ is skipped)
```

So this never terminates:

```java
int i = 1;
while (i <= 5) {
    if (i % 2 == 0) continue;   // i++ below is never reached
    i++;
}
```

Verified with a cap: `i` advances 1 → 2 and then **sticks forever**, because
every later pass hits the `continue` before reaching `i++`. Aborted after 1000
spins.

The `for` equivalent is fine — the update lives in the header where `continue`
can't skip it. **Fix:** increment before the `continue`, or use a `for` loop.

## The exercise: prime check

`break` is the textbook fit — once a divisor is found there's nothing left to
learn:

```java
for (int i = 2; i < n; i++) {
    if (n % i == 0) return false;   // return IS a break here
}
return true;
```

### But measure before believing

Iterations to decide **999983** (prime) and **999984** (composite):

| Approach | 999983 (prime) | 999984 (composite) |
| --- | --- | --- |
| no break, `i < n` | 999,981 | 999,982 |
| **break**, `i < n` | **999,981** | **1** |
| `i <= n/2` | 499,990 | — |
| `i*i <= n`, odds only | **499** | — |

Two separate lessons fall out of that table:

- **`break` collapses the composite case to nothing** — 999,982 iterations down
  to 1 — and does **absolutely nothing for a prime**. There is no divisor to
  break on, so the loop runs to the bound either way.
- **Only the bound helps the worst case.** `i*i <= n` with odd divisors takes
  499 iterations where the naive loop takes 999,981 — roughly 2000× fewer.

They optimise *different inputs*. Testing only on a composite would make `break`
look like the whole answer.

### Why `√n` is enough

If `n = a × b` then one of `a`, `b` is `≤ √n`. Checking past the square root
can never find a divisor you haven't already seen as the other factor.

```java
for (int i = 3; (long) i * i <= n; i += 2)
```

Written as `i * i <= n`, not `i <= Math.sqrt(n)`:

- stays in **integer arithmetic**, so there's no floating-point rounding at the
  boundary (Day 8)
- the `(long)` cast stops `i * i` overflowing for large `n` (Day 13)
- `i += 2` skips evens, halving the work again — after handling 2 separately

## `continue` as a filter

```java
for (int c = 2; c < limit; c++) {
    if (!isPrime(c)) continue;
    System.out.print(c + " ");
}
```

Equivalent to wrapping the body in `if (isPrime(c))`. `continue` reads better
when the body is long or there are several skip conditions — it keeps the
guards flat instead of nesting them.

## Labelled break and continue

A label lets you target an **outer** loop. This is the closest thing Java has to
`goto`, and it's restricted to loops.

```java
search:
for (int row = 0; row < grid.length; row++) {
    for (int col = 0; col < grid[row].length; col++) {
        if (grid[row][col] < 0) break search;    // leaves BOTH loops
    }
}
```

`continue rows;` likewise abandons the current row and starts the next. Without
labels you'd need a flag checked by the outer condition — more code, easily got
wrong.

## `break` inside a switch is not the same `break`

```java
for (int i = 1; i <= 3; i++) {
    switch (i) {
        case 2: break;      // ends the SWITCH, not the for loop
    }
}
```

Verified: the loop ran all **3** times. Inside a `switch`, `break` ends the
switch (Day 12). To leave the loop as well you need a label — or the arrow form,
which needs no `break` at all.

## Verifying correctness

Three examples prove nothing (Day 11). `isPrime` is checked against a **Sieve of
Eratosthenes** — a completely different algorithm, so agreement is real evidence
rather than a shared bug:

```
checked 0..10000, found 1229 primes
mismatches: 0
```

1229 is the known count of primes below 10,000. Edge cases handled: `n < 2`
(including negatives and 0, 1) is not prime, and 2 is the only even prime.

## Run

```bash
javac PrimeCheck.java && printf '999983\n' | java PrimeCheck
```

```bash
javac BreakAndContinue.java && java BreakAndContinue
```

## Takeaway

`break` and `continue` are control flow, but the day's real lesson is
measurement: `break` gives a ~1,000,000× speedup on composites and **zero** on
primes, while the loop bound gives ~2000× on the case `break` can't touch.
Which optimisation matters depends entirely on which input you're worried about.

And `continue` is genuinely dangerous in a `while` loop — it skips the
increment, which is the one line keeping the loop finite.
