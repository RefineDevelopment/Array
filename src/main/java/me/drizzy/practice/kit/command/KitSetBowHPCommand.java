package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit bowHP"}, permission = "array.dev")
public class KitSetBowHPCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isBowHP()) {
                kit.getGameRules().setBowHP(false);
            } else if (!kit.getGameRules().isBowHP()) {
                kit.getGameRules().setBowHP(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated bow-hp mode for &c" + kit.getName() +  " &7to &c" + (kit.getGameRules().isBowHP() ? "true!" : "false!")));
        }
    }
}
