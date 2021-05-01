package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit infiniteStrength"}, permission = "array.dev")
public class KitSetInfiniteStrengthCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isInfiniteStrength()) {
                kit.getGameRules().setInfiniteStrength(false);
            } else if (!kit.getGameRules().isInfiniteStrength()) {
                kit.getGameRules().setInfiniteStrength(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated infinite-strength mode for &c" + kit.getName() +  " &7to &c" + (kit.getGameRules().isInfiniteStrength() ? "true!" : "false!")));
        }
    }
}
