package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit lavakill"}, permission = "practice.dev")
public class KitLavaKillCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isLavakill()) {
                kit.getGameRules().setLavakill(false);
            } else if (!kit.getGameRules().isLavakill()) {
                kit.getGameRules().setLavakill(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set lavakill mode to " + (kit.getGameRules().isLavakill() ? "true!" : "false!")));
        }
    }
}
