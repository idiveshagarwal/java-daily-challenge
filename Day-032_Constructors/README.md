# Day 32 — Constructors: Default and Parameterised

**14 Sep 2026 (Mon) · Ch 12 — OOP Basics**

A constructor runs once, when `new` creates the object, and its job is to leave
that object **valid**. Day 31 covered the basics — the implicit default
constructor vanishing, and `this.field` shadowing. This day covers how several
constructors cooperate, the order initialisation happens in, and the traps.

## Default and parameterised

```
new Rectangle()                 -> rect[1.0 x 1.0]   area 1.0
new Rectangle(3)                -> rect[3.0 x 3.0]   area 9.0
new Rectangle(4, 2)             -> rect[4.0 x 2.0]   area 8.0
new Rectangle(4, 2.5, "door")   -> door[4.0 x 2.5]   area 10.0
```

These are **overloads** (Day 27), chosen by the arguments at the `new`
expression. The no-argument one is written out explicitly — Java supplies it for
free only when a class declares no constructor at all.

## Chain to one real constructor

```java
public Rectangle()                          { this(1.0, 1.0); }
public Rectangle(double side)               { this(side, side); }
public Rectangle(double width, double height) { this(width, height, "rect"); }
public Rectangle(double width, double height, String label) {
    // validate, assign, count — the only place this happens
}
```

`new Rectangle()` passes through **three** constructors, yet the object counter
rises by exactly **one**:

```
objects created before: 4, after: 5   (+1)
```

Validation and side effects live in one constructor; every other entry point
chains to it. Copying the body into each overload is how they drift apart.

The payoff is that every way in is checked:

```
new Rectangle(-2)          -> dimensions must be positive: -2.0 x -2.0
new Rectangle(4, 0)        -> dimensions must be positive: 4.0 x 0.0
new Rectangle(4, 2, "  ")  -> label is required
```

## Copy constructor

```java
public Rectangle(Rectangle other) { this(other.width, other.height, other.label + "-copy"); }
```

```
original = panel[5.0 x 3.0]
copy     = panel-copy[5.0 x 3.0]
copy == original : false
```

Unlike `Rectangle alias = original;` — which copies only the reference — this
makes a new object. And because it chains, a copy is validated like an original.

## Initialisation order

```
first new Widget():
  1. static block            (once, when the class loads)
  2. field initialiser a
  3. instance initialiser block
  4. field initialiser b
  5. constructor body         a=1 b=2
second new Widget():
  2. field initialiser a
  3. instance initialiser block
  4. field initialiser b
  5. constructor body         a=1 b=2
```

- The static block runs **once**, for the class.
- Field initialisers and instance blocks run in **textual order** — `b` is
  declared after the block, so it runs after.
- The **constructor body runs last**, which is why it already sees `a=1, b=2`.

## Five pitfalls

### 1. `void` turns a constructor into a method

```java
public void Box(int size) { this.size = size; }
```

```
new Box()   -> size -1    ← the "constructor" never ran
b.Box(5)    -> size 5     ← callable as an ordinary method
```

A constructor has **no** return type, not even `void`. Add one and it silently
becomes a method that shares the class name. It compiles, `-Xlint:all` is
silent, and since the class now declares no constructor, javac supplies a
default one — so `new Box()` works while `new Box(5)` doesn't.

### 2. `this(...)` must come first — **except on Java 25**

For thirty years `this(...)`/`super(...)` had to be the first statement. **Java
25 finalised flexible constructor bodies**, and I checked both:

```
T() { System.out.println("hi"); this(5); }

javac --release 21:  error: flexible constructors is not supported in -source 21
javac on 25:         compiles, prints "hi", then chains
```

What 25 still forbids is touching the object before it exists:

```
reading a field:   error: cannot reference x before supertype constructor has been called
calling a method:  error: cannot reference helper() before supertype constructor has been called
```

So the textbook rule is out of date on your JDK. The useful case is validating
or computing arguments *before* chaining. This day's code keeps the old style so
it still compiles on 21.

### 3. Constructors that chain in a circle

```java
C()      { this(1); }
C(int x) { this(); }
// error: recursive constructor invocation
```

A recursive **method** (Day 29) fails at runtime with `StackOverflowError`. A
constructor cycle is caught at **compile** time, because constructor chaining is
fully static.

### 4. A `final` field must be set on every path

```java
F(String id) { this.id = id; }               // forgets level
F2(boolean flag) { if (flag) { level = 1; } } // forgets it on the else branch
// error: variable level might not have been initialized   (both)
```

javac tracks definite assignment through every constructor **and** every branch.
It's the same check that turns Day 31's shadowing bug into a compile error.

### 5. Calling an overridable method from a constructor

```
new Child():
  Base constructor calls describe(): constant=literal computed=null count=0
after construction:
  constant=literal computed=literal count=42
```

The base-class constructor runs **before** the subclass's field initialisers. If
it calls a method the subclass overrides, that method sees fields still at their
defaults — including a **`final` field reading `null`**, which is supposed to be
impossible.

`constant` only *looks* fine because it's a compile-time constant that javac
inlines into `describe()` itself (Day 6). I confirmed this by adding a
non-constant final alongside it: that one reads `null`.

`javac -Xlint:this-escape` catches it for a public top-level class:

```
warning: [this-escape] possible 'this' escape before subclass is fully initialized
```

…but did **not** warn for the nested classes in this file, which can't be
subclassed from elsewhere. **Rule: a constructor should only call `private`,
`static` or `final` methods.**

## Run

```bash
javac Rectangle.java ConstructorDemo.java && java ConstructorDemo
```

```bash
javac ConstructorPitfalls.java && java ConstructorPitfalls
```

## Takeaway

Give a class several constructors but **one** body: every overload chains with
`this(...)` to the constructor that validates and assigns, so an invalid object
can't be built through any entry point.

The constructor body runs *last*, after initialisers — and a superclass
constructor runs *before* the subclass's initialisers, which is why calling an
overridable method from a constructor exposes half-built state.

And one rule from every older tutorial — "`this(...)` must be first" — no longer
holds on Java 25.
