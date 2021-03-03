package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit waterkill"}, permission = "array.dev")
public class KitWaterKillCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isWaterkill()) {
                kit.getGameRules().setWaterkill(false);
            } else if (!kit.getGameRules().isWaterkill()) {
                kit.getGameRules().setWaterkill(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set water kill mode to " + (kit.getGameRules().isWaterkill() ? "true!" : "false!")));
        }
    }
}
