# Day 22 — 2D Arrays: Matrix Addition

**04 Sep 2026 (Fri) · Ch 8 — Arrays**

## Java has no 2D array type

`int[][]` is **an array whose elements are `int[]` references** — an array of
arrays. Every surprise below follows from that, plus Day 19's rule that arrays
are objects held by reference.

```java
int[][] m = new int[3][4];

m.getClass()     // int[][]
m[0].getClass()  // int[]      ← each row is its own object
m.length         // 3          ← number of ROWS
m[0].length      // 4          ← columns of THAT row
```

There is no `m.length` for columns, because **columns aren't a property of the
outer array**.

## Matrix addition

```java
for (int i = 0; i < a.length; i++) {
    sum[i] = new int[a[i].length];
    for (int j = 0; j < a[i].length; j++) {
        sum[i][j] = a[i][j] + b[i][j];
    }
}
```

The arithmetic is trivial. The interesting part is that **"same dimensions" and
even "rectangular" are assumptions the code must check**:

```
2x2 + 2x3     -> IllegalArgumentException: row 0 length differs: 2 vs 3
jagged + rect -> IllegalArgumentException: row 1 length differs: 1 vs 2
```

Without the guard, the loops throw `ArrayIndexOutOfBoundsException` part-way
through — after already writing some of the result.

## Rows can be missing, and can differ

```java
new int[3][]     // [null, null, null]  ← nulls, not empty arrays
partial[0][0]    // NullPointerException
```

`new int[3][4]` is shorthand for allocating the outer array *and* all three
rows. The two-step form lets rows differ — or stay absent.

Jagged arrays are perfectly legal, and are the natural shape for triangular
data (Pascal's triangle from Day 18 wastes half a rectangle):

```
length 1  [1]
length 2  [2, 2]
length 3  [3, 3, 3]
length 4  [4, 4, 4, 4]
```

**Consequence:** always write the inner loop bound as `m[i].length`, never a
captured `cols`. The captured version is a latent
`ArrayIndexOutOfBoundsException`.

## Printing needs `deepToString`

```
Arrays.toString     [[I@8bcc55f, [I@58644d46]
Arrays.deepToString [[1, 2], [3, 4]]
```

`toString` prints each *row's* default `Object.toString` — type plus hashcode —
because the elements are references. Same reason `Arrays.equals` needs
`deepEquals` for 2D.

## `clone()` is shallow

The trap worth internalising:

```java
int[][] shallow = original.clone();
shallow[0][0] = 99;
```

```
original [[99, 2], [3, 4]]   ← CHANGED
shallow  [[99, 2], [3, 4]]
original[0] == shallow[0] : true
```

`clone()` duplicated the outer array **of references**. The rows were never
copied. A real copy clones each row:

```java
int[][] copy = new int[source.length][];
for (int i = 0; i < source.length; i++) copy[i] = source[i].clone();
```

The same aliasing can be created deliberately, and is easy to do by accident:

```java
int[] row = {0,0,0};
int[][] m = { row, row, row };   // three references, ONE array
m[0][0] = 7;                     // [[7,0,0], [7,0,0], [7,0,0]]
```

## `Arrays.fill` doesn't work on a 2D array

```java
int[][] m = new int[2][3];
Arrays.fill(m, 5);     // compiles — then throws
```

```
-> ArrayStoreException: java.lang.Integer
```

`int[][]` **is** an `Object[]`, so this binds to `fill(Object[], Object)` and
tries to store a boxed `Integer` where an `int[]` belongs. That's Day 19's
covariance hole, this time reached through the standard library — it type-checks
and fails at runtime.

Correct version:

```java
for (int[] r : m) Arrays.fill(r, 5);   // [[5,5,5], [5,5,5]]
```

## Traversal order

```
row-major (i outer): 1 2 3 4 5 6
col-major (j outer): 1 4 2 5 3 6
```

Row-major walks each row's memory in sequence, so it's cache-friendlier for
large matrices. Column-major also **breaks on jagged input**, since `m[0].length`
isn't every row's length.

## Run

```bash
javac MatrixAddition.java && printf '2 3\n1 2 3 4 5 6\n10 20 30 40 50 60\n' | java MatrixAddition
```

```bash
javac TwoDArrays.java && java TwoDArrays
```

## Takeaway

`int[][]` means "array of `int[]`", not "matrix". Rows are independent objects,
so lengths are per-row, rows can be `null`, and rows can be **shared**.

That single fact explains why `clone()` copies nothing useful, why printing
needs `deepToString`, why `Arrays.fill` throws instead of filling, and why every
inner loop should bound on `m[i].length`.

If your algorithm needs a rectangle, **check for one** — the type system won't.
