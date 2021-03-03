package me.drizzy.practice.arena.command;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "arena", "arenahelp" }, permission = "array.staff")
public class ArenaCommand {
    public void execute(final Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» Arena Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate("&b/arena create <name> <Shared|Standalone> &8- &8&o(&7&oCreate an Arena&8&o)"));
        player.sendMessage(CC.translate("&b/arena disablepearls &8- &8&o(&7&oEnable or Disable the ability for players to pear on the arena&8&o)"));
        player.sendMessage(CC.translate("&b/arena remove (name) &8- &8&o(&7&oDelete an Arena&8&o)"));
        player.sendMessage(CC.translate("&b/arena seticon (name) &8- &8&o(&7&oSets the item your holding to Arena Icon&8&o)"));
        player.sendMessage(CC.translate("&b/arena setspawn (1/2) (1/2) &8- &8&o(&7&oSet 1/2 spawn of arena&8&o)"));
        player.sendMessage(CC.translate("&b/arena addkit (Arena) (Kit) &8- &8&o(&7&oAdd a kit to the arena&8&o)"));
        player.sendMessage(CC.translate("&b/arena removekit (Arena) (Kit) &8- &8&o(&7&oRemove a kit from the arena&8&o)"));
        player.sendMessage(CC.translate("&b/arena save &8- &8&o(&7&oSave Arenas&8&o)"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }

}
