package xyz.refinedev.practice.util.other;

import xyz.refinedev.practice.Array;

public class Description {

    public static String getVersion() {
        return Array.getInstance().getDescription().getVersion();
    }

    public static String getAuthor() {
        return Array.getInstance().getDescription().getAuthors().toString().replace("[", "").replace("]", "");
    }

    public static String getName() {
        return Array.getInstance().getDescription().getName();
    }

    public static String getWebsite() {
        return Array.getInstance().getDescription().getWebsite();
    }
}
