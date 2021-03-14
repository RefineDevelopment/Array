package me.drizzy.practice.array.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"array reload", "practice reload"}, permission="array.dev")
public class ArrayReloadCommand {
    public void execute(Player p) {
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING! Reloading is not recommended. You might need to restart to make all features work again.");
        long st=System.currentTimeMillis();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(CC.RED + "Reloading Practice plugin, please rejoin in 1 minute!"));
        Array.getInstance().getPluginLoader().disablePlugin(Array.getInstance());
        Array.getInstance().getPluginLoader().enablePlugin(Array.getInstance());
        long et=System.currentTimeMillis();
        p.sendMessage(ChatColor.AQUA + "Array was reloaded in " + (et - st) + " ms.");
    }

}
