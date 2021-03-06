package team.unnamed.molang.ast;

/**
 * Utility class holding utility static
 * methods for working with character
 * tokens
 */
public final class Tokens {

    /**
     * Character used to escape other characters
     * to consider them special characters or
     * non-tokens
     */
    public static final char ESCAPE = '\\';
    public static final char UNDERSCORE = '_';
    public static final char HYPHEN = '-';
    public static final char DOT = '.';
    public static final char AMPERSAND = '&';
    public static final char EXCLAMATION = '!';
    public static final char EQUAL = '=';

    // I don't know the name of this character, it's just a line
    public static final char LINE = '|';

    // MoLang currently only supports single quotes for string
    public static final char QUOTE = '\'';

    private Tokens() {
    }

    public static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    public static boolean isLetter(int c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    public static boolean isDigit(int c) {
        return Character.isDigit(c);
    }

    public static boolean isValidForIdentifier(int c) {
        return isLetter(c) || c == UNDERSCORE;
    }

    public static boolean isValidIdentifierContinuation(int c) {
        return isValidForIdentifier(c) || isDigit(c);
    }

}
