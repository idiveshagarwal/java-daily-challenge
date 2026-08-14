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

Folder naming: `Day-226_Hello-World` — zero-padded day number, underscore, then the
problem name in `Title-Case-With-Hyphens`. The Java file name matches its public class.

## Running a day

```bash
cd Day-226_Hello-World && javac HelloWorld.java && java HelloWorld
```

Class files are gitignored, so compiling in place is safe.

## Index

| Day | Problem | Topic | Solution |
| --- | --- | --- | --- |
| 226 | Hello World | Basics | [HelloWorld.java](Day-226_Hello-World/HelloWorld.java) |

## Adding a day

1. Create `Day-<NNN>_<Problem-Name>/`.
2. Add the `.java` file and a `README.md` covering problem, approach, and complexity.
3. Add a row to the index table above.
