package me.drizzy.practice.enums;

import java.beans.ConstructorProperties;
import org.bukkit.Material;

public enum PartyEventType {
    FFA("&bFFA", Material.DIAMOND),
    SPLIT("&bSplit", Material.LEASH),
    HCF("&bHCF", Material.GOLDEN_APPLE);

    private final String name;
    private final Material material;

    @ConstructorProperties({ "name", "lore", "material" })
    PartyEventType(final String name, final Material material) {
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
