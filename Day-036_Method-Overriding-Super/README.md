# Day 36 — Method Overriding and the `super` Keyword

**18 Sep 2026 (Fri) · Ch 13 — Inheritance**

Day 35 showed that the *reference* type decides what you may **call**. Today's
rule is the other half: the *object* decides which body actually **runs**.

```java
public class Manager extends Employee {
    @Override
    public double annualPay() {
        return super.annualPay() * (1 + bonusRate);   // extend, don't replace
    }
}
```

`super` means two different things depending on where it appears:

| Written | Means |
| --- | --- |
| `super(name, salary);` | call the parent **constructor** — first line only (Day 35) |
| `super.annualPay()` | call the parent's **version of this method**, on this same object |

## 1. Same call, different body

```
Employee Arjun     annualPay() = 600000.0     Arjun earns 600000.0
Manager Priya      annualPay() = 1188000.0    Priya earns 1188000.0, manages 2
```

Both variables are declared `Employee`. Java picks the method body from the
object's class at runtime — **dynamic dispatch** — so the loop never mentions
`Manager` and never needs to.

## 2. `super.annualPay()` reaches the version you replaced

```
Employee.annualPay()  : 1080000.0   <- the base calculation
Manager.annualPay()   : 1188000.0   <- super.annualPay() * 1.10
```

Inside the override, `this.annualPay()` would call the override again and
recurse until `StackOverflowError`. `super` isn't a variable holding a parent
object — it's an instruction to call `Employee`'s version on *this* object.

## 3. The parent starts running the child's code

`Employee.describe()` is written as `return name + " earns " + annualPay();` —
and for a Manager it prints:

```
Priya earns 1188000.0, manages 2
```

The bonus is in there, although `Employee.java` has never heard of a bonus: the
unqualified call means `this.annualPay()`, and `this` is a Manager. This is
exactly why a constructor must not call overridable methods (Day 35, trap 3) —
it would run child code before the child's fields exist.

## 4. Covariant return types

`Employee.copy()` returns `Employee`; `Manager.copy()` narrows it to `Manager`:

```
Manager clone = priya.copy();   <- compiles, no cast needed
clone reports        : [Arjun, Meera, Sanjay]
original reports     : [Arjun, Meera]   <- independent
```

An override may **narrow** the return type to a subtype. It may not widen it,
and the parameter list must match exactly — change that and you've written a
different method, which is trap 1.

## Six pitfalls

### 1. An overload is not an override

```java
class Printer     { String print(Object value) { ... } }
class LoudPrinter extends Printer { String print(String value) { ... } }
```

```
printer.print("hello")       : Printer.print(Object): hello
loud.print("hello")          : LoudPrinter.print(String): HELLO
loud.print((Object) "hello") : Printer.print(Object): hello
```

`LoudPrinter` now has *two* print methods and replaced nothing. Overloads are
picked by the compiler from the static types (Day 27); overrides are picked at
runtime from the object. `@Override` would have caught it:

```
error: method does not override or implement a method from a supertype
```

### 2. Static methods are hidden, not overridden

```
Site site = new EuSite();
site.region()          : Site.region        <- the REFERENCE type wins
site.instanceRegion()  : EuSite.instanceRegion  <- the OBJECT wins
```

A static method belongs to the class, so there's no object to dispatch on. javac
even warns about the call that misleads:

```
warning: [static] static method should be qualified by type name, Site, instead of by an expression
```

and annotating one is an error of its own:

```
error: static methods cannot be annotated with @Override
```

### 3. Private methods are never overridden

A public `render()` in the parent calls a `private header()`. The child declares
`private String header()` with the same signature:

```
report.render()          : render -> Report.header   <- still the parent's
report.callOwnHeader()   : SalesReport.header
```

A private method isn't inherited, so there's nothing to override and no
dispatch: `Report.render()` was bound to `Report.header()` at compile time. A
method meant to be replaceable must be at least package-private.

### 4. Four rules the compiler enforces

| Change | Allowed? | Error if not |
| --- | --- | --- |
| access `public` → `protected` | no (may only widen) | `attempting to assign weaker access privileges; was public` |
| add `throws IOException` | no (may only narrow) | `overridden method does not throw IOException` |
| return `int` → `long` | no (subtype only) | `return type long is not compatible with int` |
| override a `final` method | no | `overridden method is final` |

Each error is preceded by `error: greet() in C cannot override greet() in P`.
Every rule protects the same thing: code holding a parent reference must not be
surprised by a method that is suddenly inaccessible, throws more, or returns
something else.

### 5. `equals` is the classic victim

```java
public boolean equals(Point other) { ... }   // overload, not override
```

```
a.equals(b)                   : true   <- the overload runs
a.equals(bAsObject)           : false  <- Object.equals, identity only
List.of(a).contains(b)        : false
HashSet.contains(b)           : false
GoodPoint HashSet.contains(..): true
```

Every collection calls `equals(Object)`, so the overload is never reached and
the objects compare by identity — a bug that passes every direct unit test. The
parameter must be `Object`, and `@Override` is what tells you so.

### 6. `super` goes up exactly one level

```
Level3 (parent says Level2 (parent says Level1))
```

Level 3 reaches Level 1 only because Level 2 chose to call `super` too. Skipping
a level isn't expressible — `super.super.name()` doesn't even parse:

```
error: <identifier> expected
```

So an override that forgets `super` breaks the chain for everyone below it, and
a parent has no way to force the call.

## Run

```bash
javac Employee.java Manager.java OverridingDemo.java && java OverridingDemo
```

```bash
javac OverridingPitfalls.java && java OverridingPitfalls
```

(The pitfalls file compiles with one deliberate `[static]` warning — that's
pitfall 2 demonstrating itself.)

## Takeaway

An override needs the **same name, same parameter types**, a return type that is
the same or narrower, access that is the same or wider, and no new checked
exceptions. Anything else compiles into a *different* method that quietly never
runs.

Write `@Override` on every one. It costs nothing and turns four of today's six
pitfalls — the typo, the overload, the private method, the static method — into
compile errors instead of behaviour you discover in production.
