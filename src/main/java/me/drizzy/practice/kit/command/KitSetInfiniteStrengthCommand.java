package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit infinitestrength"}, permission = "practice.dev")
public class KitSetInfiniteStrengthCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isInfinitestrength()) {
                kit.getGameRules().setInfinitestrength(false);
            } else if (!kit.getGameRules().isInfinitestrength()) {
                kit.getGameRules().setInfinitestrength(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set infinitestrength mode to " + (kit.getGameRules().isInfinitestrength() ? "true!" : "false!")));
        }
    }
}
