package me.drizzy.practice.statistics.command;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.statistics.menu.StatsMenu;
import org.bukkit.entity.Player;

@CommandMeta(label = {"stats", "profile", "player", "elo"})
public class StatsCommand {

    public void execute(Player player) {
        new StatsMenu(player).openMenu(player);
    }

    public void execute(Player player, @CPL("player") Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "That player does not exist!");
            return;
        }

        new StatsMenu(target).openMenu(player);
    }

}
