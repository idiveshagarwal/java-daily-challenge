# Day 35 — Inheritance: `extends`, Parent and Child Classes

**17 Sep 2026 (Thu) · Ch 13 — Inheritance**

`class Manager extends Employee` says a Manager **is an** Employee, plus whatever
`Manager.java` adds. Everything Employee exposes comes along without being
written again.

```java
public class Manager extends Employee {
    private final List<String> reports = new ArrayList<>();

    public Manager(String name, double monthlySalary) {
        super(name, monthlySalary);          // build the Employee part first
    }

    public void addReport(String employeeName) { reports.add(employeeName); }
}
```

## What the child gets

```
inherited  getName()        : Priya
inherited  annualPay()      : 1080000.0
inherited  badge()          : [Priya]
own        teamSummary()    : Priya manages 2 [Arjun, Meera]
```

`Manager.java` declares none of the inherited methods. It also never touches
`name` directly: the field is `private` to Employee, so a Manager object *has*
it, but Manager's code can't name it —

```
error: name has private access in Employee
```

— and reads it through `getName()` like any other class.

## A Manager is an Employee

A `List<Employee>` payroll accepts a Manager with no cast, and a loop written
only for Employee handles it:

```
payroll size        : 3
total annual pay    : 2340000.0
priya instanceof Employee : true
plain instanceof Manager  : false
```

It only works one way: an Employee isn't a Manager.

## The variable's type decides what you can call

```java
Employee asEmployee = new Manager("Priya", 90_000);
asEmployee.getName();          // fine
asEmployee.addReport("Arjun"); // does not compile
```

```
error: cannot find symbol
  symbol:   method addReport(String)
  location: variable asEmployee of type Employee
```

The object is still a Manager (`getClass()` prints `Manager`). The compiler only
knows the declared type, so the extras are hidden, not gone. `instanceof Manager
m` gets them back.

## Every chain ends at `Object`

```
Manager -> Employee -> java.lang.Object
```

A class with no `extends` silently extends `Object`, which is why `toString()`,
`equals()` and `hashCode()` exist on everything. `priya.toString()` prints
`Manager@...` — Object's version, inherited through two levels.

## Six pitfalls

### 1. Constructors are not inherited

```java
class Parent { Parent(String n) {} }
class Child extends Parent { }
```

Child gets a default constructor whose hidden first line is `super()`, and Parent
has no no-arg constructor:

```
error: constructor Parent in class Parent cannot be applied to given types;
  required: String
  found:    no arguments
```

`new Child("x")` fails too — `Parent(String)` didn't become `Child(String)`:

```
error: constructor Child in class Child cannot be applied to given types;
  required: no arguments
  found:    String
```

### 2. The parent is built first — all of it

```
1. Base field initialiser
2. Base constructor body
3. Derived field initialiser
4. Derived constructor body
```

`Derived()` is *called* first, but its `super()` runs before Derived's own field
initialisers.

### 3. A parent constructor can see a half-built child

If the parent constructor calls a method the child overrides (overriding comes
on a later day), the child's version runs **before the child's fields are set**:

```
Widget constructor calls describe(): text=null constantText=hello tags=null
after construction, describe(): text=hello constantText=hello tags=0
```

`text` and `tags` are `final` and still `null`. `constantText` only looks ready
because it's a compile-time constant and javac inlined `"hello"` into
`describe()` (Day 6) — `javap` shows `getfield` for `text` and `tags` but none
for `constantText`. Calling `tags.size()` there would throw
`NullPointerException`. **Don't call overridable methods from a constructor.**

### 4. Redeclaring a field makes a second field

```java
class Animal { String sound = "..."; String speak() { return sound; } }
class Dog extends Animal { String sound = "woof"; }
```

```
dog.sound        : woof
asAnimal.sound   : ...     <- same object!
dog.speak()      : ...     <- Animal's method sees Animal's field
```

A Dog now carries both fields, and which one you get depends on the declared type
of the expression. **Methods can be overridden; fields can only be hidden.**

### 5. `protected` across packages is narrower than it looks

```java
package b;
public class Sub extends a.Base {
    int mine()              { return this.secret;  }   // ok
    int viaSub(Sub other)   { return other.secret; }   // ok
    int viaBase(Base other) { return other.secret; }   // error
}
```

```
error: secret has protected access in Base
```

From another package, a subclass may use protected members of itself and its own
kind, but not of an arbitrary `Base`, which might belong to some unrelated
subclass.

### 6. One parent, and only if the parent allows it

```
class C extends A, B {}      -> error: '{' expected
class C extends A {}         -> error: cannot inherit from final A
```

A class has exactly one direct superclass — javac doesn't even reach a type
error, it just expects the class body after one name. `String`, `Integer` and
every record are `final`, so none of them can be a parent.

## Run

```bash
javac Employee.java Manager.java InheritanceDemo.java && java InheritanceDemo
```

```bash
javac InheritancePitfalls.java && java InheritancePitfalls
```

## Takeaway

`extends` gives the child the parent's accessible members and makes every child
object usable wherever a parent is expected. It does **not** give the child the
parent's constructors or its private fields, and it doesn't make fields
polymorphic.

The ordering rule behind pitfalls 1–3 is the one to remember: **the parent part
of the object is constructed completely before the child's fields are
initialised**, which is why `super(...)` must come first and why a parent
constructor should never call back into the child.
