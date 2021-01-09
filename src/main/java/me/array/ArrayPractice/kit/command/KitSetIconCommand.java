package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(label={"kit seticon"}, permission="practice.staff")
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
            player.sendMessage(CC.GREEN + "Kit Icon set!");
        }
    }
}
