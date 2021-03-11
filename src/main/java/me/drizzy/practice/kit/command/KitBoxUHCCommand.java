package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit boxuhc"}, permission = "array.dev")
public class KitBoxUHCCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c")) + "Kit does not exist");
        } else {
            if (kit.getGameRules().isBoxuhc()) {
                kit.getGameRules().setBoxuhc(false);
            } else if (!kit.getGameRules().isBoxuhc()) {
                kit.getGameRules().setBoxuhc(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated BoxUHC mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isBoxuhc() ? "true!" : "false!")));
            player.sendMessage(CC.translate("&8[&bTIP&8] &7Please make the Arena a Barrier Box with Wood filled inside it except on the spawn points!"));
        }
    }
}
