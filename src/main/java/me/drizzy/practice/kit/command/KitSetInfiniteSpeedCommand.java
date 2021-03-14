package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit infinitespeed"}, permission = "array.dev")
public class KitSetInfiniteSpeedCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
        } else {
            if (kit.getGameRules().isInfinitespeed()) {
                kit.getGameRules().setInfinitespeed(false);
            } else if (!kit.getGameRules().isInfinitespeed()) {
                kit.getGameRules().setInfinitespeed(true);
            }
            kit.save();
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated infinite-speed mode for &b" + kit.getName() +  " &7to &b" + (kit.getGameRules().isInfinitespeed() ? "true!" : "false!")));
        }
    }
}