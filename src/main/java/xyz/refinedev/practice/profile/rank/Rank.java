package xyz.refinedev.practice.profile.rank;

import lombok.Getter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.rank.apis.*;
import org.bukkit.Bukkit;

public class Rank {

    @Getter public static RankType rankType;
    private static final Array plugin = Array.getInstance();

    public static void preLoad() {
        if (plugin.getConfigHandler().isCORE_HOOK_ENABLED()) {
            if (Bukkit.getPluginManager().getPlugin("AquaCore") != null) {
                rankType = new AquaCore();
            } else if (Bukkit.getPluginManager().getPlugin("MizuCore") != null) {
                rankType = new MizuCore();
            } else if (Bukkit.getPluginManager().getPlugin("HestiaCore") != null) {
                rankType = new HestiaCore();
            } else if (Bukkit.getPluginManager().getPlugin("ZoomCore") != null) {
                rankType = new ZoomCore();
            } else {
                rankType = new DefaultProvider();
            }
        } else {
            rankType = new DefaultProvider();
        }
    }
}
