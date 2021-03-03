package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"arena reload", "arenas reload"}, permission="array.dev")
public class ArenaReloadCommand {
    public void execute(Player p) {
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING! Reloading is not recommended. You might need to restart to make all features work again.");
        long st=System.currentTimeMillis();
        Match.cleanup();
        Arena.getArenas().clear();
        Arena.preload();
        long et=System.currentTimeMillis();
        p.sendMessage(ChatColor.AQUA + "Arenas were reloaded in " + (et - st) + " ms.");
    }

}
