package me.array.ArrayPractice.util;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class StringUtil {
    public static final String NO_PERMISSION;
    public static final String PLAYER_ONLY;
    public static final String PLAYER_NOT_FOUND;
    public static final String LOAD_ERROR;
    public static final String SPLIT_PATTERN;
    private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";
    private static final List<String> VOWELS;

    static {
        NO_PERMISSION = ChatColor.RED + "You don't enough permissions.";
        PLAYER_ONLY = ChatColor.RED + "Only players can use this command.";
        PLAYER_NOT_FOUND = ChatColor.RED + "%s not found.";
        LOAD_ERROR = ChatColor.RED + "An error occured, please contact an administrator.";
        SPLIT_PATTERN = Pattern.compile("\\s").pattern();
        VOWELS = Arrays.asList("a", "e", "u", "i", "o");
    }

    public static String buildString(final String[] args, final int start) {
        return String.join(" ", (CharSequence[])Arrays.copyOfRange(args, start, args.length));
    }

    private StringUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static String toNiceString(String string) {
        string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.toCharArray().length; ++i) {
            char c = string.toCharArray()[i];
            if (i > 0) {
                final char prev = string.toCharArray()[i - 1];
                if ((prev == ' ' || prev == '[' || prev == '(') && (i == string.toCharArray().length - 1 || c != 'x' || !Character.isDigit(string.toCharArray()[i + 1]))) {
                    c = Character.toUpperCase(c);
                }
            } else if (c != 'x' || !Character.isDigit(string.toCharArray()[i + 1])) {
                c = Character.toUpperCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String buildMessage(final String[] args, final int start) {
        if (start >= args.length) {
            return "";
        }
        return ChatColor.stripColor(String.join(" ", (CharSequence[]) Arrays.copyOfRange(args, start, args.length)));
    }

    public static String getFirstSplit(final String s) {
        return s.split(StringUtil.SPLIT_PATTERN)[0];
    }

    public static String getAOrAn(final String input) {
        return StringUtil.VOWELS.contains(input.substring(0, 1).toLowerCase()) ? "an" : "a";
    }
}
