

package me.array.ArrayPractice.party;

import java.beans.ConstructorProperties;
import org.bukkit.Material;

public enum PartyEvent
{
    FFA("&bFFA", "&7Let your party members fight for themselves", Material.DIAMOND),
    SPLIT("&bSplit", "&7Split your party in 2 teams and fight!", Material.DIAMOND_SWORD),
    HCF("&bHCF", "&7Split your party, and choose bard/diamond", Material.GOLDEN_APPLE),
    KOTH("&bKOTH", "&7Split your party, and cap a koth 5 times", Material.STRING);
    
    private String name;
    private String lore;
    private Material material;
    
    @ConstructorProperties({ "name", "lore", "material" })
    private PartyEvent(final String name, final String lore, final Material material) {
        this.name = name;
        this.lore = lore;
        this.material = material;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getLore() {
        return this.lore;
    }
    
    public Material getMaterial() {
        return this.material;
    }
}
