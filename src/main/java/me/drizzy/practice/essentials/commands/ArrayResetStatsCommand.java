package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;


@CommandMeta(label = {"array reset", "array resetstats"}, permission = "array.dev")
public class ArrayResetStatsCommand {

    public void execute(Player player, @CPL("profile") Profile name) {

        if (name == null) {
            player.sendMessage(CC.RED + "Either that player does not exist or you did not specify a name!");
            return;
        }

        name.getStatisticsData().values().forEach(stats -> {
            stats.setElo(1000);
            stats.setWon(0);
            stats.setLost(0);
        });
        name.setGlobalElo(1000);
        name.save();

        name.getPlayer().kickPlayer("Your Profile was reset by an Admin, Please Rejoin!");

    }

}
