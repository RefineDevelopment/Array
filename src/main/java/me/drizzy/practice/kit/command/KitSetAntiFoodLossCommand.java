package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit antifoodloss"}, permission = "array.dev")
public class KitSetAntiFoodLossCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isAntifoodloss()) {
                kit.getGameRules().setAntifoodloss(false);
            } else if (!kit.getGameRules().isAntifoodloss()) {
                kit.getGameRules().setAntifoodloss(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set antifoodloss mode to " + (kit.getGameRules().isAntifoodloss() ? "true!" : "false!")));
        }
    }
}
