package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit parkour"}, permission = "array.dev")
public class KitParkourCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isParkour()) {
                kit.getGameRules().setParkour(false);
            } else if (!kit.getGameRules().isParkour()) {
                kit.getGameRules().setParkour(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated parkour mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isParkour() ? "true!" : "false!")));
            player.sendMessage(CC.translate("&8[&bTIP&8] &7&oUse Iron pressure plate for Check-Point and Gold pressure plate for Win-point!"));
        }
    }
}
