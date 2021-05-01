package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit boxUHC"}, permission = "array.dev")
public class KitBedwarsCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&c&lArray&8] &c")) + "Kit does not exist");
        } else {
            if (kit.getGameRules().isBedwars()) {
                kit.getGameRules().setBedwars(false);
            } else if (!kit.getGameRules().isBedwars()) {
                kit.getGameRules().setBedwars(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated Bedwars mode for &c" + kit.getName() +  " &7to &c" + (kit.getGameRules().isBedwars() ? "true!" : "false!")));
            player.sendMessage(CC.translate("&8[&cTIP&8] &7Please put Beds near the player spawns!"));
        }
    }
}
