package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(label={"kit seticon"}, permission = "practice.dev")
public class KitSetIconCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        ItemStack item = player.getItemInHand();
        if (item == null) {
            player.sendMessage(CC.RED + "Please hold a valid item in your hand!");
        }
        else if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            kit.setDisplayIcon(item);
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a"))+ "Kit Icon set!");
        }
    }
}
