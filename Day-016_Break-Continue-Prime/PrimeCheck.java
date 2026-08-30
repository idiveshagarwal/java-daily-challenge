import java.util.Scanner;

/**
 * Day 16 — break and continue, via the prime check.
 *
 * A primality test is the textbook case for `break`: the moment a divisor is
 * found there is nothing left to learn, so continuing to loop is wasted work.
 *
 * But `break` only helps when a divisor EXISTS. For an actual prime there is
 * nothing to break out of, and the loop runs to the bound regardless — which
 * is why the bound itself is the more important optimisation. Both are
 * measured below.
 *
 * Run:  printf '999983\n' | java PrimeCheck
 *
 * @author  Divesh Agarwal
 * @since   2026-08-29
 */
public class PrimeCheck {

    /** Counts loop iterations so the approaches can be compared honestly. */
    private static long iterations;

    public static void main(String[] args) {
        System.out.println("Day 16 — Prime check (break and continue)");

        int n;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\nNumber to test: ");
            n = sc.hasNextInt() ? sc.nextInt() : 999983;
        }

        System.out.println();
        System.out.println("  " + n + " is " + (isPrime(n) ? "PRIME" : "NOT prime"));
        System.out.println();

        compareApproaches(n);
        System.out.println();
        listPrimesWithContinue(50);
        System.out.println();
        verifyAgainstSieve(10_000);
    }

    // ── the four versions ──────────────────────────────────────────────────

    /** No break: keeps looping after the answer is known. */
    static boolean noBreak(int n) {
        if (n < 2) {
            return false;
        }
        boolean prime = true;
        for (int i = 2; i < n; i++) {
            iterations++;
            if (n % i == 0) {
                prime = false;          // answer known, but the loop continues
            }
        }
        return prime;
    }

    /** With break: stops at the first divisor. Here `return` IS the break. */
    static boolean withBreak(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i < n; i++) {
            iterations++;
            if (n % i == 0) {
                return false;           // leaves the loop AND the method
            }
        }
        return true;
    }

    /** Half the range: no divisor of n can exceed n/2 (other than n itself). */
    static boolean toHalf(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= n / 2; i++) {
            iterations++;
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * The real optimisation. If n = a * b then one of a, b is <= sqrt(n), so
     * checking past the square root can never find a new divisor.
     *
     * Written as `i * i <= n` rather than `i <= Math.sqrt(n)` to stay in
     * integer arithmetic — no floating-point rounding at the boundary (Day 8).
     * The cast to long stops i * i overflowing for large n (Day 13).
     */
    static boolean isPrime(int n) {
        if (n < 2) {
            return false;               // 0, 1 and negatives are not prime
        }
        if (n % 2 == 0) {
            return n == 2;              // 2 is the only even prime
        }
        for (int i = 3; (long) i * i <= n; i += 2) {   // odd divisors only
            iterations++;
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // ── measurements ───────────────────────────────────────────────────────

    /**
     * The point of the day. `break` and the loop bound optimise DIFFERENT
     * cases, and comparing them on one number hides that — so both a prime and
     * a composite are measured.
     */
    private static void compareApproaches(int n) {
        System.out.println("── Iterations to decide " + n + " ──");
        System.out.printf("  %-22s %14s %10s%n", "approach", "iterations", "result");

        report("no break (i < n)", () -> noBreak(n));
        report("break (i < n)", () -> withBreak(n));
        report("i <= n/2", () -> toHalf(n));
        report("i*i <= n, odds only", () -> isPrime(n));

        int composite = n + 1;
        System.out.println();
        System.out.println("── The same two on a composite (" + composite + ") ──");
        report("no break", () -> noBreak(composite));
        report("break", () -> withBreak(composite));

        System.out.println();
        System.out.println("  break collapses the composite case to almost nothing,");
        System.out.println("  and does NOTHING for a prime — there is no divisor to");
        System.out.println("  break on, so the loop runs to the bound either way.");
        System.out.println("  Only the bound helps the worst case.");
    }

    private static void report(String label, java.util.function.BooleanSupplier test) {
        iterations = 0;
        boolean result = test.getAsBoolean();
        System.out.printf("  %-22s %14d %10s%n", label, iterations, result);
    }

    /**
     * `continue` reads well as a filter: skip the values you do not care
     * about, leaving the body to handle only the interesting ones.
     */
    private static void listPrimesWithContinue(int limit) {
        System.out.println("── continue as a filter ──");
        System.out.print("  primes below " + limit + ": ");

        for (int candidate = 2; candidate < limit; candidate++) {
            if (!isPrime(candidate)) {
                continue;               // skip to the next candidate
            }
            System.out.print(candidate + " ");
        }

        System.out.println();
        System.out.println("  Equivalent to wrapping the body in `if (isPrime(c))`.");
        System.out.println("  continue is clearer when the body is long or the skip");
        System.out.println("  conditions are several.");
    }

    /**
     * Correctness is not something to assume from three examples (Day 11).
     * A Sieve of Eratosthenes is a completely different algorithm, so agreeing
     * with it is real evidence rather than a shared bug.
     */
    private static void verifyAgainstSieve(int limit) {
        boolean[] composite = new boolean[limit + 1];
        for (int i = 2; (long) i * i <= limit; i++) {
            if (composite[i]) {
                continue;
            }
            for (int mark = i * i; mark <= limit; mark += i) {
                composite[mark] = true;
            }
        }

        int mismatches = 0;
        int primes = 0;
        for (int n = 0; n <= limit; n++) {
            boolean expected = n >= 2 && !composite[n];
            if (isPrime(n) != expected) {
                mismatches++;
                System.out.println("  MISMATCH at " + n);
            }
            if (expected) {
                primes++;
            }
        }

        System.out.println("── Verified against a sieve ──");
        System.out.println("  checked 0.." + limit + ", found " + primes + " primes");
        System.out.println("  mismatches: " + mismatches);
        System.out.println(mismatches == 0
                ? "  isPrime agrees with the sieve on every value."
                : "  SOMETHING IS WRONG.");
    }
}
