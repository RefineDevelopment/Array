package me.drizzy.practice.util;

import java.util.Arrays;
import java.util.List;

import me.drizzy.practice.util.chat.CC;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(Material material, int n2, int n3) {
        if (n2 == 0) {
            this.itemStack = new ItemStack(material, 1, (short)n3);
            return;
        }
        this.itemStack = new ItemStack(material, n2, (short)n3);
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack, int n2) {
        if (n2 == 0) {
            this.itemStack = itemStack;
            return;
        }
        this.itemStack = itemStack;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.itemStack.getItemMeta();
        leatherArmorMeta.setColor(color);
        this.itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        return this;
    }

    public ItemBuilder setLeatherUnbreakable() {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.itemStack.getItemMeta();
        leatherArmorMeta.spigot().setUnbreakable(true);
        this.itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int n2) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, n2, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int n2, boolean bl2) {
        if (bl2) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.addEnchant(enchantment, n2, true);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemBuilder addEnchantLeather(Enchantment enchantment, int n2, boolean bl2) {
        if (bl2) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.itemStack.getItemMeta();
            leatherArmorMeta.addEnchant(enchantment, n2, true);
            this.itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        }
        return this;
    }

    public ItemBuilder setUnbreakableLeather() {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.itemStack.getItemMeta();
        leatherArmorMeta.spigot().setUnbreakable(true);
        this.itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        return this;
    }

    public ItemBuilder setColorLeather(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.itemStack.getItemMeta();
        leatherArmorMeta.setColor(color);
        this.itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        return this;
    }

    public ItemBuilder addUnsafeEnchant(Enchantment enchantment, int n2) {
        this.itemStack.addUnsafeEnchantment(enchantment, n2);
        return this;
    }

    public ItemBuilder setDisplayName(String string) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(CC.translate(string));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(String ... arrstring) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(Arrays.asList(arrstring));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(List<String> list) {
        if (list.size() == 0) {
            return this;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        for (int i2 = 0; i2 < list.size(); ++i2) {
            String string = list.get(i2).replace("&", "\u00a7");
            list.set(i2, string);
        }
        itemMeta.setLore(list);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}

