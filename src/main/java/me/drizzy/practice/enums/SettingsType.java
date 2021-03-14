package me.drizzy.practice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum SettingsType {

    TOGGLESCOREBOARD("&b&lToggle Scoreboard", Material.PAINTING),
    TOGGLEDUELREQUESTS("&b&lToggle Duel Requests", Material.DIAMOND_SWORD),
    TOGGLESPECTATORS("&b&lToggle Spectators", Material.ENDER_PEARL),
    TOGGLELIGHTNING("&b&lToggle Lightning", Material.BLAZE_ROD),
    CORESETTINGS("&b&lView Core Settings",Material.EMERALD),
    TOGGLEPINGONSCOREBOARD("&b&lToggle Ping on Scoreboard", Material.STRING),
    TOGGLEPINGFACTOR("&b&lToggle Ping Factor", Material.BOOK),
    TOGGLETOURNAMENTMESSAGES("&b&lToggle Tournament Messages", Material.PAPER),
    TOGGLESHOWPLAYERS("&b&lToggle Player Visibility", Material.LEVER),
    TOGGLEVANILLATAB("&b&lToggle Vanilla Tab", Material.REDSTONE_COMPARATOR);

    private final String name;
    private final Material material;
}
