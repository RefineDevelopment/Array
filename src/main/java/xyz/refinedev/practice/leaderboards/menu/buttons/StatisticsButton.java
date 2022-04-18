package xyz.refinedev.practice.leaderboards.menu.buttons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.SkullCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/13/2021
 * Project: Array
 */

//TODO: Open a new menu or probably profile menu
public class StatisticsButton extends Button {

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        List<String> lore = new ArrayList<>();

        lore.add(CC.MENU_BAR);
        lore.add("&eClick here to view your statistics");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                .name("&c" + player.getName() + " &7\uff5c &fStatistics")
                .lore(lore)
                .clearFlags()
                .build();
    }
}