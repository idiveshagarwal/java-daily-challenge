# Day 3 — Comments, Keywords & Compiling with javac

**16 Aug 2026 (Sun) · Ch 1**

## Comments

Three forms, one of which is not really a comment at all — it's documentation.

| Form | Syntax | Read by |
| --- | --- | --- |
| Line | `// …` to end of line | nobody |
| Block | `/* … */` | nobody |
| Javadoc | `/** … */` | the `javadoc` tool |

### Block comments do not nest

`/*` … `/*` … `*/` ends at the **first** `*/`. The remaining `*/` is then a
syntax error. This is why commenting out a region that already contains a block
comment breaks the build — and why `//` on every line is the safer way to
disable code.

### Comment delimiters inside strings are just text

The lexer only recognises comments outside string literals:

```java
String s = "// this is text, not a comment";
```

### Comments are gone after compilation

`javac` discards them. Verified with `javap` on this day's class — phrases that
appear only in comments return zero hits in the bytecode, while a string literal
appears three times:

```bash
javap -c -p -v CommentsAndKeywords.class | grep -c "reserved TYPE NAME"   # 0
javap -c -p -v CommentsAndKeywords.class | grep -c "not a comment"        # 3
```

Comments cost nothing at runtime. Write as many as are useful.

### The one exception: Unicode escapes

`javac` translates Unicode escapes in a pass **before** the lexer decides what a
comment is. So `\u000A` becomes a real newline first, ending a `//` comment
early and leaving the rest of the line as live code.

`UnicodeEscapeGotcha.java` demonstrates it — a `System.out.println` sitting
inside a `//` comment that genuinely executes:

```
1. before the comment
2. THIS RAN, from inside a // comment
3. after the comment
```

Practical rule: never write `\u000A`, `\u000D` or `\u0022` inside a comment.

## Keywords

**50 reserved words.** They can never be used as identifiers.

```
abstract   continue   for          new         switch
assert     default    goto         package     synchronized
boolean    do         if           private     this
break      double     implements   protected   throw
byte       else       import       public      throws
case       enum       instanceof   return      transient
catch      extends    int          short       try
char       final      interface    static      void
class      finally    long         strictfp    volatile
const      float      native       super       while
```

Two are **reserved but unused**: `goto` and `const` have no meaning in Java. The
language claims the words purely so they cannot become identifiers.

### Contextual keywords

Newer additions would have broken existing code if made reserved, so they bind
only in specific positions and stay legal identifiers everywhere else:

`var` · `yield` · `record` · `sealed` · `permits` · `non-sealed` · `when`

All of these compile:

```java
int var = 5;
int record = 10;
int sealed = 20;
int permits = 30;
int yield = 40;
```

Swap any one for `class` and javac rejects it at once.

### true, false and null are not keywords

They are reserved, but the spec classifies them as **literals**. A frequent exam
trap — the question asks for the keyword count and `true`/`false`/`null` are not
among the 50.

## Compiling with javac

```bash
javac CommentsAndKeywords.java      # produces CommentsAndKeywords.class
java CommentsAndKeywords            # class name, no .class extension
```

### Flags worth knowing early

| Flag | Does |
| --- | --- |
| `-d out` | write `.class` files to `out/`, mirroring package dirs |
| `-cp path` | where to find existing classes (the **package root**) |
| `-Xlint:all` | enable all optional warnings |
| `-Werror` | treat warnings as errors |
| `--release 21` | compile against a specific Java version's API |
| `-g` | include full debug info for the debugger |
| `-encoding UTF-8` | source file encoding — set it explicitly on shared code |
| `-verbose` | show every class loaded and file written |

### Reading a javac error

```
Bad.java:3: error: not a statement
        int class = 5;
        ^
1 error
```

Four parts: **file : line**, **severity**, **message**, then a caret pointing at
the offending column. Always fix the *first* error and recompile — later ones
are frequently cascading noise from the first.

### Generating the docs

```bash
javadoc -d docs CommentsAndKeywords.java
```

This is what makes the `/** … */` form worth using: the tags (`@author`,
`@version`, `@since`, `@param`, `@return`) become structured fields in the
generated HTML.

## Run

```bash
javac CommentsAndKeywords.java && java CommentsAndKeywords
javac UnicodeEscapeGotcha.java && java UnicodeEscapeGotcha
```

## Output

```
Day 3 — Comments, keywords & javac

Inside a string literal:
  // this is text, not a comment
  /* also just text */

Contextual keywords used as variable names:
  var + record + sealed + permits + yield = 105

true / false / null are LITERALS, not keywords:
  flag = true, nothing = null
```

## Takeaway

Comments are stripped before bytecode and cost nothing — except when a Unicode
escape smuggles code back in, because escape processing happens before the
source is even split into tokens.

Reserved words are fixed for life; contextual keywords were the workaround that
let Java add `var` and `record` without invalidating existing programs.
