package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit healthregen"}, permission = "practice.dev")
public class KitSetHealthRegenenerationCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isHealthRegeneration()) {
                kit.getGameRules().setHealthRegeneration(false);
            } else if (!kit.getGameRules().isHealthRegeneration()) {
                kit.getGameRules().setHealthRegeneration(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set health-regen mode to " + (kit.getGameRules().isHealthRegeneration() ? "true!" : "false!")));
        }
    }
}