package xyz.refinedev.practice.util.inventory;

import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/9/2021
 * Project: Array
 */

@UtilityClass
public class TeamFightUtil {

    public void giveBardKit(Player player) {
        player.getInventory().setHelmet(new ItemBuilder(Material.GOLD_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.GOLD_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.GOLD_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setBoots(new ItemBuilder(Material.GOLD_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());

        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.IRON_INGOT).amount(64).build());
        player.getInventory().setItem(3, new ItemBuilder(Material.BLAZE_POWDER).amount(64).build());
        player.getInventory().setItem(4, new ItemBuilder(Material.GHAST_TEAR).amount(16).build());
        player.getInventory().setItem(5, new ItemBuilder(Material.POTION).amount(1).durability(8259).build());
        player.getInventory().setItem(8, new ItemBuilder(Material.COOKED_BEEF).amount(64).build());
        player.getInventory().setItem(18, new ItemBuilder(Material.FEATHER).amount(32).build());
        player.getInventory().setItem(9, new ItemBuilder(Material.SUGAR).amount(64).build());
        player.getInventory().setItem(35, new ItemBuilder(Material.MAGMA_CREAM).amount(64).build());
        player.getInventory().setItem(26, new ItemBuilder(Material.SPIDER_EYE).amount(32).build());

        ItemStack pots = new ItemBuilder(Material.POTION).durability(16421).build();

        while (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().firstEmpty() == -1) {
                return;
            }
            player.getInventory().addItem(pots);
        }
        player.updateInventory();

        Array.getInstance().getPvpClassManager().attemptEquip(player);
    }

    public void giveDiamondKit(Player player) {
        player.getInventory().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());

        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.POTION).amount(1).durability(8259).build());
        player.getInventory().setItem(3, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());
        player.getInventory().setItem(8, new ItemBuilder(Material.COOKED_BEEF).amount(64).build());


        player.getInventory().setItem(17, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());
        player.getInventory().setItem(26, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());
        player.getInventory().setItem(35, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());

        ItemStack pots = new ItemBuilder(Material.POTION).durability(16421).build();
        while (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().firstEmpty() == -1) {
                return;
            }
            player.getInventory().addItem(pots);
        }
        player.updateInventory();
    }


    public void giveArcherKit(Player player) {
        List<Color> colors = Arrays.asList(Color.fromRGB(6717235), Color.fromRGB(3361970), Color.fromRGB(5000268), Color.fromRGB(1644825));

        Collections.shuffle(colors);
        Collections.shuffle(colors);

        double chance = Array.RANDOM.nextDouble();

        if (chance <= 0.7D) {
            Color color = colors.get(0);
            player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).color(color).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).color(color).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).color(color).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).color(color).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());
        } else {
            player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
            player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());
        }

        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.ARROW_FIRE, 1).enchantment(Enchantment.ARROW_INFINITE, 1).build());
        player.getInventory().setItem(3, new ItemBuilder(Material.POTION).amount(1).durability(8259).build());

        player.getInventory().setItem(7, new ItemBuilder(Material.COOKED_BEEF).amount(64).build());
        player.getInventory().setItem(8, new ItemBuilder(Material.SUGAR).amount(64).build());

        player.getInventory().setItem(26, new ItemBuilder(Material.ARROW).amount(1).build());
        player.getInventory().setItem(17, new ItemBuilder(Material.FEATHER).amount(64).build());

        ItemStack pots = new ItemBuilder(Material.POTION).durability(16421).build();
        while (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().firstEmpty() == -1) {
                return;
            }
            player.getInventory().addItem(pots);
        }
        player.updateInventory();

        Array.getInstance().getPvpClassManager().attemptEquip(player);
    }

    public void giveRogueKit(Player player) {
        player.getInventory().setArmorContents(InventoryUtil.deserializeInventory("t@305:e@0@1:e@2@4:e@34@3;t@304:e@0@1:e@34@3;t@303:e@0@1:e@34@3;t@302:e@0@1:e@34@3;"));
        player.getInventory().setContents(InventoryUtil.deserializeInventory("t@276:e@16@1:e@34@3;t@368:a@16;t@283;t@283;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@353:a@64;t@393:a@64;t@283;t@283;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@283;t@283;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@283;t@283;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@373:d@16421;t@288:a@64;"));
        player.updateInventory();

        Array.getInstance().getPvpClassManager().attemptEquip(player);
    }
    
}
