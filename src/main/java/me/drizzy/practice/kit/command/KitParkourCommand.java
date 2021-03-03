package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit parkour"}, permission = "array.dev")
public class KitParkourCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            if (kit.getGameRules().isParkour()) {
                kit.getGameRules().setParkour(false);
            } else if (!kit.getGameRules().isParkour()) {
                kit.getGameRules().setParkour(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "Kit set parkour mode to " + (kit.getGameRules().isParkour() ? "true!" : "false!"));
            player.sendMessage(CC.translate("&8[&bTIP&8] &7Use Iron pressure plate for Check-Point and Gold pressure plate for Win-point!"));
        }
    }
}
