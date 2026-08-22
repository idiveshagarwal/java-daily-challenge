# Day 9 — Taking User Input with Scanner

**22 Aug 2026 (Sat) · Ch 4 — I/O**

## The mental model

`Scanner` wraps an input source and hands back **tokens**. A token is a run of
characters bounded by the *delimiter*, which defaults to whitespace.

```java
import java.util.Scanner;                  // easy to forget — it's java.util, not java.io

Scanner sc = new Scanner(System.in);
```

Nearly every Scanner problem follows from one asymmetry:

> **Token methods stop at the delimiter and leave it in the buffer.
> `nextLine()` consumes its line ending.**

## The methods

| Method | Reads | Consumes the newline? |
| --- | --- | --- |
| `nextLine()` | rest of the line | **yes** |
| `next()` | one token | no |
| `nextInt()` | one token as `int` | no |
| `nextDouble()` | one token as `double` | no |
| `nextBoolean()` | `true`/`false`, case-insensitive | no |

Every `next*()` has a matching `hasNext*()` that tests **without consuming** —
the basis of every validation loop.

```java
next()      // on "hello world extra"  →  "hello"
nextLine()  // immediately after       →  " world extra"   (rest of the same line)
```

## The classic trap

```java
int age = sc.nextInt();
String name = sc.nextLine();   // ← comes back EMPTY, never waits for input
```

Given input `25⏎Divesh Agarwal⏎`, the buffer looks like:

```
2 5 \n D i v e s h   A g a r w a l \n
     ↑
     nextInt() stops HERE, leaving \n unread
```

`nextLine()` then reads from that position to the end of the line — finding
nothing, and returning `""`. Verified output:

```
age  = [25]
name = []   <- EMPTY, and it never waited for input
```

### Three fixes

**1. Flush the newline.**

```java
int age = sc.nextInt();
sc.nextLine();                 // discard the leftover newline
String name = sc.nextLine();   // now correct
```

**2. Read lines only, parse yourself.** *(best default)*

```java
int age = Integer.parseInt(sc.nextLine().trim());
```

Never mix token methods with line methods and the problem cannot arise.

**3. Use tokens only.**

```java
int age = sc.nextInt();
String name = sc.next();
```

But `next()` stops at whitespace, so `Divesh Agarwal` reads as just `Divesh`.
Only safe for single words.

**Rule: pick line-based or token-based, and stay consistent.**

## Handling bad input

Three failure modes:

| Exception | Cause |
| --- | --- |
| `InputMismatchException` | next token isn't the requested type |
| `NoSuchElementException` | no token at all — end of input, or closed stream |
| *(no exception)* | infinite loop — see below |

### Why naive retry loops spin forever

```java
while (true) {
    try { return sc.nextInt(); }
    catch (InputMismatchException e) { /* retry */ }   // ← BUG
}
```

**`InputMismatchException` does not consume the offending token.** `"abc"` is
still sitting in the buffer, so the next `nextInt()` fails on the same token,
forever. Tested with a capped counter: **1,000,000 identical failures on a
single `abc`** before giving up.

The catch block *must* discard it:

```java
catch (InputMismatchException e) {
    sc.nextLine();          // ESSENTIAL
}
```

### Better: test before consuming

`hasNextInt()` avoids the cleanup entirely:

```java
if (!sc.hasNextInt()) {
    String bad = sc.next();     // consume it, or we spin
    System.out.println("not a number: " + bad);
    continue;
}
int value = sc.nextInt();
sc.nextLine();                  // flush (Fix 1)
```

`SafeInput.java` implements this as reusable `readIntInRange` and
`readNonEmptyLine` helpers. Verified rejecting `abc`, then `-5` as out of
range, then accepting `30`.

## Closing a Scanner closes System.in

```java
Scanner s1 = new Scanner(System.in);
s1.nextLine();
s1.close();                          // also closes System.in — permanently

Scanner s2 = new Scanner(System.in);
s2.nextLine();                       // NoSuchElementException
```

Verified. `Scanner.close()` closes the underlying source, and `System.in` cannot
be reopened for the life of the JVM.

Practical consequences:

- Create **one** Scanner over `System.in` and pass it around.
- Do **not** wrap a `System.in` Scanner in try-with-resources unless the program
  is finished reading — the automatic `close()` has the same effect.
- Closing at JVM exit is pointless anyway; the OS reclaims it.

## Alternatives

| Class | Use when |
| --- | --- |
| `Scanner` | convenience, parsing mixed types, small input |
| `BufferedReader` | speed, or large input — significantly faster |
| `System.console()` | passwords — `readPassword()` doesn't echo |

`Scanner` parses and allocates per token; `BufferedReader.readLine()` just
hands back a line. For competitive programming or large files, `BufferedReader`
with a manual split is the usual choice. `System.console()` returns `null` when
input is piped or under an IDE, so guard it.

## Run

All three read from standard input, so they work interactively or from a pipe:

```bash
javac ScannerBasics.java && printf 'Divesh Agarwal\n25\n1.75\ntrue\n' | java ScannerBasics
```

```bash
javac NextLineTrap.java && printf '25\nDivesh Agarwal\n25\nDivesh Agarwal\n' | java NextLineTrap
```

```bash
javac SafeInput.java && printf 'abc\n-5\n30\nDivesh Agarwal\n' | java SafeInput
```

`NextLineTrap` takes the age/name pair twice — once to show the bug, once to
show the fix.

## Takeaway

Only `nextLine()` consumes its own line ending. Every other `next*()` leaves the
delimiter behind, which is why `nextInt()` followed by `nextLine()` returns an
empty string, and why a retry loop that doesn't discard the bad token spins
forever on it.

Test with `hasNextInt()` instead of catching and cleaning up, keep one Scanner
for `System.in`, and never close it mid-program.
