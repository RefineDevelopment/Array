package me.drizzy.practice.party;

import java.beans.ConstructorProperties;
import org.bukkit.Material;

public enum PartyEvent
{
    FFA("&bFFA", Material.DIAMOND),
    SPLIT("&bSplit", Material.DIAMOND_SWORD),
    HCF("&bHCF", Material.GOLDEN_APPLE);

    private String name;
    private Material material;

    @ConstructorProperties({ "name", "lore", "material" })
    PartyEvent(final String name,final Material material) {
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
