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
    TOGGLEPINGFACTOR("&b&lToggle Ping Factor", Material.BOOK);

    private final String name;
    private final Material material;
}
