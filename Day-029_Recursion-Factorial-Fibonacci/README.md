# Day 29 — Factorial & Fibonacci by Recursion

**11 Sep 2026 (Fri) · Ch 11 — Recursion**

A recursive method is two things:

- **base case** — an input it can answer without recursing
- **recursive case** — a step that moves *strictly toward* the base case

Miss either and you get `StackOverflowError` rather than an infinite loop.
`factorial(n + 1)` has a base case and still dies, because it never approaches
it.

## Factorial — one call per level

```java
if (n <= 1) return 1;              // BASE CASE
return n * factorial(n - 1);       // RECURSIVE CASE
```

Nothing is computed on the way *down*. Five frames sit waiting, and the
multiplication happens on the way back up: `1, 2, 6, 24, 120`.

### Overflow arrives long before the stack does

| n | `int` result | `long` result | exact |
| --- | --- | --- | --- |
| 12 | 479001600 | 479001600 | 479001600 |
| **13** | **1932053504** ✗ | 6227020800 | 6227020800 |
| 20 | **−2102132736** ✗ | 2432902008176640000 | 2432902008176640000 |
| **21** | **−1195114496** ✗ | **−4249290049419214848** ✗ | 51090942171709440000 |

`int` holds up to **12!**, `long` up to **20!**. Both wrap silently (Day 4) —
21! even comes back *negative*. The recursion is perfectly correct; the **return
type** is what fails. `BigInteger` has no such limit.

### The stack is finite

```
plain recursion overflowed at depth ~47160
```

Tens of thousands of frames, not millions. **This number moves between runs** —
four consecutive runs gave 42,598 / 45,202 / 45,920 / 57,985 — since it depends
on the JVM, frame size and `-Xss`. Read it as an order of magnitude.

### Java does not optimise tail calls

A **tail call** is one where the recursive call is the last thing the method
does, so nothing is pending on return:

```java
return factorialTail(n - 1, n * accumulator);   // tail position
```

Scheme or Scala compile that into a jump and run in constant stack. Java still
pushes a frame:

```
plain recursion  overflowed at depth ~47160
TAIL recursion   overflowed at depth ~24802
```

Note it dies **earlier**, not at the same depth — the frame is bigger, holding
two parameters instead of one. Tail position changed nothing; only frame size
moved. (Both figures drift between runs, but the ordering held in every run:
the tail version always died first.)

**Practical consequence:** rewriting a recursion into tail form buys you nothing
in Java. If depth is the problem, convert it to a loop.

## Fibonacci — two calls per level, and that changes everything

```java
return fib(n - 1) + fib(n - 2);    // TWO recursive calls
```

| n | fib(n) | calls | time |
| --- | --- | --- | --- |
| 30 | 832040 | 2,692,537 | 4 ms |
| 35 | 9227465 | 29,860,703 | 53 ms |
| **40** | 102334155 | **331,160,281** | **601 ms** |

Each `+5` on `n` multiplies the work by about 11. `fib(50)` would take roughly a
minute; `fib(60)` about two hours.

### The call count has a closed form

```
calls(n) == 2 * fib(n+1) - 1
checked n = 0..30, mismatches: 0
```

So computing `fib(n)` costs about `2·fib(n)` calls — **the function is its own
cost model.** That's the cleanest way to see why it explodes: `fib` grows
exponentially, so the work to find it does too.

### Where the work goes

```
                   fib(5)
              /              \
          fib(4)            fib(3)      ← recomputed
        /       \          /      \
    fib(3)    fib(2)   fib(2)   fib(1)
    /    \
fib(2)  fib(1)
```

`fib(3)` twice, `fib(2)` three times, compounding downward. There are only **n
distinct subproblems** — the naive version solves them exponentially many times.

### Memoisation

```
n=40  calls=79    57 us
n=90  calls=179   67 us
```

**331,160,281 calls → 79.** Same recursion, same base case, one `HashMap`. Each
`n` is computed once and reused, so the count becomes linear (~2n: one miss and
one hit per level).

This is dynamic programming in its smallest form — recursion plus a memory of
what you already solved.

### Iterative

```java
long previous = 0, current = 1;
for (int i = 0; i < n; i++) { long next = previous + current; previous = current; current = next; }
```

O(n) time, O(1) space, no stack risk, no cache.

## Comparison

| approach | time | space | usable to |
| --- | --- | --- | --- |
| naive | O(2ⁿ) | O(n) stack | n ≈ 40 |
| memoised | O(n) | O(n) + stack | n ≈ 92 |
| iterative | O(n) | **O(1)** | n ≈ 92 |

All three stop at **92** for the same reason — `long` overflow, not algorithmic
cost:

```
fib(92) = 7540113804746346429
fib(93) = -6246583658587674878   ← overflowed
```

## Run

```bash
javac Factorial.java && java Factorial
```

```bash
javac Fibonacci.java && java Fibonacci
```

## Takeaway

The lesson is **not** "recursion is slow". Recursion is fine — **recomputing**
is slow. The naive Fibonacci's flaw is that it forgets, and memoisation fixes it
without changing the shape of the code at all.

For factorial, recursion recurses once and costs O(n) — the same as a loop, but
with a stack limit thrown in. Recursion earns its keep when the problem is
genuinely branching (trees, parsing, backtracking), not when it's a countdown.

And in Java the stack limit is real and unavoidable: tail position doesn't help,
because tail calls aren't eliminated.
