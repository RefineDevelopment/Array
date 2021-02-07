/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package me.drizzy.practice.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryRestore {
    private Player player;
    private ItemStack[] inventoryContents;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private int totalExp;
    private float exp;

    public InventoryRestore(Player player) {
        this.player = player;
        this.inventoryContents = player.getInventory().getContents();
        this.helmet = player.getInventory().getHelmet();
        this.chestplate = player.getInventory().getChestplate();
        this.leggings = player.getInventory().getLeggings();
        this.boots = player.getInventory().getBoots();
        this.totalExp = player.getTotalExperience();
        this.exp = player.getExp();
    }

    public void restore() {
        this.getPlayer().getInventory().setContents(this.inventoryContents);
        this.getPlayer().getInventory().setHelmet(this.helmet);
        this.getPlayer().getInventory().setChestplate(this.chestplate);
        this.getPlayer().getInventory().setLeggings(this.leggings);
        this.getPlayer().getInventory().setBoots(this.boots);
        this.getPlayer().setTotalExperience(this.totalExp);
        this.getPlayer().setExp(this.exp);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ItemStack[] getInventoryContents() {
        return this.inventoryContents;
    }

    public void setInventoryContents(ItemStack[] arritemStack) {
        this.inventoryContents = arritemStack;
    }

    public ItemStack getHelmet() {
        return this.helmet;
    }

    public void setHelmet(ItemStack itemStack) {
        this.helmet = itemStack;
    }

    public ItemStack getChestplate() {
        return this.chestplate;
    }

    public void setChestplate(ItemStack itemStack) {
        this.chestplate = itemStack;
    }

    public ItemStack getLeggings() {
        return this.leggings;
    }

    public void setLeggings(ItemStack itemStack) {
        this.leggings = itemStack;
    }

    public ItemStack getBoots() {
        return this.boots;
    }

    public void setBoots(ItemStack itemStack) {
        this.boots = itemStack;
    }

    public int getTotalExp() {
        return this.totalExp;
    }

    public void setTotalExp(int n2) {
        this.totalExp = n2;
    }

    public float getExp() {
        return this.exp;
    }

    public void setExp(float f2) {
        this.exp = f2;
    }
}

