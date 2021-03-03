package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit sumo"}, permission = "array.dev")
public class KitSumoCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            if (kit.getGameRules().isSumo()) {
                kit.getGameRules().setSumo(false);
            } else if (!kit.getGameRules().isSumo()) {
                kit.getGameRules().setSumo(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "Kit set sumo mode to " + (kit.getGameRules().isSumo() ? "true!" : "false!"));
        }
    }
}
