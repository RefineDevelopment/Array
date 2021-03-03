package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit partyffa"}, permission = "array.dev")
public class KitPartyFFACommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isPartyffa()) {
                kit.getGameRules().setPartyffa(false);
            } else if (!kit.getGameRules().isPartyffa()) {
                kit.getGameRules().setPartyffa(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set partyffa mode to " + (kit.getGameRules().isPartyffa() ? "true!" : "false!")));
        }
    }
}
