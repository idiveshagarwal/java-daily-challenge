# Day 31 — Classes and Objects: Your First Class

**13 Sep 2026 (Sun) · Ch 12 — OOP Basics**

## Blueprint and instance

A **class** declares what state each object holds (fields) and what it can do
(methods). An **object** is one concrete instance built from it with `new`,
holding its own copy of that state.

```
class BankAccount        the blueprint — written once
new BankAccount(...)     an object      — created as many times as needed
```

## What `new` does

```java
BankAccount a = new BankAccount("Asha", 500);
```

1. allocates memory for the fields, **zeroed** (Day 19's defaults)
2. runs the **constructor** to set them properly
3. returns a **reference** to the object

## Each object has its own state

```
asha.deposit(250); ravi.withdraw(40);
  BankAccount[owner=Asha, balance=750.00]
  BankAccount[owner=Ravi, balance=60.00]
```

Same class, same methods, separate balances. A method acts on the object it's
called **on**.

A `static` field exists **once for the whole class** — `accountsOpened` counts
every account ever made, and is read as `BankAccount.getAccountsOpened()` with no
object at all (Day 26).

## Objects protect their own state

Putting behaviour next to state lets the object reject operations that would
corrupt it, rather than trusting every caller:

```
withdraw(500) on a balance of 100 -> false   (balance still 100.0)
deposit(-50)                      -> IllegalArgumentException
new BankAccount("", 10)           -> owner is required
```

`balance` is `private`, so `acc.balance = -1000` doesn't compile from outside.
Every change goes through a method that checks it.

## Variables hold references

```java
BankAccount alias = original;   // copies the reference, not the object
alias.deposit(900);             // original changes too
```

```
original == alias          : true
original == other          : false   (identical contents)
original.equals(other)     : false   ← equals not overridden
```

A class that doesn't override `equals` compares by **identity** — the same as
`StringBuilder` (Day 25).

## Five pitfalls

### 1. A shadowed field — silent

```java
Person(String name, int age) {
    name = name;          // meant this.name = name
    this.age = age;
}
```

```
new Person("Divesh", 30)  ->  name = null, age = 30
```

Inside the constructor `name` means the **parameter**, which shadows the field.
`name = name` copies the parameter onto itself and the field keeps its default.

It compiles, it runs, and **`javac -Xlint:all` reports nothing** — verified on
25.0.4. The `null` surfaces later, far from the constructor, as an NPE in some
unrelated method.

**The better fix is `final`,** which moves the same bug to compile time:

```java
final String name;
Person(String name) { name = name; }
// error: variable name might not have been initialized
```

A runtime null becomes a compile error, for free.

### 2. The default constructor vanishes

```java
class Box { int w; }                            // new Box() compiles
class Box { int w; Box(int w) { this.w = w; } }  // new Box() does not
```

```
error: constructor Box in class Box cannot be applied to given types;
  required: int
  found:    no arguments
```

Java supplies a no-argument constructor **only when you declare none**. Adding a
constructor silently removes the free one, breaking any existing `new Box()`.

### 3. Fields default; locals don't

```
fields of a new object:  count=0  price=0.0  active=false  label=null

int local;  System.out.println(local);
error: variable local might not have been initialized
```

Fields are zeroed when `new` allocates the object. Locals must be assigned first.
The field default is the **riskier** of the two: a forgotten assignment reads as
`0` or `null` instead of failing to compile — which is exactly how pitfall 1
slips through.

### 4. The default `toString`

```
System.out.println(person)  ->  ObjectPitfalls$Person@c387f44
```

That's `Object.toString()`: class name, `@`, hash code in hex (`$` because the
class is nested). Override it in any class you'll print or log.

### 5. Calling through `null`

```
Cannot invoke "N$Counter.inc()" because "<local1>" is null   ← without -g
Cannot invoke "N$Counter.inc()" because "counter" is null    ← with -g
```

Java's helpful NPE messages name what was null — but **the variable name only
appears if the class was compiled with `-g`**, which keeps local variable names
in the class file. Without it you get a positional placeholder. Verified both
ways.

## Run

```bash
javac BankAccount.java FirstClassDemo.java && java FirstClassDemo
```

```bash
javac ObjectPitfalls.java && java ObjectPitfalls
```

## Takeaway

A class bundles state with the behaviour allowed to change it, so an object can
refuse to become invalid instead of relying on every caller to be careful.

The pitfall worth carrying forward is the shadowed field: a one-word omission
(`this.`) that compiles cleanly, passes `-Xlint:all`, and produces a `null` that
fails somewhere else entirely. Declaring the field `final` converts that bug
into a compile error — a good reason to make fields `final` by default and
relax it only when a field genuinely needs to change.
