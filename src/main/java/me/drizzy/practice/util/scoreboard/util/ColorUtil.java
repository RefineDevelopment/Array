package me.blazingtide.pistol.util;

import org.bukkit.ChatColor;

public class ColorUtil {

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
