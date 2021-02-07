package me.drizzy.practice.util.external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextSplitter {

    public static List<String> split(int length, String text, String prefix) {
        if (text.length() <= length) {
            return Collections.singletonList(prefix + text);
        }

        List<String> lines = new ArrayList<>();
        String[] split = text.split(" ");
        StringBuilder builder = new StringBuilder(prefix);

        for (int i = 0; i < split.length; ++i) {
            if (builder.length() + split[i].length() >= length) {
                lines.add(builder.toString());
                builder = new StringBuilder(prefix);
            }

            builder.append(split[i]);
            builder.append(" ");
        }

        if (builder.length() != 0) {
            lines.add(builder.toString());
        }

        return lines;
    }

}
