# Day 30 — Tower of Hanoi (Recursion Wrap-Up)

**12 Sep 2026 (Sat) · Ch 11 — Recursion**

Three pegs, n disks stacked largest-first on A. Move them all to C, one at a
time, never placing a larger disk on a smaller one.

## The solution is a restatement of the problem

```java
static void solve(int n, char from, char to, char via) {
    if (n == 0) return;
    solve(n - 1, from, via, to);      // clear the way
    System.out.println("move disk " + n + ": " + from + " -> " + to);
    solve(n - 1, via, to, from);      // bring them back on top
}
```

> to move n disks from A to C using B:
> move n−1 from A to B, move disk n to C, move n−1 from B to C

The argument shuffle is the whole trick: the peg that was the **destination**
becomes the **spare**, and vice versa. Base case `n == 0` rather than `n == 1`
is tidier — nothing to do, no special case.

This is the best argument in the course for recursion existing. The iterative
solution is genuinely hard to derive; this one is three lines.

## Verifying the moves are legal

Printing plausible-looking moves proves nothing, so the moves are replayed
against three real stacks with the rule checked on every step:

```
disks              : 3
moves made         : 7
expected 2^n - 1   : 7
illegal placements : 0
max recursion depth: 3
final state        : A=[] B=[] C=[1, 2, 3]
```

Checked for n = 1..20: move count always exactly `2ⁿ − 1`, zero illegal
placements, puzzle always solved.

## The recurrence, and why it's optimal

```
T(0) = 0
T(n) = T(n-1) + 1 + T(n-1) = 2·T(n-1) + 1   →   T(n) = 2ⁿ − 1
```

This is **optimal**, not merely what this algorithm happens to do. The largest
disk must move at least once; before it can, the other n−1 must all be on the
spare peg — which is the same problem again. So `T(n) ≥ 2·T(n−1) + 1` for *any*
correct solution.

The legend uses 64 disks: **18,446,744,073,709,551,615** moves — about 585
billion years at one per second.

## Depth and work are different quantities

| n | moves (2ⁿ − 1) | max depth |
| --- | --- | --- |
| 10 | 1,023 | 10 |
| 20 | 1,048,575 | 20 |
| 30 | 1,073,741,823 | 30 |

30 disks is a **billion moves but only 30 stack frames**. Hanoi will never throw
`StackOverflowError` for any n you could wait for — it simply won't finish.

**Depth bounds the memory; the call count bounds the time.** Factorial hid this
(both are n); Hanoi separates them cleanly.

## The wrap-up question: why can't memoisation fix this?

Day 29 showed naive Fibonacci making 2ⁿ calls for one number, and memoisation
collapsing it to linear. Hanoi is also 2ⁿ. Same fix?

The tempting answer — *"Hanoi has no repeated subproblems"* — **is false**. I
measured it:

| problem | n | total calls | distinct states | repetition |
| --- | --- | --- | --- | --- |
| hanoi | 20 | 1,048,575 | **57** | **18,396×** |
| fib | 20 | 21,891 | 21 | 1,042× |

Hanoi repeats an order of magnitude **more** than Fibonacci. The repetition is
absolutely there. Caching still can't help.

### The real reason

| | returns | size of answer | the 2ⁿ work is… |
| --- | --- | --- | --- |
| `fib(n)` | one number | O(1) | **waste** — removable |
| `hanoi(n)` | a sequence of moves | **2ⁿ − 1 moves** | **the answer** — not removable |

`fib(40)` does 331 million calls to produce **one integer** — almost all of it
recomputation, so a cache deletes it.

`hanoi(20)` does a million calls to produce **a million moves**. You *could*
cache "the move list for `hanoi(19, A→C)`" — but that list has 524,287 entries.
Storing it doesn't make emitting it cheaper.

**The output size is a lower bound on the running time.** No algorithm prints
2ⁿ − 1 moves in fewer than 2ⁿ − 1 steps.

### Counting is cheap; listing is not

```java
(1L << 60) - 1   // 1152921504606846975 — O(1), one shift and one subtraction
```

Same puzzle, same n. Asking *how many* is trivial; asking *which* is impossible.
**The exponential lives in the question, not in the algorithm.**

## Ch 11 wrap-up: when is recursion right?

| problem | recursion is | because |
| --- | --- | --- |
| factorial | unnecessary | one call per level — that's a loop |
| fibonacci | a trap | two calls, overlapping — memoise or iterate |
| **hanoi** | **the right tool** | two calls, genuinely branching |

The test isn't "can this be recursive" but **"does the problem branch?"** A
countdown doesn't; a tree does.

Three things to carry forward:

1. **Base case plus progress toward it**, or `StackOverflowError` (Day 29).
2. **Depth bounds memory; call count bounds time.** They're different, and
   Hanoi separates them cleanly.
3. **Memoisation removes repeated work, never a large answer.** When you meet an
   exponential, ask which one it is.

## Run

```bash
javac TowerOfHanoi.java && printf '3\n' | java TowerOfHanoi
```

```bash
javac HanoiAnalysis.java && java HanoiAnalysis
```

## Takeaway

Hanoi is the case recursion was made for: the code is shorter than the English
description of the algorithm, and the recurrence falls straight out of it.

Its exponential cost looks like Fibonacci's and is the opposite kind. Fibonacci
wastes 2ⁿ calls computing one number; Hanoi spends 2ⁿ steps producing 2ⁿ moves.
The first is a bug you can cache away. The second is the problem telling you how
big its answer is.
