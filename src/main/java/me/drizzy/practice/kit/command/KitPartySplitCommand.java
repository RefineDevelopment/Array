package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit disablePartySplit"}, permission = "array.dev")
public class KitPartySplitCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7A kit with that name does not exist."));
        } else {
            if (kit.getGameRules().isDisablePartySplit()) {
                kit.getGameRules().setDisablePartySplit(false);
            } else if (!kit.getGameRules().isDisablePartySplit()) {
                kit.getGameRules().setDisablePartySplit(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated party-split mode for &c" + kit.getName() +  " &7to &c" + (kit.getGameRules().isDisablePartySplit() ? "true!" : "false!")));
        }
    }
}
