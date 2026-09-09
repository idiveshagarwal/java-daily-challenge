# Day 27 — Method Overloading

**09 Sep 2026 (Wed) · Ch 10 — Methods**

Several methods sharing one name, distinguished by their **parameter list**. The
compiler picks which one you meant from the arguments at the call site.

## What counts as different

Only three things: the **number**, the **types**, and the **order** of
parameters.

```java
add(int, int)          add(2, 3)        = 5
add(int, int, int)     add(2, 3, 4)     = 9      different number
add(double, double)    add(2.5, 3.5)    = 6.0    different type
describe(String, int)  describe("x", 5)          different order
describe(int, String)  describe(5, "odd")
```

## What does not

```java
static int    f(int n)
static double f(int n)     // error: method f(int) is already defined

static void f(int count)
static void f(int total)   // error: method f(int) is already defined
```

Both make sense once you see why: the call `f(5)` names **neither** the return
type nor the parameter name, so neither could tell the compiler which you meant.

The **signature** is the name plus the parameter types. That's all javac has to
match against.

## Overloading as default arguments

Java has no default parameter values, so an overload family fills the gap:

```java
format(3.14159)            = 3.14
format(3.14159, 4)         = 3.1416
format(3.14159, 1, true)   = +3.1
```

Each convenience overload **delegates to the fullest one**, so the logic exists
once. Duplicating the body across overloads is how they drift apart. This is why
`println` has ten overloads.

## The resolution ladder

Resolution happens at **compile time**, and javac tries progressively more
permissive rules, stopping at the first phase that finds a match.

Given four overloads all able to accept `show(5)`, removing candidates one at a
time gives the full order:

| candidates | winner |
| --- | --- |
| `long`, `Integer`, `Object`, `int...` | **`long`** |
| `Integer`, `Object`, `int...` | **`Integer`** |
| `Object`, `int...` | **`Object`** |
| `int...` | `int...` |

| Phase | Allows | Widening chain |
| --- | --- | --- |
| 1 | widening only | `int → long → float → double` |
| 2 | boxing | `int → Integer → Object` |
| 3 | varargs | `int → int...` |

**Widening beats boxing.** That ordering is deliberate: it kept pre-Java-5 code
meaning the same thing when autoboxing arrived, so adding a wrapper overload
can't silently steal calls from a primitive one.

## Bound at compile time — the conceptual point

```java
String asString = "hello";
Object asObject = "hello";     // runtime type is STILL String

render(asString);   // render(String)
render(asObject);   // render(Object)   ← same object!
```

Same object, different overload. The compiler used the **declared** type and
never asked what the object really is.

That's the line between the two mechanisms:

| | Chosen | By |
| --- | --- | --- |
| **overloading** | compile time | declared type |
| **overriding** | runtime | actual type |

## `null`

```java
accept(null)   // picks accept(String), not accept(Object)
```

`null` is assignable to every reference type, so **all** overloads apply. javac
picks the most specific — `String` is a subtype of `Object`, so it wins.

But when the candidates are unrelated:

```java
static void f(String s)
static void f(Integer i)
f(null);

// error: reference to f is ambiguous
//   both method f(String) and method f(Integer) match
```

Neither is more specific, so javac refuses to guess. Fix by casting:
`f((String) null)`.

## The `List.remove` trap

The famous real-world consequence. `List` has **both** `remove(int index)` and
`remove(Object o)`:

```
List<Integer> starts as        [10, 20, 30]
remove(1)                   -> [10, 30]        ← removed INDEX 1
remove(Integer.valueOf(1))  -> [10, 20, 30]    ← removed VALUE 1 (absent)
```

**`remove(1)` does not remove the value 1.** The `int` overload matches in
phase 1 without boxing, so it wins — and it means *index*.

This is overload resolution producing a silently wrong result in the standard
library. To remove by value from a `List<Integer>`, box explicitly.

**General lesson:** overloads that differ only between a primitive and its
wrapper are a design smell. `removeAt` and `removeValue` would have cost nothing
and prevented this entirely.

## varargs

```java
static int sum(int... values) { ... }

sum()               = 0    ← empty array, not null
sum(1, 2, 3)        = 6
sum(new int[]{4,5}) = 9    ← an array works too
```

`int...` is an `int[]` with call-site sugar. It must be the **last** parameter,
and there can be only one — and it's the compiler's **last resort** when
resolving an overload.

## Run

```bash
javac Overloading.java && java Overloading
```

```bash
javac OverloadResolution.java && java OverloadResolution
```

## Takeaway

A signature is the name plus parameter types — return type and parameter names
are invisible to the caller, so they can't distinguish overloads.

Resolution runs in three phases and stops at the first match, which is why
**widening beats boxing**, and why `list.remove(1)` silently means "index".

And the whole thing is decided from **declared** types at compile time. That one
sentence is what separates overloading from overriding, which is where Ch 10
goes next.
