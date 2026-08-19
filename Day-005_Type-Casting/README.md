# Day 5 — Type Casting: Implicit vs Explicit

**18 Aug 2026 (Tue) · Ch 2**

## The rule in one line

**Widening is implicit; narrowing is explicit.** If the target type can always
hold the value, Java converts silently. If it might not, you must write the cast
and take responsibility for the result.

## The widening ladder

```
byte ──▶ short ──▶ int ──▶ long ──▶ float ──▶ double
                    ▲
            char ───┘
```

Moving **right** = implicit, no cast needed.
Moving **left** = explicit cast required.

`char` widens to `int`, but `char` ⟷ `short` needs a cast **both ways** — they
are the same width (16 bits) yet `char` is unsigned and `short` is signed, so
neither can hold the other's full range.

## Narrowing: what actually happens

Java does not round and does not clamp for integer types — it **discards the
high-order bits**.

| Cast | Result | Why |
| --- | --- | --- |
| `(byte) 200` | `-56` | 200 doesn't fit −128..127; bits wrap |
| `(byte) 130` | `-126` | same wrap |
| `(int) 3.99` | `3` | truncates **toward zero** |
| `(int) -3.99` | `-3` | **not** −4 — truncation, not floor |
| `(char) 65` | `A` | int reinterpreted as a code point |

Without the cast, javac stops you:

```
error: incompatible types: possible lossy conversion from double to int
```

Note the word **possible** — javac rejects based on the *types*, not the value.
`int i = 3.0;` fails even though 3.0 fits perfectly.

### Two different narrowing behaviours

Easy to conflate, but they differ:

| Conversion | Out-of-range behaviour |
| --- | --- |
| integer → smaller integer | **wraps** — `(byte) 200` → `-56` |
| floating-point → integer | **saturates** — `(int) 1e20` → `2147483647` |

And `(int) Double.NaN` → `0`.

## The trap: widening can still lose data

Widening is implicit because it is always *permitted*, not because it is always
*exact*. Floating-point types trade mantissa bits for range:

```java
int i = 16_777_217;   // 2^24 + 1
float f = i;          // implicit — no cast, no warning
(int) f               // 16777216  ← lost 1
```

```java
long l = 9_007_199_254_740_993L;  // 2^53 + 1
double d = l;                     // implicit
(long) d                          // 9007199254740992  ← lost 1
```

`float` has 24 bits of mantissa, `double` has 53. Beyond that, consecutive
integers are no longer representable. **`int → float` and `long → double` are
the two lossy widenings** — worth memorising, because nothing in the syntax
warns you.

## Promotion in expressions

Before arithmetic, operands narrower than `int` are promoted to `int`, and if
the operands differ, the narrower is promoted to the wider.

```java
5 / 2          // 2     — both int, so integer division
5 / 2.0        // 2.5   — one double promotes the whole expression
(double)5 / 2  // 2.5   — cast binds tighter than /, applies to 5 only
'A' + 1        // 66    — char promoted to int
(char)('A'+1)  // 'B'   — cast back to see a character
```

The classic bug is `5 / 2` inside a larger expression: the truncation happens
**before** assignment, so `double avg = 5 / 2;` gives `2.0`, not `2.5`.

## Compound assignment hides a cast

The most surprising rule in the topic:

```java
byte b = 10;
b += 5;        // ✅ compiles
b = b + 5;     // ❌ error: possible lossy conversion from int to byte
```

Both look identical, but the spec defines `b += 5` as `b = (byte)(b + 5)` — the
compound operator **supplies a narrowing cast for you**. Convenient, and a
silent-overflow hazard:

```java
byte w = 120;
w += 10;       // 130 doesn't fit → -126, no warning
```

## Reference casting

Same two directions, completely different mechanism. A reference cast **never
touches the object** — it only changes which type you may use it through. The
object's real class is fixed at `new`.

| Direction | Syntax | Checked |
| --- | --- | --- |
| Upcast (child → parent) | implicit | always safe |
| Downcast (parent → child) | explicit `(Dog)` | at **runtime** |

```java
Animal a = new Dog();      // upcast — implicit
Dog d = (Dog) a;           // downcast — explicit, valid here
```

A wrong downcast compiles fine and fails at runtime:

```
ClassCastException: class Cat cannot be cast to class Dog
```

Guard it with `instanceof`. Since Java 16 the pattern variable binds the result,
so the cast isn't written twice:

```java
if (a instanceof Dog d) {
    d.fetch();
}
```

**Contrast worth noting:** a bad primitive narrowing fails *silently* (you get
garbage); a bad reference downcast fails *loudly* (you get an exception).

## Never castable

`boolean` converts to nothing and nothing converts to `boolean`:

```
error: incompatible types: boolean cannot be converted to int
```

No cast syntax rescues this — it isn't a narrowing, it's a type error.

## Run

```bash
javac TypeCasting.java && java TypeCasting
javac ReferenceCasting.java && java ReferenceCasting
```

## Takeaway

Implicit vs explicit is about whether the compiler is willing to take the blame.
It converts silently when the target type can always hold the source type — but
"always holds" is a statement about **ranges, not precision**, which is why
`int → float` slips through implicitly and quietly loses digits.
