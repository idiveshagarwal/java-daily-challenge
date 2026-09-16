# Day 34 — Encapsulation: Private Fields, Getters and Setters

**16 Sep 2026 (Wed) · Ch 12 — OOP Basics**

Day 31 already made `BankAccount`'s fields private and put validation in its
methods. This day is about the part that usually gets skipped: **private fields
plus a getter and setter for each is not encapsulation.** That combination is
public fields with extra typing.

Encapsulation is controlling *how* state changes, so the object can guarantee
something about itself. `Course` guarantees three things at all times:

- the roster never exceeds capacity
- the roster never contains a duplicate
- credits is always 1–6

## Four attacks, and why they fail

### 1. Mutate the list you handed in

```java
List<String> source = new ArrayList<>(List.of("ana", "ben"));
Course course = new Course("CS101", 3, source, 4);
source.add("gatecrasher");        // caller mutates the ORIGINAL
```

```
source list now  : [ana, ben, gatecrasher]
course roster    : [ana, ben]
```

The constructor takes a **defensive copy**, so the course never saw it.

### 2. Mutate what the getter returns

```
getRoster().add(...) -> UnsupportedOperationException
getRoster().clear()  -> UnsupportedOperationException
roster after  : [ana]   <- unchanged
```

`getRoster` returns `Collections.unmodifiableList(roster)` — readable, not
writable. A copy would also be safe; a view is cheaper and stays in sync, at the
cost of failing at runtime rather than being independent.

### 3. Break the rules through the API

```
enrol("cara")  -> true   CS101[3/3, 4 credits]
enrol("dan")   -> false  <- refused, course is full
enrol("ana")   -> false  <- refused, already enrolled
setCredits(9)  -> credits must be 1..6: 9
```

There is **no `setRoster`**, because "replace the whole roster" isn't an
operation the rules allow, so it isn't in the API. `setCredits` exists because
it validates — setters were never the problem, unchecked ones are.

### 4. Derived state can't drift

`getSeatsLeft()` is computed from the roster, never stored. A stored copy would
have to be updated in both `enrol` and `withdraw`, and would eventually disagree
with reality.

## The same class written the usual way

Private fields, a getter and setter for each:

```
caller mutates the list it passed in -> [ana, ben, gatecrasher]
caller clears the getter's result     -> []
caller sets credits to -99            -> -99
```

Three guarantees broken from outside, without touching a single field directly.
**`private` protected the field; it protected nothing about the object.**

## Five pitfalls

### 1. `private` is per class, not per object

```java
int peekAt(Wallet other) { return other.balance; }   // compiles
```

```
mine.peekAt(yours)    = 250   <- read another object's private field
```

Any method of `Wallet` can touch any `Wallet`'s privates. That's what makes
`equals`, `compareTo` and copy constructors possible without accessors — and it
means `private` draws a boundary around the **class**, not the instance.

### 2. The leaky getter

```java
private final List<String> items;
List<String> getItems() { return items; }
```

```
size before        : 2
after caller ran add() then clear() on the getter result:
size after         : 0
```

`private` and `final` are both intact. `final` froze the **reference**, not the
list (Day 6), and the getter handed that reference out.

### 3. The leaky constructor

```
size at construction : 2
size now             : 4     ← caller kept adding to the list it passed in
```

The object never exposed anything — the caller simply kept the reference it
already had. **Copy on the way in as well as on the way out.**

### 4. Records leak too

A record gives you final fields and accessors for free, which is often mistaken
for immutability:

```
after mutating the source list : [ana, ben, via the original list]
after mutating via accessor    : [ana, ben, via the original list, via the accessor]
```

A record is **shallowly** immutable: you can't reassign the field, but you can
change what it points at. A compact constructor closes both holes at once:

```java
record SafeTeam(String name, List<String> members) {
    SafeTeam { members = List.copyOf(members); }
}
```

```
after mutating the source    : [ana, ben]
accessor add()               -> UnsupportedOperationException
```

`List.copyOf` returns an immutable list, so the accessor is safe with no extra
work.

### 5. Four access levels, and the one with no keyword

| modifier | class | package | subclass | everywhere |
| --- | --- | --- | --- | --- |
| `private` | yes | no | no | no |
| *(none)* | yes | **yes** | no | no |
| `protected` | yes | yes | **yes** | no |
| `public` | yes | yes | yes | **yes** |

Writing no modifier isn't "defaults to public" — it's **package-private**.
Compiling from a different package:

```
error: packageOnly is not public in Box; cannot be accessed from outside package
error: mine has private access in Box
error: openToSubs has protected access in Box
```

Note `protected` is **wider** than package-private, not narrower — it adds
subclasses to everything the package already had.

## Run

```bash
javac Course.java EncapsulationDemo.java && java EncapsulationDemo
```

```bash
javac EncapsulationPitfalls.java && java EncapsulationPitfalls
```

## Takeaway

The test for encapsulation isn't "are the fields private" but **"can an outsider
put this object into a state its own rules forbid?"** A class with a setter per
field fails that test while looking textbook-correct.

Two habits cover most of it: **copy mutable state on the way in and on the way
out**, and **expose operations rather than fields** — `enrol(student)` instead
of `setRoster(list)`. The second is what stops `setRoster` existing to be
misused in the first place.
