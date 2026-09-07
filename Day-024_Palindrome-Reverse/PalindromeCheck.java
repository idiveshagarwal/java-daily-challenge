import java.util.Random;
import java.util.Scanner;

/**
 * Day 24, part 2 — palindrome checking.
 *
 * The obvious solution is "reverse it and compare". The better one never
 * builds a second string at all: walk two pointers inward from both ends.
 *
 * The harder half of the problem is not the loop — it is deciding what
 * "the same backwards" means. "A man, a plan, a canal: Panama" is a palindrome
 * only after you agree to ignore case, spaces and punctuation.
 *
 * Run:  printf 'A man, a plan, a canal: Panama\n' | java PalindromeCheck
 *
 * @author  Divesh Agarwal
 * @since   2026-09-06
 */
public class PalindromeCheck {

    public static void main(String[] args) {
        System.out.println("Day 24 - Palindrome check");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Text: ");
            String input = sc.hasNextLine() ? sc.nextLine() : "A man, a plan, a canal: Panama";

            System.out.println();
            System.out.println("  input          [" + input + "]");
            System.out.println("  strict         " + isPalindromeStrict(input));
            System.out.println("  normalised     " + isPalindrome(input));
            System.out.println("  normalises to  [" + normalise(input) + "]");
        }

        System.out.println();
        theTwoApproaches();
        normalisationMatters();
        edgeCases();
        verifyBothAgree();
    }

    /**
     * Two pointers, no allocation. Compares the ends and walks inward,
     * returning false at the first mismatch (Day 16's break).
     *
     * O(n) time, O(1) space — and it exits early, where reverse-and-compare
     * always builds the whole reversed string first.
     */
    public static boolean isPalindromeStrict(String s) {
        for (int i = 0, j = s.length() - 1; i < j; i++, j--) {
            if (s.charAt(i) != s.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /** The obvious version, for comparison. Allocates a whole second string. */
    public static boolean byReversing(String s) {
        return s.equals(new StringBuilder(s).reverse().toString());
    }

    /** Ignores case and anything that is not a letter or digit. */
    public static boolean isPalindrome(String s) {
        return isPalindromeStrict(normalise(s));
    }

    /**
     * Keeps letters and digits, lowercased. Character.isLetterOrDigit is
     * Unicode-aware, so accented letters survive.
     */
    public static String normalise(String s) {
        StringBuilder kept = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                kept.append(Character.toLowerCase(c));
            }
        }
        return kept.toString();
    }

    /**
     * A two-pointer scan that normalises as it goes — no second string at all,
     * genuinely O(1) extra space even for messy input.
     */
    public static boolean isPalindromeInPlace(String s) {
        int i = 0;
        int j = s.length() - 1;

        while (i < j) {
            while (i < j && !Character.isLetterOrDigit(s.charAt(i))) {
                i++;
            }
            while (i < j && !Character.isLetterOrDigit(s.charAt(j))) {
                j--;
            }
            if (Character.toLowerCase(s.charAt(i)) != Character.toLowerCase(s.charAt(j))) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

    private static void theTwoApproaches() {
        System.out.println("-- Two approaches --");
        System.out.println("  reverse & compare : O(n) time, O(n) space, always full work");
        System.out.println("  two pointers      : O(n) time, O(1) space, exits at the");
        System.out.println("                      first mismatch");
        System.out.println();
        System.out.println("  On \"abcdef\" the two-pointer version compares ONE pair");
        System.out.println("  and returns; reversing builds all six characters first.");
        System.out.println();
    }

    private static void normalisationMatters() {
        String[] samples = {
                "racecar",
                "RaceCar",
                "A man, a plan, a canal: Panama",
                "No 'x' in Nixon",
                "hello"
        };

        System.out.println("-- Normalisation --");
        System.out.printf("  %-32s %8s %12s%n", "input", "strict", "normalised");
        for (String s : samples) {
            System.out.printf("  %-32s %8s %12s%n",
                    "[" + s + "]", isPalindromeStrict(s), isPalindrome(s));
        }
        System.out.println();
        System.out.println("  Only \"racecar\" passes strictly. The rest need case");
        System.out.println("  folding and punctuation removal before the question even");
        System.out.println("  makes sense.");
        System.out.println();
    }

    private static void edgeCases() {
        System.out.println("-- Edge cases --");
        System.out.println("  \"\"       -> " + isPalindromeStrict("") + "    (vacuously: no pair differs)");
        System.out.println("  \"a\"      -> " + isPalindromeStrict("a") + "    (single character)");
        System.out.println("  \"aa\"     -> " + isPalindromeStrict("aa") + "    (even length)");
        System.out.println("  \"aba\"    -> " + isPalindromeStrict("aba") + "    (odd length, middle unchecked)");
        System.out.println("  \",,,\"    -> " + isPalindrome(",,,") + "    (normalises to empty)");
        System.out.println();
        System.out.println("  The loop condition is i < j, not i <= j: on odd lengths");
        System.out.println("  the middle character never needs comparing with itself.");
        System.out.println();
    }

    /**
     * Three implementations that should always agree. Random strings over a
     * tiny alphabet produce plenty of real palindromes by chance, so both
     * answers get exercised.
     */
    private static void verifyBothAgree() {
        Random rng = new Random(11);
        int trials = 5000;
        int mismatches = 0;
        int palindromes = 0;

        for (int t = 0; t < trials; t++) {
            int len = rng.nextInt(10);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append((char) ('a' + rng.nextInt(2)));      // only 'a' and 'b'
            }
            String s = sb.toString();

            boolean strict = isPalindromeStrict(s);
            boolean reversed = byReversing(s);
            boolean inPlace = isPalindromeInPlace(s);

            if (strict != reversed || strict != inPlace) {
                mismatches++;
                System.out.println("  MISMATCH [" + s + "]");
            }
            if (strict) {
                palindromes++;
            }
        }

        System.out.println("-- Verified --");
        System.out.println("  " + trials + " random strings over {a,b}, lengths 0..9");
        System.out.println("  " + palindromes + " were palindromes, so both branches were exercised");
        System.out.println("  mismatches: " + mismatches);
        System.out.println(mismatches == 0
                ? "  two-pointer, reverse-and-compare and in-place all agree."
                : "  SOMETHING IS WRONG.");
    }
}
