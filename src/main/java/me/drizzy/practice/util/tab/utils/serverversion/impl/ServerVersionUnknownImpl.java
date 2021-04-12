package me.drizzy.practice.util.tab.utils.serverversion.impl;

import me.drizzy.practice.util.tab.utils.serverversion.*;
import org.bukkit.entity.*;


public class ServerVersionUnknownImpl implements IServerVersion {

    @Override
    public void clearArrowsFromPlayer(Player player) {

    }

    @Override
    public String getPlayerLanguage(Player player) {
        return "en";
    }
}
