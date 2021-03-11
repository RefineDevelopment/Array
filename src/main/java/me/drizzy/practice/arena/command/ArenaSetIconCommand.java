package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(label={"arena seticon"}, permission="array.dev")
public class ArenaSetIconCommand {
    public void execute(Player player, @CPL("arena") Arena arena) {
    ItemStack item = player.getItemInHand();
    if (item == null) {
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7Please hold a valid item in your hand!"));
    }
    else if (arena == null) {
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7An arena with that name does not exist."));
    } else {
        arena.setDisplayIcon(item);
        arena.save();
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully set the &barena icon &7to the &bitem&7 in your hand."));
    }
  }
}
