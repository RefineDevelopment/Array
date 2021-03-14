package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit sumo"}, permission = "array.dev")
public class KitSumoCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isSumo()) {
                kit.getGameRules().setSumo(false);
            } else if (!kit.getGameRules().isSumo()) {
                kit.getGameRules().setSumo(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated sumo mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isSumo() ? "true!" : "false!")));
        }
    }
}
