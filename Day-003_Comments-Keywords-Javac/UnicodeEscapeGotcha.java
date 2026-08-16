/**
 * The one case where a comment is NOT inert.
 *
 * javac processes Unicode escapes in a pass BEFORE the lexer splits the source
 * into tokens. So \u000A becomes a real line terminator before anything knows
 * what a comment is — which ends the // comment early and leaves the rest of
 * the line as live code.
 *
 * This is why you should never write \u000A, \u000D or \u0022 inside a comment.
 */
public class UnicodeEscapeGotcha {

    public static void main(String[] args) {
        System.out.println("1. before the comment");

        // this comment is ended early by the escape -> \u000A System.out.println("2. THIS RAN, from inside a // comment");

        System.out.println("3. after the comment");
    }
}
