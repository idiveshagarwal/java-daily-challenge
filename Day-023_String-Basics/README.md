# Day 23 — String Basics & Common Methods

**05 Sep 2026 (Sat) · Ch 9 — Strings**

Day 6 covered the string pool and why `==` is not `.equals()`. This day is the
methods — and the one property that explains all of them.

## Strings are immutable

Nothing can change the characters of an existing `String`. Every method that
looks like it edits one **returns a new String** and leaves the original alone.

```java
String s = "  hello  ";
s.trim();           // computes a trimmed String, throws it away
s.toUpperCase();    // likewise
s.concat(" world"); // likewise
// s is still "  hello  "
```

That's the most common String bug there is: **calling a method and discarding
the result**. Assign it:

```java
String fixed = s.trim().toUpperCase();   // "HELLO"
```

## The common methods

| Category | Methods |
| --- | --- |
| inspect | `length()` · `charAt(i)` · `isEmpty()` · `isBlank()` |
| search | `indexOf` · `lastIndexOf` · `contains` · `startsWith` · `endsWith` |
| extract | `substring(a)` · `substring(a, b)` |
| transform | `trim` · `strip` · `toUpperCase` · `replace` · `repeat` |
| compare | `equals` · `equalsIgnoreCase` · `compareTo` |
| convert | `split` · `String.join` · `String.valueOf` |

`length()` is a **method** here; arrays use the **field** `.length` (Day 19).
Both mistakes are compile errors.

`"   ".isEmpty()` is **false** — spaces are characters. `isBlank()` (Java 11+)
is the whitespace-only test.

### substring: the end index is exclusive

```java
"programming".substring(0, 7)   // "program"
"programming".substring(3, 3)   // ""  — empty, not an error
"programming".substring(11)     // ""  — also legal
"programming".substring(5, 2)   // Range [5, 2) out of bounds for length 11
```

`substring(a, b)` has length `b - a`. That subtraction is the easiest way to
remember which index is excluded — same convention as `Arrays.copyOfRange`
(Day 19).

### compareTo returns a difference, not a sign

```java
"a".compareTo("b")            // -1     one apart
"a".compareTo("z")            // -25    25 apart
"apple".compareTo("apricot")  // -2     'p' - 'r' at the first difference
"abc".compareTo("ab")         //  1     no difference found -> length difference
```

Only the **sign** is contractual. Test `< 0`, `> 0`, `== 0` — never `== -1`.

## Four traps

### 1. `split` takes a regular expression

The characters you most want to split on are all regex metacharacters.

```
"a.b.c".split(".")      ->  []          length 0
"a.b.c".split("\\.")    ->  [a, b, c]   correct
"a|b".split("|")        ->  [a, |, b]
```

`.` matches **any** character, so every character is a separator, every field is
empty, and then the trailing empties are dropped (see trap 2) — leaving
**nothing at all**. `|` is alternation between two empty patterns, matching at
every position.

Fix: escape it, or use `Pattern.quote(".")`. Note `replace` takes a **literal**
while `replaceAll` takes a **regex**.

### 2. Trailing empty fields vanish

```
"a,b,,c,,".split(",")      ->  [a, b, , c]      length 4
"a,b,,c,,".split(",", -1)  ->  [a, b, , c, , ]  length 6
```

Two fields disappeared. A CSV row with empty trailing columns comes back
**short**, and the index you expect to hold column 6 is out of bounds.

It's asymmetric — **leading** empties are kept:

```
",,a".split(",")   ->  [, , a]   length 3
```

Pass `limit = -1` whenever the field count matters.

### 3. `trim()` is not `strip()`

`trim()` predates Unicode awareness: it removes characters `<= U+0020`.
`strip()` (Java 11+) uses `Character.isWhitespace`.

| Character | `isWhitespace` | `trim()` | `strip()` |
| --- | --- | --- | --- |
| EM SPACE `U+2003` | **true** | not removed | **removed** |
| NBSP `U+00A0` | **false** | not removed | **not removed** |

The second row is the one worth knowing. `strip()` is *not* "removes anything
that looks blank" — it removes what `Character.isWhitespace` accepts, and that
**excludes non-breaking spaces by design**. NBSP pasted from a web page survives
both methods, which is why input that looks trimmed still fails an `isEmpty()`
check.

### 4. `+=` in a loop is quadratic

Because Strings are immutable, each `+=` allocates a new String and copies
everything so far.

```
       n         s += c  StringBuilder      ratio
   10000         7.8 ms       0.331 ms        24x
   20000        18.1 ms       0.391 ms        46x
   40000        85.8 ms       0.349 ms       246x
```

Doubling `n` roughly **quadruples** the concat time while the builder stays flat
— the signature of O(n²) versus O(n).

(Absolute times vary run to run; the ratios and the quadrupling are the stable
part. A second run gave 9.4 / 19.8 / 77.8 ms with the builder still under half a
millisecond throughout.)

Worth knowing why the compiler doesn't save you: javac *does* rewrite
`a + b + c` in a single expression into a `StringBuilder`. It cannot do that
across loop iterations, because each one is a separate statement.

## Run

```bash
javac StringBasics.java && java StringBasics
```

```bash
javac StringPitfalls.java && java StringPitfalls
```

## Takeaway

Immutability explains the whole class: methods return new Strings (so a
discarded result is a no-op), and `+=` in a loop is quadratic (so use
`StringBuilder`).

The two API traps are both about a method doing more than its name suggests —
`split` silently interprets a regex, and `strip` silently defers to
`Character.isWhitespace`, which rejects the non-breaking space that real-world
input is full of.
