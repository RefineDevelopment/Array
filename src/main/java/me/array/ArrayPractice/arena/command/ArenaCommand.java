package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "arena", "arenahelp" }, permission = "practice.staff")
public class ArenaCommand {
    public void execute(final Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» Arena Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate("&7» &b/arena create (name) (SHARED/STANDALONE) &7- Create an Arena"));
        player.sendMessage(CC.translate("&7» &b/arena remove (name) &7- Delete an Arena"));
        player.sendMessage(CC.translate("&7» &b/arena seticon (name) &7- Sets the item your holding to Arena Icon"));
        player.sendMessage(CC.translate("&7» &b/arena setspawn (1/2) (1/2) &7- Set 1/2 spawn of arena"));
        player.sendMessage(CC.translate("&7» &b/arena setpoint (1/2) (KOTH ARENA) &7- Set 1/2 point of Koth Area of arena"));
        player.sendMessage(CC.translate("&7» &b/arena addkit (Arena) (Kit) &7- Add a kit to the arena."));
        player.sendMessage(CC.translate("&7» &b/arena removekit (Arena) (Kit) &7- Remove a kit from the arena."));
        player.sendMessage(CC.translate("&7» &b/arena save &7- Save Arenas"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}
