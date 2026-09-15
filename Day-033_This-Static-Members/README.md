# Day 33 — The `this` Keyword, Instance vs Static Members

**15 Sep 2026 (Tue) · Ch 12 — OOP Basics**

Earlier days already touched both halves: Day 26 introduced the
"non-static … from a static context" error, Day 31 fixed parameter shadowing
with `this.field`, and Day 32 chained constructors with `this(...)`. This day
explains what `this` actually *is*, and what follows from static members
belonging to the class rather than to any object.

## What `this` is

`this` is a reference to the object a method was called **on**. Every instance
method receives it as a hidden first argument:

```java
one.addPoints(7);            // `this` is one
Player.addPointsTo(two, 7);  // static: no `this`, so the object is passed in
```

```
one.addPoints(7)           -> Player#6[one, score=7]
Player.addPointsTo(two, 7) -> Player#7[two, score=7]
```

Same result. A static method gets no hidden argument, so `this` has nothing to
refer to — which is the entire reason it can't appear there.

## The three roles of `this`

**1. Disambiguation** — a parameter shadows a field (Day 31):

```java
this.name = name;
```

**2. Returning `this`** — fluent chaining, exactly how `StringBuilder.append`
works (Day 25):

```java
carol.addPoints(10).addPoints(5).addPoints(3)
// -> Player#3[carol, score=18]
// returned == carol : true
```

**3. Passing `this`** — handing the current object to another:

```java
public Player pairWith(Player other) {
    this.partner = other;
    other.partner = this;      // esha now holds a reference back to dev
    return this;
}
```

```
esha.isPartnerOf(dev) : true
dev.isPartnerOf(esha) : true
```

## Instance vs static

| | instance | static |
| --- | --- | --- |
| belongs to | each object | the class |
| copies | one per object | exactly one |
| accessed as | `obj.member` | `ClassName.member` |
| has `this`? | yes | **no** |
| dispatch | runtime type | **declared type** |

```
Player#1[alice, score=40]
Player#2[bob, score=15]
Player.getOnline() = 2
```

The constructor reads the **shared** `nextId` and stores the result in the
**per-object** `id` — that's how one counter hands two players different ids.

A static factory like `Player.guest()` is static because there's no player to
call it on until one exists.

## Five pitfalls

### 1. Static access through `null` doesn't throw

```java
Config nothing = null;
nothing.describe()   // -> "Config version 2.1"
nothing.version      // -> "2.1"
```

No `NullPointerException`. javac rewrites `nothing.describe()` as
`Config.describe()` at compile time, so the null is never dereferenced.

It compiles silently. Only `-Xlint:static` says anything:

```
warning: [static] static method should be qualified by type name, Config, instead of by an expression
```

Always call statics as `ClassName.member()` — calling them through a variable
implies the object matters, and it doesn't.

### 2. Static methods are hidden, not overridden

```java
Parent p = new Child();
p.name()   // -> Child.name (instance)    ← runtime type decides
p.who()    // -> Parent.who (static)      ← declared type decides
```

Same object, same call syntax, different rules. A subclass static method with
the same signature **hides** the parent's; which one runs is fixed at compile
time by the variable's type — the same rule as overload resolution (Day 27).

javac enforces the distinction:

```
@Override on a static method:
  error: static methods cannot be annotated with @Override

an instance method matching a parent's static one:
  error: who() in Child cannot override who() in Parent
    overridden method is static
```

### 3. One instance changes a static for all

```
alice visits twice, bob once:
  alice.mine=2  bob.mine=1
  alice.total=3  bob.total=3  Visitor.total=3
alice.total = 100;
  bob.total=100  Visitor.total=100   ← bob changed too
```

`alice.total = 100` *reads* as if it concerns alice. It rewrote the one field
every `Visitor` shares. Mutable static state is effectively a global variable —
with all the same problems, including between threads.

### 4. A local shadows a static field

```java
static int count = 5;
static int withLocal() { int count = 99; return count + Registry.count; }
// withLocal() -> 104   (99 local + 5 static)
```

Day 31's fix for shadowing was `this.`, but a static method has **no `this`**.
Reach the static field through the class name instead.

### 5. What javac refuses

```
this inside a static method:
  error: non-static variable this cannot be referenced from a static context

an instance field from a static method:
  error: non-static variable count cannot be referenced from a static context

this = other;
  error: cannot assign to 'this'
```

The last one is why `this` is safe to rely on: inside a method it can never be
repointed at a different object.

## Run

```bash
javac Player.java ThisAndStaticDemo.java && java ThisAndStaticDemo
```

```bash
javac StaticPitfalls.java && java StaticPitfalls
```

## Takeaway

`this` is just the object a method was called on, passed invisibly. That one
idea explains all three uses — disambiguating, returning it for chaining,
passing it to another object — and explains why static methods can't use it.

Static members follow the opposite rule: they belong to the class and are
resolved from the **declared type** at compile time. So a static call through
`null` succeeds, a static method in a subclass hides rather than overrides, and
writing a static field through one instance silently changes it for all of them.
The habit that prevents all three is the same: write `ClassName.member`, never
`variable.member`, for anything static.
