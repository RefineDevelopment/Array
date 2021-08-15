package me.drizzy.practice.util.chat;

import org.bukkit.ChatColor;

public enum ColourUtils {

    BLACK("BLACK", "&0", ChatColor.BLACK, 15),
    DARK_BLUE("DARK_BLUE", "&1", ChatColor.DARK_BLUE, 11),
    DARK_GREEN("DARK_GREEN", "&2", ChatColor.DARK_GREEN, 13),
    DARK_AQUA("DARK_AQUA", "&3", ChatColor.DARK_AQUA, 9),
    DARK_RED("DARK_RED", "&4", ChatColor.DARK_RED, 14),
    DARK_PURPLE("DARK_PURPLE", "&5", ChatColor.DARK_PURPLE, 10),
    GOLD("GOLD", "&6", ChatColor.GOLD, 4),
    GRAY("GRAY", "&7", ChatColor.GRAY, 8),
    DARK_GRAY("DARK_GRAY", "&8", ChatColor.DARK_GRAY, 7),
    BLUE("BLUE", "&9", ChatColor.BLUE, 9),
    GREEN("GREEN", "&a", ChatColor.GREEN, 5),
    AQUA("AQUA", "&b", ChatColor.AQUA, 3),
    RED("RED", "&c", ChatColor.RED, 14),
    LIGHT_PURPLE("LIGHT_PURPLE", "&d", ChatColor.LIGHT_PURPLE, 2),
    YELLOW("YELLOW", "&e", ChatColor.YELLOW, 4),
    WHITE("WHITE", "&f", ChatColor.WHITE, 0),;

    private final String name;
    private final String input;
    private final ChatColor minecraftColor;
    private final int woolColor;

    ColourUtils(String name, String input, ChatColor minecraftColor, int woolColor) {
        this.name = name;
        this.input = input;
        this.minecraftColor = minecraftColor;
        this.woolColor = woolColor;
    }

    public int getWoolColor() {
        return woolColor;
    }

    public ChatColor getMinecraftColor() {
        return this.minecraftColor;
    }

    public String getInput() {
        return this.input;
    }

    public String getName() {
        return this.name;
    }

    public static ChatColor format(String message) {
        for (ColourUtils c : values()) {
            if(message.equalsIgnoreCase(c.getName()) || message.equalsIgnoreCase(c.getInput())){
                return c.getMinecraftColor();
            }
        }
        return null;
    }

    public static int getWoolColor(ChatColor chatColor) {
        for (ColourUtils c : values()) {
            if(chatColor == c.getMinecraftColor()){
                return c.getWoolColor();
            }
        }
        return 8;
    }
}