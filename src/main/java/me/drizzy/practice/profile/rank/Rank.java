package me.drizzy.practice.profile.rank;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.rank.apis.*;
import org.bukkit.Bukkit;

public class Rank {

    public Rank() {
     preLoad();
    }

    public static void preLoad() {
        if (Bukkit.getPluginManager().getPlugin("AquaCore") != null) {
            Array.getInstance().setRankManager(new AquaCore());
            Array.logger("&bFound AquaCore! Hooking in...");
        } else if (Bukkit.getPluginManager().getPlugin("MizuCore") != null) {
            Array.getInstance().setRankManager(new MizuCore());
            Array.logger("&bFound MizuCore! Hooking in...");
        } else if (Bukkit.getPluginManager().getPlugin("HestiaCore") != null) {
            Array.getInstance().setRankManager(new HestiaCore());
            Array.logger("&bFound HestiaCore! Hooking in...");
        } else if (Bukkit.getPluginManager().getPlugin("ZoomCore") != null) {
            Array.getInstance().setRankManager(new ZoomCore());
            Array.logger("&bFound ZoomCore! Hooking in...");
        } else {
            Array.getInstance().setRankManager(new DefaultProvider());
        }
    }
}
