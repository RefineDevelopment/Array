package me.array.ArrayPractice.profile.options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum OptionsType {

    TOGGLESCOREBOARD("&b&lToggle Scoreboard", Material.PAINTING),
    TOGGLEDUELREQUESTS("&b&lToggle Duel Requests", Material.DIAMOND_SWORD),
    TOGGLESPECTATORS("&b&lToggle Spectators", Material.ENDER_PEARL),
    TOGGLELIGHTNING("&b&lToggle Lightning", Material.BLAZE_ROD),
    CORESETTINGS("&b&lView Core Settings",Material.EMERALD),
    TOGGLEPINGONSCOREBOARD("&b&lToggle Ping on Scoreboard", Material.STRING),
    TOGGLEPINGFACTOR("&b&lToggle Ping Factor", Material.BOOK),
    TOGGLETOURNAMENTMESSAGES("&b&lToggle Tournament Messages", Material.PAPER),
    TOGGLEPLAYERVISIBILITY("&b&lToggle Player Visibility", Material.REDSTONE_COMPARATOR);

    private final String name;
    private final Material material;
}
