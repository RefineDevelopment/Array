package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit waterkill"}, permission = "array.dev")
public class KitWaterKillCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isWaterkill()) {
                kit.getGameRules().setWaterkill(false);
            } else if (!kit.getGameRules().isWaterkill()) {
                kit.getGameRules().setWaterkill(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated water kill mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isWaterkill() ? "true!" : "false!")));
        }
    }
}
