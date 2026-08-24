# Day 11 — Nested if: Largest of Three Numbers

**24 Aug 2026 (Mon) · Ch 5 — Conditionals**

## The problem

Given three numbers, return the largest. The classic use of a nested `if`, and a
good excuse to ask a harder question than "does it work": **which version is
hardest to get wrong?**

## Approach 1 — nested if

```java
if (a >= b) {
    if (a >= c) return a;      // a beat b and c
    else        return c;      // a beat b, but c beat a
} else {
    if (b >= c) return b;      // b beat a and c
    else        return c;      // b beat a, but c beat b
}
```

The outer test picks the larger of `a` and `b`; the inner test compares that
winner against `c`. **Only two comparisons ever run**, regardless of input.

### Why `>=` and not `>`

With a tie, either equal value is a correct answer, so `>` also works. `>=`
states the intent more honestly — "a is at least as large as b" — and keeps the
four branches symmetric. Consistency matters more than the choice itself.

## Approach 2 — else-if ladder

```java
if (a >= b && a >= c)      return a;
else if (b >= a && b >= c) return b;
else                       return c;
```

Up to four comparisons instead of two. In exchange, **each branch is a complete
claim** you can check on its own, rather than a path whose meaning depends on
which tests already failed.

## Approach 3 — Math.max

```java
return Math.max(a, Math.max(b, c));
```

The library already solves the two-value case. Clearest of the four, and — as
below — the only one that is correct for `double`.

## Approach 4 — ternary

```java
return a >= b ? (a >= c ? a : c)
              : (b >= c ? b : c);
```

Structurally identical to the nested `if`, in one expression. Compact, but
nesting hurts readability quickly (Day 8).

## Verifying, rather than assuming

Three hand-picked test values prove nothing. For a small range every
combination can simply be enumerated:

```
range [-4..4], 729 combinations tested
mismatches: 0
all four approaches agree on every case.
```

Each result is checked against an **independent** reference — sort the three
values and take the last — so a shared bug in the four implementations can't
hide.

Tie and negative cases, all agreeing:

```
(  5,  5,  3) -> 5      (  7,  7,  7) -> 7
(  5,  3,  5) -> 5      ( -2, -9, -4) -> -2
(  3,  5,  5) -> 5
```

## Where it breaks: doubles

For `int` all four agree. For `double` they do not.

```
(NaN, 5, 3)   nested = 5.0    Math.max = NaN
(5, NaN, 3)   nested = 3.0    Math.max = NaN
```

**The second line is the important one.** The nested `if` returns `3.0` — it
skipped the `5` entirely, returning neither the largest value nor the `NaN`.

The trace, using Day 8's rule that *every* comparison involving `NaN` is false:

```
a >= b   ->   5 >= NaN   ->   false   -> take the else branch
                                          (i.e. "b must be bigger")
b >= c   ->   NaN >= 3   ->   false   -> return c = 3
```

Both tests fail, and each failure is read as evidence for the *other* operand.
The result is a value that is neither the maximum nor obviously wrong — the
worst kind of bug, because nothing throws.

`Math.max` propagates `NaN` instead, which is the documented IEEE-754 behaviour:
an unknown input makes the maximum unknown. Loud beats silent.

### Negative zero too

```
(-0.0, 0.0, -1)   nested = -0.0    Math.max = 0.0
```

`-0.0 >= 0.0` is `true`, so the nested `if` keeps `-0.0`. `Math.max` documents
`0.0` as strictly greater. Both defensible; they simply differ (Day 8).

## Which to use

| Approach | Comparisons | Reads well? | Safe for `double`? |
| --- | --- | --- | --- |
| nested if | 2 | must trace each path | **no** — NaN breaks it |
| else-if ladder | up to 4 | each branch self-contained | no |
| `Math.max` | 2 | best | **yes** |
| ternary | 2 | poor once nested | no |

For `int`, pick on readability — `Math.max` wins. For `double`, hand-rolled
comparisons are correct **only if `NaN` cannot occur**, and proving that is
usually harder than just calling `Math.max`.

## Run

```bash
javac LargestOfThree.java && printf '12 45 23\n' | java LargestOfThree
```

```bash
javac LargestVerifier.java && java LargestVerifier
```

## Takeaway

The nested `if` is the fewest comparisons and the most opportunities to be
wrong, because each branch means something only in light of the tests that
already failed.

That structure is exactly what `NaN` exploits: when a comparison is false for a
reason other than the obvious one, "the test failed, so the other operand must
be bigger" stops being valid — and the nested `if` returns a value that was
never the largest.

Exhausting a small range turns "it works on my examples" into an actual proof,
and cost about ten lines.
