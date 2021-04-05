package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit disablePartyFFA"}, permission = "array.dev")
public class KitPartyFFACommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isDisablePartyFFA()) {
                kit.getGameRules().setDisablePartyFFA(false);
            } else if (!kit.getGameRules().isDisablePartyFFA()) {
                kit.getGameRules().setDisablePartyFFA(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated Party-FFA mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isDisablePartyFFA() ? "true!" : "false!")));
        }
    }
}
