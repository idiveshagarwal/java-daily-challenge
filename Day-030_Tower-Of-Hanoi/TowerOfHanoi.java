import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

/**
 * Day 30 — Tower of Hanoi.
 *
 * Three pegs, n disks stacked largest-first on peg A. Move them all to peg C,
 * one disk at a time, never placing a larger disk on a smaller one.
 *
 * The recursive solution is three lines, and it is the best argument in the
 * course for recursion existing at all: the iterative version is genuinely
 * hard, and this one is almost a restatement of the problem.
 *
 *     to move n disks from A to C using B:
 *         move n-1 disks from A to B   (using C)
 *         move disk n from A to C
 *         move n-1 disks from B to C   (using A)
 *
 * Run:  printf '3\n' | java TowerOfHanoi
 *
 * @author  Divesh Agarwal
 * @since   2026-09-12
 */
public class TowerOfHanoi {

    private static long moveCount;

    public static void main(String[] args) {
        System.out.println("Day 30 - Tower of Hanoi");
        System.out.println();

        int n;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Number of disks: ");
            n = sc.hasNextInt() ? sc.nextInt() : 3;
        }
        if (n < 1 || n > 20) {
            System.out.println("  (out of range, using 3)");
            n = 3;
        }

        System.out.println();
        theSolution(n);
        System.out.println();
        proveEveryMoveIsLegal(n);
        System.out.println();
        theRecurrence();
    }

    /**
     * The whole algorithm. The base case is n == 0 — no disks, nothing to do —
     * which is tidier than n == 1 and removes a special case.
     *
     * Note the argument shuffle on each call: the peg that was the DESTINATION
     * becomes the SPARE, and vice versa. That rotation is the entire trick.
     */
    static void solve(int n, char from, char to, char via) {
        if (n == 0) {
            return;                                  // BASE CASE
        }
        solve(n - 1, from, via, to);                 // clear the way
        moveCount++;
        System.out.println("    move disk " + n + ":  " + from + " -> " + to);
        solve(n - 1, via, to, from);                 // bring them back on top
    }

    /** Same recursion without the printing, for counting. */
    static long countMoves(int n) {
        return n == 0 ? 0 : countMoves(n - 1) + 1 + countMoves(n - 1);
    }

    private static void theSolution(int n) {
        System.out.println("-- Solving " + n + " disks, A -> C --");
        moveCount = 0;
        solve(n, 'A', 'C', 'B');
        System.out.println();
        System.out.println("  " + moveCount + " moves, and 2^" + n + " - 1 = "
                + ((1L << n) - 1));
    }

    /**
     * The printed moves are only convincing if they are LEGAL. This replays
     * them against three real stacks and checks the invariant on every step:
     * a disk may never land on a smaller one.
     */
    private static void proveEveryMoveIsLegal(int n) {
        Simulator sim = new Simulator(n);
        sim.run(n, 0, 2, 1);

        System.out.println("-- Verifying the moves --");
        System.out.println("  disks              : " + n);
        System.out.println("  moves made         : " + sim.moves);
        System.out.println("  expected 2^n - 1   : " + ((1L << n) - 1));
        System.out.println("  illegal placements : " + sim.illegal);
        System.out.println("  max recursion depth: " + sim.maxDepth);
        System.out.println("  final state        : " + sim.describe());
        System.out.println();
        System.out.println("  " + (sim.isSolved() && sim.illegal == 0
                ? "solved, with no larger disk ever placed on a smaller one."
                : "SOMETHING IS WRONG."));
    }

    /** Three pegs as stacks, so the rules can actually be checked. */
    private static final class Simulator {

        private final Deque<Integer>[] pegs;
        private final int disks;
        long moves;
        int illegal;
        int depth;
        int maxDepth;

        @SuppressWarnings("unchecked")
        Simulator(int n) {
            disks = n;
            pegs = new Deque[] { new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>() };
            for (int d = n; d >= 1; d--) {
                pegs[0].push(d);                     // largest at the bottom
            }
        }

        void run(int n, int from, int to, int via) {
            if (n == 0) {
                return;
            }
            depth++;
            maxDepth = Math.max(maxDepth, depth);

            run(n - 1, from, via, to);

            int disk = pegs[from].pop();
            if (!pegs[to].isEmpty() && pegs[to].peek() < disk) {
                illegal++;                           // the rule being tested
            }
            pegs[to].push(disk);
            moves++;

            run(n - 1, via, to, from);
            depth--;
        }

        boolean isSolved() {
            return pegs[0].isEmpty() && pegs[1].isEmpty() && pegs[2].size() == disks;
        }

        String describe() {
            return "A=" + pegs[0] + " B=" + pegs[1] + " C=" + pegs[2];
        }
    }

    private static void theRecurrence() {
        System.out.println("-- The recurrence --");
        System.out.println("    T(0) = 0");
        System.out.println("    T(n) = T(n-1) + 1 + T(n-1)  =  2*T(n-1) + 1");
        System.out.println();
        System.out.println("  Unrolling gives T(n) = 2^n - 1:");
        System.out.printf("  %6s %12s %14s%n", "n", "T(n)", "2^n - 1");
        for (int n = 1; n <= 6; n++) {
            System.out.printf("  %6d %12d %14d%n", n, countMoves(n), (1L << n) - 1);
        }
        System.out.println();
        System.out.println("  And this is OPTIMAL, not merely what the algorithm does.");
        System.out.println("  The largest disk must move at least once, and before it");
        System.out.println("  can, the other n-1 must all be on the spare peg — which");
        System.out.println("  is itself the same problem. So T(n) >= 2*T(n-1) + 1 for");
        System.out.println("  ANY correct solution.");
        System.out.println();
        System.out.println("  The legend uses 64 disks:");
        System.out.println("    2^64 - 1 = " + Long.toUnsignedString(-1L) + " moves");
        System.out.println("    at one move per second, about 585 billion years.");
    }
}
