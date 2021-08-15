package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit elo", "kit ranked"}, permission = "array.dev")
public class KitSetRankedCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isRanked()) {
                kit.getGameRules().setRanked(false);
            } else if (!kit.getGameRules().isRanked()) {
                kit.getGameRules().setRanked(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated ranked mode for &c" + kit.getName() +  " &7to &c" + (kit.getGameRules().isRanked() ? "true!" : "false!")));
        }
    }
}
