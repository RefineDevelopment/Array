package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit partysplit"}, permission = "array.dev")
public class KitPartySplitCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7A kit with that name does not exist."));
        } else {
            if (kit.getGameRules().isPartysplit()) {
                kit.getGameRules().setPartysplit(false);
            } else if (!kit.getGameRules().isPartysplit()) {
                kit.getGameRules().setPartysplit(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated party-split mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isPartysplit() ? "true!" : "false!")));
        }
    }
}
