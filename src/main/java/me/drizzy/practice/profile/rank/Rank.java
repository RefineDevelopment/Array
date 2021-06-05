package me.drizzy.practice.profile.rank;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.rank.apis.*;
import org.bukkit.Bukkit;

public class Rank {

    public static void preLoad() {
        if (Bukkit.getPluginManager().getPlugin("AquaCore") != null) {
            Array.getInstance().setRankManager(new AquaCore());
            Array.logger("&7Found AquaCore! Hooking in...");
            Array.logger("&aSucessfully hooked into AquaCore!");
        } else if (Bukkit.getPluginManager().getPlugin("MizuCore") != null) {
            Array.getInstance().setRankManager(new MizuCore());
            Array.logger("&7Found MizuCore! Hooking in...");
            Array.logger("&aSucessfully hooked into MizuCore!");
        } else if (Bukkit.getPluginManager().getPlugin("HestiaCore") != null) {
            Array.getInstance().setRankManager(new HestiaCore());
            Array.logger("&7Found HestiaCore! Hooking in...");
            Array.logger("&aSucessfully hooked into HestiaCore!");
        } else if (Bukkit.getPluginManager().getPlugin("ZoomCore") != null) {
            Array.getInstance().setRankManager(new ZoomCore());
            Array.logger("&7Found ZoomCore! Hooking in...");
            Array.logger("&aSucessfully hooked into ZoomCore!");
        } else {
            Array.getInstance().setRankManager(new DefaultProvider());
        }
    }
}
