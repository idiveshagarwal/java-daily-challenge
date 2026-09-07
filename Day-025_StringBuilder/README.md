# Day 25 — StringBuilder & String Manipulation

**07 Sep 2026 (Mon) · Ch 9 — Strings**

`String` is immutable (Day 23), which makes repeated modification quadratic.
`StringBuilder` is the mutable counterpart — one growable char buffer, edited in
place, converted to a `String` once at the end.

Day 23 measured the difference (22× to 257×). This day is the class itself.

## The defining difference

```java
String s = "abc";
s.concat("def");            // discarded — s is still "abc"

StringBuilder sb = new StringBuilder("abc");
sb.append("def");           // the buffer itself changed — sb is "abcdef"
```

With `String` you must assign the result. With `StringBuilder` **the call is the
modification**.

## The editing surface

```
start                  [Hello World]
append("!")            [Hello World!]
insert(5, ",")         [Hello, World!]
replace(0, 5, "Howdy") [Howdy, World!]    end exclusive, like substring
deleteCharAt(last)     [Howdy, World]
delete(0, 6)           [ World]
setCharAt(0, 'w')      [wWorld]
reverse()              [dlroWw]           surrogate-safe (Day 24)
```

`insert`, `delete`, `replace` and `setCharAt` have **no String equivalent at
all** — String can only produce new strings.

## Chaining returns `this`

Every mutator returns the same object, which is what makes chaining work. It
also means assigning the result creates an **alias**, not a copy:

```java
StringBuilder second = first.append("y");
second.append("z");
// first == second  →  true.  Both read "xyz".
```

To branch, use `new StringBuilder(existing)`.

## Two append surprises

```java
sb.append('a' + 1)          // 98  — the int, not 'b'
sb.append((char)('a' + 1))  // b
```

`char` arithmetic promotes to `int` (Day 7), so the `int` overload wins.

```java
String s = null;
sb.append(s)                // [null]  — length 4
```

No `NullPointerException`. You get the four characters `n-u-l-l`, which is
rarely what the caller wanted.

## length vs capacity

A growable array has two sizes, and only one is the string's length:

| | length | capacity |
| --- | --- | --- |
| `new StringBuilder()` | 0 | **16** |
| `new StringBuilder("abc")` | 3 | **19** (16 + text) |
| `new StringBuilder(100)` | 0 | 100 |

### Growth is geometric

```
length   17  capacity   16 ->   34
length   35  capacity   34 ->   70
length   71  capacity   70 ->  142
length  143  capacity  142 ->  286
length  287  capacity  286 ->  574
```

Each step is **2n + 2**, so appending *n* characters costs O(log n)
reallocations rather than O(n). That's precisely why `append` is amortised O(1)
while `String +=` is quadratic.

### Presizing is measurable

Appending 2,000,000 characters:

```
default capacity         7.0 ms
new StringBuilder(n)     1.8 ms   (3.8x faster)
reallocations avoided: 17
```

Each reallocation allocates a larger array and copies everything across. **If
you know the size, say so.**

(Absolute times vary run to run — a second run gave 3.9×. The reallocation count
is exact.)

## The trap: `equals()` is not overridden

```java
StringBuilder a = new StringBuilder("hi");
StringBuilder b = new StringBuilder("hi");

a.equals(b)                        // false  ← identity, NOT content
a.compareTo(b)                     // 0      ← compareTo DOES compare content
a.toString().equals(b.toString())  // true   ← the correct test
```

This is **worse than String's `==` pitfall** (Day 6), because `equals()` is
exactly the method you're taught to reach for. Here it silently means reference
identity — and `compareTo` disagreeing with `equals` makes it stranger still.

**Consequence:** a `StringBuilder` is unusable as a `HashMap` key or in a `Set`
— every instance is distinct. Call `toString()` before storing or comparing.

## `setLength` truncates, pads, and never shrinks

```
setLength(5) on "hello world"  -> [hello]   capacity 27 -> 27  (unchanged)
setLength(0)                   -> length 0, capacity 27        (the reuse idiom)
setLength(5) on "ab"           -> chars: 97 98 0 0 0
```

Three things worth noting:

- Truncating **does not free memory** — the array stays the same size.
- `setLength(0)` is the reuse idiom: it empties the builder while keeping the
  buffer, so a builder reused across loop iterations stops reallocating.
- **Growing pads with the null character** (code 0), not spaces. Those pad
  characters are invisible when printed but count toward `length()`.

## StringBuilder vs StringBuffer

Identical APIs. `StringBuffer`'s methods are `synchronized`; `StringBuilder`'s
are not.

| | Since | Thread-safe | Speed |
| --- | --- | --- | --- |
| `StringBuffer` | Java 1.0 | yes | slower |
| `StringBuilder` | Java 5 | no | faster |

Use `StringBuilder`. A builder is almost always a local variable that never
escapes its method, so the locking protects nothing and costs on every call.

## Run

```bash
javac StringBuilderBasics.java && java StringBuilderBasics
```

```bash
javac BuilderInternals.java && java BuilderInternals
```

## Takeaway

`StringBuilder` is a growable array with a String-shaped API, and the two sizes
of a growable array — used versus allocated — explain both its performance
(2n+2 growth makes append amortised O(1)) and its tuning (presizing was 3.8×
faster over two million appends).

The one genuine trap is `equals()`: not overridden, so it compares references
while `compareTo` compares content. Convert to `String` before comparing or
storing.
