# Java Daily Challenge

One Java problem a day. Each day lives in its own folder with the source file and a
short write-up of the problem, the approach, and the complexity.

## Layout

```
java-daily-challenge/
├── README.md
├── .gitignore
└── Day-<NNN>_<Problem-Name>/
    ├── <ClassName>.java
    └── README.md
```

Folder naming: `Day-001_Hello-World` — day number zero-padded to three digits,
underscore, then the problem name in `Title-Case-With-Hyphens`. Three digits keeps the
folder list sorting correctly past day 99. The Java file name matches its public class.

## Running a day

```bash
cd Day-001_Hello-World && javac HelloWorld.java && java HelloWorld
```

Class files are gitignored, so compiling in place is safe.

## Index

| Day | Problem | Topic | Solution |
| --- | --- | --- | --- |
| 001 | Hello World | Basics | [HelloWorld.java](Day-001_Hello-World/HelloWorld.java) |
| 002 | JDK, JRE, JVM & file structure | Fundamentals | [JavaFileStructure.java](Day-002_JDK-JRE-JVM/JavaFileStructure.java) |
| 003 | Comments, keywords & javac | Fundamentals | [CommentsAndKeywords.java](Day-003_Comments-Keywords-Javac/CommentsAndKeywords.java) |
| 004 | Variables & primitive data types | Fundamentals | [VariablesAndPrimitives.java](Day-004_Variables-Primitives/VariablesAndPrimitives.java) |
| 005 | Type casting — implicit vs explicit | Types | [TypeCasting.java](Day-005_Type-Casting/TypeCasting.java) |
| 006 | Constants, final & literals | Types | [ConstantsAndFinal.java](Day-006_Constants-Final-Literals/ConstantsAndFinal.java) |
| 007 | Arithmetic & assignment operators | Operators | [ArithmeticOperators.java](Day-007_Arithmetic-Assignment-Operators/ArithmeticOperators.java) |
| 008 | Relational, logical & ternary operators | Operators | [RelationalOperators.java](Day-008_Relational-Logical-Ternary/RelationalOperators.java) |
| 009 | Taking user input with Scanner | I/O | [ScannerBasics.java](Day-009_Scanner-Input/ScannerBasics.java) |
| 010 | if, if-else & the else-if ladder | Conditionals | [IfBasics.java](Day-010_Conditionals/IfBasics.java) |

## Adding a day

1. Create `Day-<NNN>_<Problem-Name>/`.
2. Add the `.java` file and a `README.md` covering problem, approach, and complexity.
3. Add a row to the index table above.
