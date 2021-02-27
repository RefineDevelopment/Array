package me.drizzy.practice.statistics.command;

import me.drizzy.practice.statistics.menu.StatsMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;


public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats");
        this.setAliases(Collections.singletonList("statistics"));
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
            Player target = Bukkit.getOfflinePlayer(target2).getPlayer();
            new StatsMenu(target).openMenu(player);

        }
        return true;
    }
}
