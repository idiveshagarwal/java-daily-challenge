# Day 15 — do-while: Menu-Driven Program

**28 Aug 2026 (Fri) · Ch 6 — Loops**

## The one guarantee

```java
do {
    body
} while (condition);
```

The body runs **before** the condition is ever tested, so it always executes at
least once. Verified with a false condition:

```
while    -> body ran 0 times
do-while -> body ran 1 time
```

Test-first vs test-last. Everything else about the two loops is identical.

## Why a menu wants exactly this

A menu must be shown once before you can know whether to show it again — the
exit condition **doesn't exist until the body has run**.

```java
// while — needs a primed variable
int choice = -1;              // a lie, invented purely to enter the loop
while (choice != 0) {
    showMenu();
    choice = read();
}

// do-while — says what it means
int choice;                   // no initial value needed
do {
    showMenu();
    choice = read();
} while (choice != 0);
```

The `while` version needs a sentinel value chosen only to satisfy a test that
has no real data yet. That's the tell: when you have to invent a value to make
the first check pass, the check belongs at the bottom.

## The required semicolon

```java
do { ... } while (cond);
                        ^ required
```

Omitting it is a **compile error**, not a silent bug:

```
error: ';' expected
```

Unusual for Java — no other block construct ends in a semicolon. The parser
needs it because `while (cond)` would otherwise read as the beginning of a new
`while` loop.

## The exercise

`MenuDrivenApp` wires the fortnight's exercises into one loop:

| Option | Comes from |
| --- | --- |
| 1 Reverse a number | Day 14 |
| 2 Largest of three | Day 11 |
| 3 Multiplication table | Day 13 |
| 4 Calculator | Day 12 |
| 0 Exit | the sentinel |

Each is the *corrected* version from its day — reverse accumulates in a `long`
so it can't overflow, and largest-of-three uses `Math.max` so `NaN` can't
mislead it.

## Two failures a menu loop must survive

Both matter more here than in a straight-line program, because a loop turns one
bad input into an infinite one.

### 1. Non-numeric input must be consumed

`hasNextInt()` doesn't remove the offending token. Testing without consuming
means the next pass tests the *same* token and fails identically — the menu
spins forever (Day 9). The fix is to consume it in the failure branch:

```java
System.out.print("\"" + sc.next() + "\" is not a number. Choice: ");
```

Verified: input `abc` prints `"abc" is not a number.` once and then accepts the
next real choice.

### 2. Input can end before the user picks Exit

This is the one that's easy to miss. A menu loop reading from a pipe — or a
user pressing Ctrl-D — hits end of input with the exit condition still unmet:

```
at java.base/java.util.Scanner.nextInt(Scanner.java:2297)
at EOF.main(EOF.java:8)
```

`NoSuchElementException`, and the program dies mid-loop. Guarding with
`hasNext()` and treating exhaustion as "quit" turns a crash into a clean exit:

```
  (input ended — exiting)

Goodbye.
```

Exit code 0. Worth doing even in a toy program, because it's exactly what
happens when someone pipes a file into your menu.

## Sentinel-controlled loops

The same shape without a menu — stop on a value in the data rather than a count:

```java
do {
    value = readings[index++];
    if (value != -1) { sum += value; count++; }
} while (value != -1 && index < readings.length);
```

```
data: 12, 7, 40, -1, 99   (-1 ends input)
summed 3 values -> 59
```

Note the second guard. Without `index < readings.length`, data that never
contains the sentinel runs straight off the end of the array.

## Run

```bash
javac MenuDrivenApp.java && printf '1\n12345\n0\n' | java MenuDrivenApp
```

```bash
javac DoWhileBasics.java && java DoWhileBasics
```

Try these inputs to exercise the failure paths:

```bash
printf 'abc\n2\n5\n99\n3\n0\n' | java MenuDrivenApp   # bad input, then option 2
```

```bash
printf '1\n999\n' | java MenuDrivenApp                # input ends before Exit
```

## Takeaway

`do-while` is the right loop whenever the exit condition depends on something
the body produces. The giveaway that you've reached for the wrong one is
inventing an initial value purely to make the first test pass.

In a menu loop, input handling stops being cosmetic: a token that isn't
consumed spins forever, and input that ends early crashes the program. Both are
one `hasNext` check away from correct.
