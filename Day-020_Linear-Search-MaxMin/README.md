# Day 20 — Linear Search & Finding Max/Min

**02 Sep 2026 (Wed) · Ch 8 — Arrays**

Both algorithms are three lines. All the difficulty is at the **edges**: what to
return when there's no answer, and what to start from.

## Linear search

```java
for (int i = 0; i < array.length; i++) {
    if (array[i] == target) return i;
}
return -1;
```

`return` doubles as the break (Day 16) — once found, the rest is never examined.

| Variant | How |
| --- | --- |
| first occurrence | scan forward, return on match |
| last occurrence | scan **backward** — one pass, not two |
| contains | `indexOf(...) != -1` |
| count | cannot stop early; every element must be seen |

### Why `-1` for "not found"

| Sentinel | Problem |
| --- | --- |
| `0` | a **valid index** — indistinguishable from a hit |
| `false` | loses the position when you needed it |
| throw | makes absence an error; usually it isn't |

`-1` is never a valid index, so it can't be mistaken for a result.
`String.indexOf` and `List.indexOf` both use it.

The cost is that callers must check. Java helps a little — `if (indexOf(a, t))`
doesn't compile (Day 10) — but `array[indexOf(a, t)]` compiles fine and throws
on `-1`.

### Searching objects needs `equals`

```java
String needle = new String("bob");
names[i] == needle        // -1 — never matches, though "bob" is present
names[i].equals(needle)   //  1 — correct
```

`==` compares references (Day 8, Day 19). Prefer `Objects.equals(a, b)`, which
also tolerates `null` on either side.

## `Arrays.binarySearch` requires sorted input

Much faster, but its contract is a precondition — and breaking it doesn't throw:

```
unsorted [5, 3, 9, 1, 7]

  target     binarySearch       true index
       9                2                2
       1               -1                3      ← WRONG
       5                0                0
```

**Searching for 1 returns `-1`, yet 1 is present at index 3.** No exception,
just a plausible answer that happens to be false. Two of the three lookups were
right by luck, which is what makes this hard to catch in testing.

On genuinely sorted input a negative result is *useful* — it encodes the
insertion point as `-(insertion point) - 1`:

```java
Arrays.binarySearch(sorted, 4)   // -3  →  would insert at index 2
```

## Finding max and min

The algorithm is trivial. **The bug is in the first line.**

```
data [-5, -2, -9, -1]   (all negative)

int max = 0                 ->  0    ← WRONG: 0 is not in the array
int max = Integer.MIN_VALUE -> -1
int max = a[0]              -> -1
```

`int max = 0` silently assumes a non-negative element exists. When it doesn't,
the function returns a value **that is not a member of its own input** — which
is a far worse failure than being merely inaccurate.

Starting from `a[0]` makes that impossible by construction: the answer is always
an element, because it started as one.

### The empty array

`MIN_VALUE` and `a[0]` disagree, and the disagreement is the point:

```
MIN_VALUE init -> -2147483648   ← silent, and looks like data
a[0] init      -> ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
```

There **is** no maximum of an empty set, so returning a number is a lie. Options,
best first:

1. `OptionalInt` — the absence is in the type
2. `throw IllegalArgumentException` with a clear message
3. return `MIN_VALUE` — indistinguishable from real data

```java
Arrays.stream(empty).max()   // OptionalInt.empty
Arrays.stream(data).max()    // OptionalInt[9]
```

### One pass for both extremes

```java
int max = data[0], min = data[0];
for (int i = 1; i < data.length; i++) {
    if (data[i] > max)      { max = data[i]; maxAt = i; }
    else if (data[i] < min) { min = data[i]; minAt = i; }
}
```

`else if` is safe: a value cannot beat both the running max and the running min
in the same iteration. Note `>` vs `>=` decides whether you get the **first** or
**last** occurrence of a repeated extreme — pick deliberately.

### `NaN` in a `double[]`

```
data [3.0, NaN, 9.0, 1.0]

manual  `if (v > max)` -> 9.0   ← NaN silently ignored
Math.max in the loop   -> NaN   ← NaN propagates, as documented
```

NaN loses every comparison (Day 8), so it's never selected — the loop reports
the largest of the others with no hint the data was contaminated. Day 11's
failure, now spread across an array. If `NaN` means "missing", **filter it
explicitly** rather than relying on comparisons to drop it.

## Verification

`indexOf` is small enough to check exhaustively — array lengths 0–6 (including
empty) against targets −1 to 3, with deliberate duplicates:

```
35 cases, failures: 0
first, last, count and contains all agree with a brute-force scan.
```

## Run

```bash
javac LinearSearch.java && printf '30\n' | java LinearSearch
```

```bash
javac MinMax.java && java MinMax
```

## Takeaway

Both algorithms fail at their edges, not in their loops.

Search needs a sentinel that **can't be confused with a result** — hence `-1`,
and hence the danger of `binarySearch` on unsorted data, which returns a real
index-shaped value that means nothing.

Max/min needs a seed that is **already an element**. `int max = 0` is the one
bug worth memorising: on all-negative data it returns a number that was never in
the array.
