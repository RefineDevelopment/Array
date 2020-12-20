package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import me.array.ArrayPractice.util.external.*;
import me.array.ArrayPractice.arena.*;
import org.bukkit.entity.Player;

import java.util.*;

@CommandMeta(label = { "arenas", "arena list" }, permission = "practice.staff")
public class ArenasCommand
{
    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» All Arenas"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        if (Arena.getArenas().isEmpty()) {
            player.sendMessage(CC.RED + "There are no arenas.");
            return;
        }
        for (final Arena arena : Arena.getArenas()) {
                player.sendMessage(CC.GRAY + " - " + (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + " (" + arena.getType().name() + ")" + (arena.isActive() ? " &7[&eIn-Match&7]" : " &7[&aFree&7]"));
        }
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}
