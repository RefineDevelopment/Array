package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label="kit hitdelay", permission = "array.dev")
public class KitSetHitDelayCommand {
    public void execute(Player player, @CPL("kit") Kit kit, @CPL("delay") int delay) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            kit.getGameRules().setHitDelay(delay);
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "Kit hitdelay set to " + delay);
        }
    }
}