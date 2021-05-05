package me.drizzy.practice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.drizzy.practice.util.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Drizzy
 * Created at 5/3/2021
 */
@Getter
@AllArgsConstructor
public enum DifficultyType {

    EASY(2.5, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.WHITE.toString() + ChatColor.BOLD + "Easy").build()),
    MEDIUM(2.8, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Medium").durability(4).build()),
    HARD(3.0, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.GOLD.toString() + ChatColor.BOLD + "Hard").durability(1).build()),
    EXPERT(3.2, new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED.toString() + ChatColor.BOLD + "Expert").durability(14).build());

    private final double reach;
    private final ItemStack item;

}