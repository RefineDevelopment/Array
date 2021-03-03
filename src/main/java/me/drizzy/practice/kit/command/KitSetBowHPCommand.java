package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit bowhp"}, permission = "array.dev")
public class KitSetBowHPCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            if (kit.getGameRules().isBowhp()) {
                kit.getGameRules().setBowhp(false);
            } else if (!kit.getGameRules().isBowhp()) {
                kit.getGameRules().setBowhp(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "Kit set bow-hp mode to " + (kit.getGameRules().isBowhp() ? "true!" : "false!"));
        }
    }
}
