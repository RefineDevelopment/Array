package me.array.ArrayPractice.profile.menu.command;

import me.array.ArrayPractice.profile.stats.menu.ELOMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "stats", "statistics", "elo", "stat" })
public class StatsCommand
{
    public void execute(final Player player) {
        new ELOMenu(player).openMenu(player);
        player.sendMessage(ChatColor.GRAY + "Now viewing stats menu.");
    }
}

