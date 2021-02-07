package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit infinitespeed"}, permission = "practice.dev")
public class KitSetInfiniteSpeedCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "Kit does not exist");
        } else {
            if (kit.getGameRules().isInfinitespeed()) {
                kit.getGameRules().setInfinitespeed(false);
            } else if (!kit.getGameRules().isInfinitespeed()) {
                kit.getGameRules().setInfinitespeed(true);
            }
            kit.save();
            player.sendMessage((CC.translate("&8[&b&lArray&8] &a") + "Kit set infinitespeed mode to " + (kit.getGameRules().isInfinitespeed() ? "true!" : "false!")));
        }
    }
}