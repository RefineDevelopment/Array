package me.array.ArrayPractice.profile.options;

import java.beans.ConstructorProperties;
import org.bukkit.Material;

public enum OptionsType
{
    TOGGLESCOREBOARD("&b&lToggle Scoreboard", Material.PAINTING),
    TOGGLEDUELREQUESTS("&b&lToggle Duel Requests", Material.DIAMOND_SWORD),
    TOGGLESPECTATORS("&b&lToggle Spectators", Material.ENDER_PEARL),
    TOGGLELIGHTNING("&b&lToggle Lightning", Material.BLAZE_ROD),
    TOGGLEPMS("&b&lToggle Private Messages", Material.BOOK);
    
    private String name;
    private Material material;
    
    @ConstructorProperties({ "name", "material" })
    private OptionsType(final String name, final Material material) {
        this.name = name;
        this.material = material;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Material getMaterial() {
        return this.material;
    }
}
