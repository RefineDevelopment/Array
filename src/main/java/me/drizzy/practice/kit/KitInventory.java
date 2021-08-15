package me.drizzy.practice.kit;

import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Data
public class KitInventory {

    private String customName = "Default";
    private ItemStack[] armor;
    private ItemStack[] contents;
    private List<PotionEffect> effects;

    public KitInventory() {
        this.armor = new ItemStack[4];
        this.contents = new ItemStack[36];
        this.effects = new ArrayList<>();
    }

    public KitInventory(String customName) {
        this.customName = customName;
        this.armor = new ItemStack[4];
        this.contents = new ItemStack[36];
        this.effects = new ArrayList<>();
    }

    public KitInventory(ItemStack[] armor, ItemStack[] contents) {
        this.armor = armor;
        this.contents = contents;
        this.effects = new ArrayList<>();
    }

}
