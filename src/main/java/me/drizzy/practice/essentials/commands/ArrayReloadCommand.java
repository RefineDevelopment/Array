package me.drizzy.practice.essentials.commands;

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
        Array.getInstance().getEssentials().load();
        Array.getInstance().getTabManager().load();
        long et=System.currentTimeMillis();
        p.sendMessage(ChatColor.RED + "Array was reloaded in " + (et - st) + " ms.");
    }

}
