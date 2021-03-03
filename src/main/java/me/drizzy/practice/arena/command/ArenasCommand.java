package me.drizzy.practice.arena.command;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.ArenaType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "arenas", "arena list" }, permission = "array.staff")
public class ArenasCommand
{
    public void execute(CommandSender player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» All Arenas"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        if (Arena.getArenas().isEmpty()) {
            player.sendMessage("");
            player.sendMessage(CC.GRAY + CC.ITALIC + "There are no arenas.");
            player.sendMessage("");
            return;
        }
        for (final Arena arena : Arena.getArenas()) {
                player.sendMessage(CC.GRAY + " - " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.translate((arena.isActive() ? " &7[&eIn-Match&7]" : " &7[&aFree&7]") + (arena.getType() == ArenaType.SHARED ? " &7[&bShared&7]" : " &7[&bStandalone&7]")));
        }
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}
