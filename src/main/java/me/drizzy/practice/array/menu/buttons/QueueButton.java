package me.drizzy.practice.array.menu.buttons;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueueButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add("&7Click to manage queues");
        lore.add("&7You can enable, disable");
        lore.add("&7and set min requirement");
        lore.add("&7for Ranked queues");
        lore.add("");
        lore.add("&cThis is in development!");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.IRON_SWORD).setLore(lore).setDisplayName("&b&lManage Queues").build();
    }

    @Override
    public void clicked(final Player player, ClickType clickType) {
        if (!player.hasPermission("array.dev")) {
            player.sendMessage(CC.RED + "You do not have permission to execute this action!");
            player.sendMessage(CC.RED + "Deploying Anti-Grief Alert!");
            for ( Player players : Bukkit.getOnlinePlayers() ) {
                if (players.hasPermission("array.staff") || players.hasPermission("array.dev")) {
                    players.sendMessage(CC.translate("&c&lWARNING: &c" + player.getName() + " &4tried to use manage menu without permissions!"));
                    players.sendMessage(CC.translate("&c&lWARNING: &cUsually, this should be impossible to happen, if this did occur then check your permissions."));
                }
            }
        }
    }
}
