# Day 21 — Bubble Sort

**03 Sep 2026 (Thu) · Ch 8 — Arrays**

## The idea

Compare adjacent pairs, swap when out of order. After one full pass the largest
value has "bubbled" to the end:

```
start [5, 1, 4, 2, 8]
  a[0]=5 vs a[1]=1 -> swap  [1, 5, 4, 2, 8]
  a[1]=5 vs a[2]=4 -> swap  [1, 4, 5, 2, 8]
  a[2]=5 vs a[3]=2 -> swap  [1, 4, 2, 5, 8]
  a[3]=5 vs a[4]=8 -> keep  [1, 4, 2, 5, 8]
```

Each pass fixes one more element at the back, so the sorted region grows from
the right.

## Three versions, one line apart

```java
for (int pass = 0; pass < a.length - 1; pass++) {
    boolean swapped = false;
    for (int j = 0; j < a.length - 1 - pass; j++) {   // ← shrinking bound
        if (a[j] > a[j + 1]) { swap(a, j, j + 1); swapped = true; }
    }
    if (!swapped) return;                             // ← early exit
}
```

## What each optimisation actually buys

Measured at `n = 10`:

| input | variant | comparisons | swaps | passes |
| --- | --- | --- | --- | --- |
| sorted | naive | 81 | 0 | 9 |
| sorted | shrinking | 45 | 0 | 9 |
| **sorted** | **earlyExit** | **9** | **0** | **1** |
| reverse | naive | 81 | 45 | 9 |
| reverse | shrinking | 45 | 45 | 9 |
| reverse | earlyExit | 45 | 45 | 9 |
| random | naive | 81 | 26 | 9 |
| random | shrinking | 45 | 26 | 9 |
| random | earlyExit | 45 | 26 | 9 |

- **naive**: `(n-1)²` = 81 comparisons, always
- **shrinking**: `(n-1)n/2` = 45 comparisons, always — a flat halving
- **earlyExit**: 9 comparisons in **one pass** on sorted input

The early exit changes the **best case from quadratic to linear** and changes
nothing else — on reverse or random input it costs one boolean and saves
nothing. That's the whole reason bubble sort is taught: it's the simplest sort
that's *adaptive*.

### Why the tail can be skipped

```
start          [5, 1, 4, 2, 8]
after pass 1   [1, 4, 2, 5, 8]   last 1 element final
after pass 2   [1, 2, 4, 5, 8]   last 2 elements final
```

Not a guess — each pass carries the largest *remaining* value all the way
right, so after pass `p` the last `p` elements are provably in place. Rescanning
them cannot find a swap.

## Swaps equal inversions

Notice the swap column above: **identical across all three variants**. That's
not a coincidence.

An *inversion* is any pair out of order, at any distance. A bubble-sort swap
only ever exchanges an out-of-order **adjacent** pair, and doing so removes
exactly **one** inversion.

```
sample [5, 2, 9, 1, 7, 3, 8, 6, 4, 0]
inversions = 26, and every variant swapped 26 times
```

Verified on 500 random arrays (with duplicates): **zero mismatches**.

So the swap count is a property of the **data**, not the algorithm variant.
Optimisations can save comparisons — they can never save swaps.

## Stability is one character

Bubble sort is stable, but only because the comparison is **strict**:

```
input      [3a, 1b, 3c, 1d, 2e]
using >    [1b, 1d, 2e, 3a, 3c]   ← 1b before 1d, 3a before 3c: STABLE
using >=   [1b, 1d, 2e, 3c, 3a]   ← equal keys reordered: UNSTABLE
```

`>=` swaps equal elements, which serves no purpose: it costs a swap *and*
destroys any ordering a previous sort established.

Stability matters when sorting by one field after another — sort by name, then
by department, and a stable sort keeps names alphabetical *within* each
department.

## Complexity

| Case | Comparisons | Swaps |
| --- | --- | --- |
| best (sorted, with early exit) | **O(n)** | 0 |
| average | O(n²) | O(n²) |
| worst (reversed) | O(n²) | `n(n-1)/2` |

Space is O(1) — it sorts in place. In practice the JDK uses dual-pivot quicksort
for primitives and Timsort for objects; bubble sort is a teaching algorithm, not
a production one.

## Verification

Checked against `Arrays.sort` on **2000 random arrays**, lengths 0–14
(including empty), values −25 to 24:

```
failures: 0
bubble sort agrees with the JDK on every case, empty included.
```

Including length 0 matters — `a.length - 1` is `-1` for an empty array, so the
outer loop must not execute. It doesn't, because `0 < -1` is false.

## Run

```bash
javac BubbleSort.java && java BubbleSort
```

```bash
javac SortAnalysis.java && java SortAnalysis
```

## Takeaway

The shrinking bound halves the comparisons on every input; the early exit does
nothing on most inputs and makes the sorted case linear. Two optimisations,
completely different beneficiaries — the same lesson as Day 16, where `break`
helped composites and the `√n` bound helped primes.

Swaps are fixed by the data (one per inversion), so no amount of cleverness in
the loop structure reduces them. And stability comes down to a single `>` — the
kind of detail that's invisible until you sort by two fields.
