package me.drizzy.practice.util.chat;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Color {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public List<String> translateFromArray(List<String> text) {
        List<String> messages = new ArrayList<>();

        for (String string : text) {
            messages.add(translate(string));
        }
        return messages;
    }

}
