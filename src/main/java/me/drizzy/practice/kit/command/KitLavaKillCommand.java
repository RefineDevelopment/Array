package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit lavakill"}, permission = "array.dev")
public class KitLavaKillCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
} else {
            if (kit.getGameRules().isLavakill()) {
                kit.getGameRules().setLavakill(false);
            } else if (!kit.getGameRules().isLavakill()) {
                kit.getGameRules().setLavakill(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated lava-kill mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isLavakill() ? "true!" : "false!")));
        }
    }
}
