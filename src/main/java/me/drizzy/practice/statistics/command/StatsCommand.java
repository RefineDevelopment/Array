package me.drizzy.practice.statistics.command;

import me.drizzy.practice.statistics.menu.StatsMenu;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;


public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats");
        this.setAliases(Arrays.asList("statistics", "elo"));
        this.usageMessage = "/stats";
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            new StatsMenu(player).openMenu(player);
            return true;
        }
        if (args.length == 1) {
            String target2 = args[0];
            Player target = Bukkit.getPlayer(target2);
            if (target == null) {
                player.sendMessage(CC.translate("&7That player is not online."));
                return true;
            }
            new StatsMenu(target).openMenu(player);

        }
        return true;
    }
}
