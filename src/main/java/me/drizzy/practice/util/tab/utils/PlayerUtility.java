package me.drizzy.practice.util.tab.utils;


import me.drizzy.practice.util.tab.utils.playerversion.*;
import me.drizzy.practice.util.tab.utils.serverversion.*;
import org.bukkit.entity.*;

public class PlayerUtility {

    /**
     * Get the Player's Language Locale
     */
    public static String getPlayerLanguage(Player player) {
        return ServerVersionHandler.version.getPlayerLanguage(player);
    }

    /**
     * Clears the arrows from the Player's body
     */
    public static void clearArrowsFromPlayer(Player player) {
        ServerVersionHandler.version.clearArrowsFromPlayer(player);
    }

    /**
     * Get the Player's current protocol
     */
    public static PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersionHandler.version.getPlayerVersion(player);
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

}
