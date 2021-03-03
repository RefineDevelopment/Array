package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(label={"arena seticon"}, permission="array.dev")
public class ArenaSetIconCommand {
    public void execute(Player player, @CPL("arena") Arena arena) {
    ItemStack item = player.getItemInHand();
    if (item == null) {
        player.sendMessage(CC.RED + "Please hold a valid item in your hand!");
    }
    else if (arena == null) {
        player.sendMessage(ChatColor.RED + "Arena does not exist");
    } else {
        arena.setDisplayIcon(item);
        arena.save();
        player.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Successfully set the arena icon to your item in hand.");
    }
  }
}
