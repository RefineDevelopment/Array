package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "arenas", "arena list" }, permission = "practice.staff")
public class ArenasCommand
{
    public void execute(Player player) {
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
