package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit timed"}, permission = "array.dev")
public class KitTimedCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isTimed()) {
                kit.getGameRules().setTimed(false);
            } else if (!kit.getGameRules().isTimed()) {
                kit.getGameRules().setTimed(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated timed mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isTimed() ? "true!" : "false!")));
        }
    }
}
