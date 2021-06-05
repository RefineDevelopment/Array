package me.drizzy.practice.robot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public enum RobotType {

    EASY(2.5D, new ItemBuilder(Material.STAINED_GLASS_PANE).name(CC.GREEN + "Easy").build()),
    MEDIUM(2.8D, new ItemBuilder(Material.STAINED_GLASS_PANE).name(CC.YELLOW + CC.BOLD + "Medium").durability(4).build()),
    HARD(3.0D, new ItemBuilder(Material.STAINED_GLASS_PANE).name(CC.GOLD + CC.BOLD + "Hard").durability(1).build()),
    EXPERT(3.2D, new ItemBuilder(Material.STAINED_GLASS_PANE).name(CC.DARK_RED + CC.BOLD + "Expert").durability(14).build());

    private final double reach;
    private final ItemStack item;
}
