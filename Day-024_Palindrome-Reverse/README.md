# Day 24 — Palindrome & Reverse a String

**06 Sep 2026 (Sun) · Ch 9 — Strings**

Two classic exercises that are both really about the same question: **what
counts as a character?**

## Reversing

Four ways, identical on ASCII:

```java
new StringBuilder(s).reverse().toString();   // idiomatic
charArraySwap(s);                            // two pointers, O(n)
s += s.charAt(i);                            // quadratic (Day 23) — avoid
recursive(s.substring(1)) + s.charAt(0);     // O(n²) and stack-limited
```

That agreement does **not** survive contact with real text.

### Three answers to "how long is this string?"

| string | chars | codepoints | graphemes |
| --- | --- | --- | --- |
| `ab😀` | 4 | 3 | 3 |
| `café` (combining accent) | 5 | 5 | 4 |
| `a🇦🇧z` (flag) | 6 | 4 | 3 |

- **char** — a UTF-16 unit; astral characters take **two**
- **codepoint** — one Unicode value; a combining mark is its own
- **grapheme** — what a reader sees as one character

### What each reversal actually produces

| input | `char[]` swap | `StringBuilder` | by grapheme |
| --- | --- | --- | --- |
| `ab😀` | `??ba` ❌ | `😀ba` ✅ | `😀ba` ✅ |
| `café` | broken ❌ | broken ❌ | `éfac` ✅ |
| `a🇦🇧z` | broken ❌ | `z🇧🇦a` ❌ | `z🇦🇧a` ✅ |

Three separate findings:

1. **A hand-written `char` swap splits surrogate pairs**, leaving two halves
   that aren't characters — hence the replacement glyphs.
2. **`StringBuilder.reverse()` handles surrogate pairs correctly.** This is
   documented in its javadoc, and it's the reason the library version beats the
   "clever" manual one.
3. **`StringBuilder` still isn't right for user-visible text.** It moves a
   combining accent onto the wrong letter, and it reverses the two regional
   indicators of a flag — turning 🇦🇧 into 🇧🇦, *a different flag*.

Only a grapheme-cluster reversal (`BreakIterator.getCharacterInstance()`) is
correct for text a person will read.

Note the grapheme result is `e` + U+0301, not the precomposed `é` (U+00E9).
Reversing preserves the cluster; it does not normalise it. The two render
identically but are different character sequences — which is why comparing
such strings needs `java.text.Normalizer` first.

**Practical position:** `StringBuilder` is the expected answer to the exercise
and is fine for ASCII. Knowing *why* it isn't fully correct is the more useful
half.

## Palindrome

The obvious solution reverses and compares. The better one never builds a second
string:

```java
for (int i = 0, j = s.length() - 1; i < j; i++, j--) {
    if (s.charAt(i) != s.charAt(j)) return false;
}
return true;
```

| approach | time | space | early exit |
| --- | --- | --- | --- |
| reverse & compare | O(n) | **O(n)** | no — always full work |
| two pointers | O(n) | **O(1)** | **yes** |

On `"abcdef"` the two-pointer version compares **one pair** and returns;
reversing builds all six characters first.

Note the condition is `i < j`, not `i <= j` — on odd lengths the middle
character never needs comparing with itself.

### Normalisation is the real problem

```
input                              strict   normalised
[racecar]                            true         true
[RaceCar]                           false         true
[A man, a plan, a canal: Panama]    false         true
[No 'x' in Nixon]                   false         true
[hello]                             false        false
```

Only `racecar` passes strictly. Everything else needs case folding and
punctuation removal before the question even makes sense —
`"A man, a plan, a canal: Panama"` normalises to `amanaplanacanalpanama`.

`Character.isLetterOrDigit` is Unicode-aware, so accented letters survive
normalisation rather than being stripped as punctuation.

### Edge cases

```
""     -> true    vacuously: no pair differs
"a"    -> true    single character
",,,"  -> true    normalises to empty
```

The empty string being a palindrome falls straight out of the loop condition —
worth checking rather than special-casing.

## Verification

Three implementations (two-pointer, reverse-and-compare, normalise-in-place)
checked against each other on **5000 random strings** over a two-letter
alphabet, lengths 0–9:

```
1988 were palindromes, so both branches were exercised
mismatches: 0
```

The tiny alphabet is deliberate — random strings over `{a,b}` produce real
palindromes by chance about 40% of the time, so the `true` branch gets tested
too. Over a 26-letter alphabet almost every case would be `false`.

## Run

```bash
javac StringReversal.java && java StringReversal
```

```bash
javac PalindromeCheck.java && printf 'A man, a plan, a canal: Panama\n' | java PalindromeCheck
```

## Takeaway

Both exercises look like loop problems and are actually **encoding** problems.

For reversal, `char` is the wrong unit: `StringBuilder.reverse()` fixes surrogate
pairs but not combining marks or emoji sequences, and only grapheme clusters
match what a reader means by "character".

For palindromes, the loop is five lines and the definition is the hard part —
strict comparison answers `false` for every real-world example, so the
normalisation step *is* the exercise.
