package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(label={"arena seticon"}, permission="practice.staff")
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
        player.sendMessage(CC.GREEN + "Arena Icon set!");
    }
  }
}
