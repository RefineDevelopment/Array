package me.array.ArrayPractice.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackBuilder
{
    private ItemStack itemStack;
    private ItemMeta itemMeta;
    
    public ItemStackBuilder(final Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }
    
    public ItemStackBuilder amount(final int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }
    
    public ItemStackBuilder setName(final String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }
    
    public ItemStackBuilder addLore(final String... strings) {
        final List<String> loreArray = new ArrayList<String>();
        for (final String loreBit : strings) {
            loreArray.add(CC.WHITE + loreBit);
        }
        this.itemMeta.setLore((List)loreArray);
        return this;
    }
    
    public ItemStackBuilder enchant(final Enchantment enchanement, final int level, final boolean ignoreLevelRestriction) {
        this.itemMeta.addEnchant(enchanement, level, ignoreLevelRestriction);
        return this;
    }
    
    public ItemStackBuilder durability(final int durability) {
        this.itemStack.setDurability((short)durability);
        return this;
    }
    
    public ItemStack build() {
        final ItemStack clonedStack = this.itemStack.clone();
        clonedStack.setItemMeta(this.itemMeta.clone());
        return clonedStack;
    }
}
