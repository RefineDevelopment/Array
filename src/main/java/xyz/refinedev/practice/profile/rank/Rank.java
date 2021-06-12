package xyz.refinedev.practice.profile.rank;

import lombok.Getter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.profile.rank.apis.*;
import org.bukkit.Bukkit;
import xyz.refinedev.practice.profile.rank.apis.*;

public class Rank {

    @Getter public static RankType rankType;

    public static void preLoad() {
        if (Essentials.getMeta().isCoreHookEnabled()) {
            if (Bukkit.getPluginManager().getPlugin("AquaCore") != null) {
                rankType = new AquaCore();
                Array.logger("&7Found AquaCore! Hooking in...");
                Array.logger("&aSucessfully hooked into AquaCore!");
            } else if (Bukkit.getPluginManager().getPlugin("MizuCore") != null) {
                rankType = new MizuCore();
                Array.logger("&7Found MizuCore! Hooking in...");
                Array.logger("&aSucessfully hooked into MizuCore!");
            } else if (Bukkit.getPluginManager().getPlugin("HestiaCore") != null) {
                rankType = new HestiaCore();
                Array.logger("&7Found HestiaCore! Hooking in...");
                Array.logger("&aSucessfully hooked into HestiaCore!");
            } else if (Bukkit.getPluginManager().getPlugin("ZoomCore") != null) {
                rankType = new ZoomCore();
                Array.logger("&7Found ZoomCore! Hooking in...");
                Array.logger("&aSucessfully hooked into ZoomCore!");
            } else {
                rankType = new DefaultProvider();
            }
        } else {
            rankType = new DefaultProvider();
        }
    }
}
