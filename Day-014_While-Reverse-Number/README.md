# Day 14 — while Loop: Reverse a Number

**27 Aug 2026 (Thu) · Ch 6 — Loops**

## Why this exercise is a `while`

The iteration count **isn't known when the loop starts** — it depends on how
many digits the input has. That's exactly the case a `for` loop fits badly:

```java
for (; n != 0; n /= 10)      // legal, but the empty init reads as an apology
```

Rule of thumb:

| Loop | Use when |
| --- | --- |
| `for` | the iteration count is known up front — counting |
| `while` | you loop until a condition changes — draining, searching |
| `do-while` | the body must run at least once — prompt-then-validate |

## The algorithm

```java
int rev = 0;
while (n != 0) {
    rev = rev * 10 + n % 10;   // shift rev left, append n's last digit
    n /= 10;                   // drop that digit from n
}
```

`% 10` takes the last digit, `/ 10` removes it — the second line works because
integer division truncates (Day 4).

Traced on 12345:

```
           n       n % 10        rev
       12345            5          5
        1234            4         54
         123            3        543
          12            2       5432
           1            1      54321
           0            -      54321   <- n hit 0, loop ends
```

## The condition must be `n != 0`, not `n > 0`

Most tutorials write `while (n > 0)`. Compare:

| input | `n > 0` | `n != 0` |
| --- | --- | --- |
| 123 | 321 | 321 |
| **-123** | **0** | **-321** |
| 0 | 0 | 0 |
| 1200 | 21 | 21 |

With `n > 0` and a negative input the condition is false immediately, the loop
never runs, and the method returns the initial `rev = 0` — no error, just a
wrong answer.

### Why negatives work at all

Because in Java **`%` takes the sign of the dividend** (Day 7):

```
-123 % 10 = -3      not 7
-123 / 10 = -12     truncates toward zero
```

Every extracted digit is negative, `rev` accumulates negatively, and the sign
survives for free. Worth noticing: this code is correct **by virtue of a
language rule**, not by design. In a language where `%` follows the divisor, the
identical algorithm would produce nonsense.

## Trailing zeros don't round-trip

```
1200 -> 21
 120 -> 21
 100 -> 1
```

Not a bug — leading zeros can't be represented in an `int`. But it means
`reverse(reverse(n)) != n` whenever `n` ends in 0. Return a `String` if the
digits must round-trip.

## Silent overflow — the serious one

A 10-digit `int` reversed usually **doesn't fit in an `int`**:

| input | naive `int` | correct (`long`) | `exact()` |
| --- | --- | --- | --- |
| 1534236469 | `1056389759` | 9646324351 | OVERFLOW |
| 2147483647 | `-1126087180` | 7463847412 | OVERFLOW |

`1534236469` reversed is `9646324351`, which needs 34 bits. The `int` version
returns `1056389759` — **no exception, no warning**, just a plausible-looking
wrong answer. The second row is worse: reversing a positive number returns a
negative one.

Two fixes:

```java
long rev = 0;                                        // 1. always fits
rev = Math.addExact(Math.multiplyExact(rev, 10), d); // 2. throws on overflow
```

`Math.multiplyExact` / `addExact` turn a silent wrong answer into a loud one —
the same argument as Day 11's `Math.max` and `NaN`.

## while vs do-while

The only difference is **when the condition is first tested**:

```java
while (cond) { ... }      // tests FIRST  -> can run zero times
do { ... } while (cond);  // tests LAST   -> always runs at least once
```

Verified: `while` ran the body **0** times, `do-while` ran it **1** time, on the
same false condition. `do-while` fits prompt-then-validate — you must read input
once before you can judge whether to ask again (Day 9).

### A literal `false` is treated inconsistently

Discovered while writing the demo above, which initially wouldn't compile:

| Written | Result |
| --- | --- |
| `while (false) { }` | **error: unreachable statement** |
| `for (;false;) { }` | **error: unreachable statement** |
| `if (false) { }` | compiles |
| `do { } while (false);` | compiles |

The `if` exemption is **deliberate**: it enables the conditional-compilation
idiom, where `static final boolean DEBUG = false` lets javac strip a whole block
rather than reject it as dead code.

`do-while` is fine because its body always runs once — nothing is unreachable.
`while` and `for` with a constant `false` genuinely can't reach their bodies, so
javac refuses.

## `while (true)` + break

```java
while (true) {
    value *= 2;
    if (value > 1000) break;
}
```

Equivalent to `for (;;)` (Day 13). Use it when the exit condition is only
computable partway through the body; otherwise put the condition in the header
where a reader can see it.

## Run

```bash
javac ReverseNumber.java && printf '12345\n' | java ReverseNumber
```

```bash
javac ReverseEdgeCases.java && java ReverseEdgeCases
```

```bash
javac WhileLoopForms.java && java WhileLoopForms
```

Try `1534236469` as input to see the overflow warning fire.

## Takeaway

The three-line reverse is correct for the case everyone tests and wrong for
three they don't: negatives (fixed by `!= 0` instead of `> 0`), trailing zeros
(unfixable — the information is gone), and overflow (fixed by a `long` or by
`Math.*Exact`).

Only one of those three produces any visible signal. The other two return
numbers that look entirely reasonable.
