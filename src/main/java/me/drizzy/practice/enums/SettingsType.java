package me.drizzy.practice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum SettingsType {

    TOGGLESCOREBOARD("&c&lToggle Scoreboard", Material.PAINTING),
    TOGGLEDUELREQUESTS("&c&lToggle Duel Requests", Material.DIAMOND_SWORD),
    TOGGLESPECTATORS("&c&lToggle Spectators", Material.ENDER_PEARL),
    TOGGLELIGHTNING("&c&lToggle Lightning", Material.BLAZE_ROD),
    CORESETTINGS("&c&lView Core Settings",Material.EMERALD),
    TOGGLEPINGONSCOREBOARD("&c&lToggle Ping on Scoreboard", Material.STRING),
    TOGGLECPSONSCOREBOARD("&c&lToggle CPS on Scoreboard", Material.BEACON),
    TOGGLEPINGFACTOR("&c&lToggle Ping Factor", Material.BOOK),
    TOGGLETOURNAMENTMESSAGES("&c&lToggle Tournament Messages", Material.PAPER),
    TOGGLESHOWPLAYERS("&c&lToggle Player Visibility", Material.LEVER),
    TOGGLEVANILLATAB("&c&lToggle Vanilla Tab", Material.REDSTONE_COMPARATOR);

    private final String name;
    private final Material material;
}
