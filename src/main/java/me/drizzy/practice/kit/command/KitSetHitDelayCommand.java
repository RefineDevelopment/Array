package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="kit sethitdelay", permission = "array.dev")
public class KitSetHitDelayCommand {

    public void execute(Player player, @CPL("kit") Kit kit, @CPL("delay") String delay) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That kit does not exist."));
        } else {
            int test;
            try {
                test = Integer.parseInt(delay);
            } catch (NumberFormatException e) {
                player.sendMessage(CC.translate("Invalid Value!"));
                return;
            }
            kit.getGameRules().setHitDelay(test);
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated &c" + kit.getName() + " &7hitdelay set to &c" + test));
        }
    }
}