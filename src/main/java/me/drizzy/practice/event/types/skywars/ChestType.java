package me.drizzy.practice.event.types.skywars;

import me.drizzy.practice.util.RandomCollection;
import me.drizzy.practice.util.external.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum ChestType {

    MID, ISLAND, NORMAL;


    public static RandomCollection<ItemStack> getLootTable(ChestType type) {
        RandomCollection<ItemStack> c = new RandomCollection<>();
        switch (type) {
            case ISLAND:
                c.add(88, new ItemStack(Material.IRON_HELMET));
                c.add(88, new ItemStack(Material.IRON_CHESTPLATE));
                c.add(88, new ItemStack(Material.IRON_LEGGINGS));
                c.add(88, new ItemStack(Material.IRON_BOOTS));
                c.add(5, new ItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_KNOCKBACK, 1).build());
                c.add(50, new ItemStack(Material.STONE, 32));
                c.add(50, new ItemStack(Material.WOOD, 32));
                c.add(30, new ItemStack(Material.COOKED_BEEF, 16));
                c.add(25, new ItemStack(Material.SNOW_BALL, 8));
                c.add(25, new ItemStack(Material.FISHING_ROD));
                c.add(50, new ItemBuilder(Material.POTION).durability((short)16418).build());
                c.add(50, new ItemStack(Material.IRON_SWORD));
                c.add(88, new ItemStack(Material.DIAMOND_AXE));
                c.add(35, new ItemStack(Material.WATER_BUCKET));
                break;
            case NORMAL:
                c.add(5, new ItemStack(Material.WEB));
                c.add(60, new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
                c.add(60, new ItemStack(Material.EXP_BOTTLE, 32));
                c.add(50, new ItemStack(Material.EGG, 64));
                c.add(50, new ItemStack(Material.DIAMOND_CHESTPLATE));
                c.add(50, new ItemStack(Material.DIAMOND_BOOTS));
                c.add(5, new ItemStack(Material.ENCHANTMENT_TABLE));
                c.add(20, new ItemBuilder(Material.POTION).amount(3).durability((short)16453).build());
                c.add(15, new ItemBuilder(Material.POTION).durability((short)8227).build());
                c.add(10, new ItemBuilder(Material.FLINT_AND_STEEL).build());
                break;
            case MID:
                c.add(5, new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                c.add(5, new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                c.add(35, new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
                c.add(50, new ItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build());
                c.add(45, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.FIRE_ASPECT, 2).build());
                c.add(1, new ItemBuilder(Material.GOLD_SWORD).enchantment(Enchantment.DAMAGE_ALL, 5).build());
                c.add(10, new ItemBuilder(Material.STONE_SWORD).enchantment(Enchantment.DAMAGE_ALL, 3).build());
                c.add(40, new ItemBuilder(Material.ANVIL).build());
                c.add(5, new ItemBuilder(Material.ENDER_PEARL).amount(5).build());
                c.add(5, new ItemBuilder(Material.STICK).enchantment(Enchantment.KNOCKBACK, 2).build());
                c.add(5, new ItemBuilder(Material.LAVA_BUCKET).build());
                c.add(25, new ItemBuilder(Material.TNT).amount(6).build());
                break;
        }
        return c;
    }


}
