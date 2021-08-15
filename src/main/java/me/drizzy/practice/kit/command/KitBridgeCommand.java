package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit bridge"}, permission = "array.dev")
public class KitBridgeCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&c&lArray&8] &c")) + "Kit does not exist");
        } else {
            if (kit.getGameRules().isBridge()) {
                kit.getGameRules().setBridge(false);
            } else if (!kit.getGameRules().isBridge()) {
                kit.getGameRules().setBridge(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated build mode for &c" + kit.getName() +  " &7to &c" + (kit.getGameRules().isBridge() ? "true!" : "false!")));
        }
    }
}
