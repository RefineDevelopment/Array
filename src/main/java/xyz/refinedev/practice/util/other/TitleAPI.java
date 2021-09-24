package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

import java.lang.reflect.Constructor;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author ConnorLinFoot
 * Project: TitleAPI
 */

@UtilityClass
public class TitleAPI {

    private final BasicConfigurationFile configHandler = Array.getInstance().getMainConfig();

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        if (player == null) return;
        try {
            Object e;
            Object chatTitle;
            Object chatSubtitle;
            Constructor subtitleConstructor;
            Object titlePacket;
            Object subtitlePacket;

            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
                sendPacket(player, titlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                titlePacket = subtitleConstructor.newInstance(e, chatTitle);
                sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMatchStart(Player player) {
        String path = "MATCH.TITLE.STARTED";
        if (!configHandler.contains(path)) return;
        if (!configHandler.getBoolean(path + "ENABLED")) return;

        int stay = configHandler.getInteger(path + "STAY", 5);
        int fadeIn = configHandler.getInteger(path + "FADE_IN", 20);
        int fadeOut = configHandler.getInteger(path + "FADE_OUT", 20);
        String text = configHandler.getString(path + "TEXT");
        String subtitle = configHandler.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            sendTitle(player, fadeIn, stay, fadeOut, text, subtitle);
        } else {
            sendTitle(player, fadeIn, stay, fadeOut, text, null);
        }
    }
    public void sendMatchCountdown(Player player) {
        String path = "MATCH.TITLE.COUNTDOWN";
        if (!configHandler.contains(path)) return;
        if (!configHandler.getBoolean(path + "ENABLED")) return;

        int stay = configHandler.getInteger(path + "STAY", 5);
        int fadeIn = configHandler.getInteger(path + "FADE_IN", 20);
        int fadeOut = configHandler.getInteger(path + "FADE_OUT", 20);
        String text = configHandler.getString(path + "TEXT");
        String subtitle = configHandler.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            sendTitle(player, fadeIn, stay, fadeOut, text, subtitle);
        } else {
            sendTitle(player, fadeIn, stay, fadeOut, text, null);
        }
    }
    public void sendMatchWinner(Player player) {
        String path = "MATCH.TITLE.WINNER";
        if (!configHandler.contains(path)) return;
        if (!configHandler.getBoolean(path + "ENABLED")) return;

        int stay = configHandler.getInteger(path + "STAY", 5);
        int fadeIn = configHandler.getInteger(path + "FADE_IN", 20);
        int fadeOut = configHandler.getInteger(path + "FADE_OUT", 20);
        String text = configHandler.getString(path + "TEXT");
        String subtitle = configHandler.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            sendTitle(player, fadeIn, stay, fadeOut, text, subtitle);
        } else {
            sendTitle(player, fadeIn, stay, fadeOut, text, null);
        }
    }
    public void sendMatchLoser(Player player) {
        String path = "MATCH.TITLE.LOSER";
        if (!configHandler.contains(path)) return;
        if (!configHandler.getBoolean(path + "ENABLED")) return;

        int stay = configHandler.getInteger(path + "STAY", 5);
        int fadeIn = configHandler.getInteger(path + "FADE_IN", 20);
        int fadeOut = configHandler.getInteger(path + "FADE_OUT", 20);
        String text = configHandler.getString(path + "TEXT");
        String subtitle = configHandler.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            sendTitle(player, fadeIn, stay, fadeOut, text, subtitle);
        } else {
            sendTitle(player, fadeIn, stay, fadeOut, text, null);
        }
    }

    public void clearTitle(Player player) {
        sendTitle(player, 0, 0, 0, "", "");
    }
}
