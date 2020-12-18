package me.array.ArrayPractice.arena.command;

import org.bukkit.entity.*;
import me.array.ArrayPractice.arena.*;
import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.kit.*;
import org.bukkit.*;

@CommandMeta(label = { "arena removekit" }, permission = "practice.staff")
public class ArenaRemoveKitCommand
{
    public void execute(final Player player, @CPL("arena") final Arena arena, @CPL("kit") final Kit kit) {
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena does not exist");
            return;
        }
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
            return;
        }
        if (arena.getKits().contains(kit.getName())) {
            arena.getKits().remove(kit.getName());
            player.sendMessage(ChatColor.GREEN + "Successfully removed the kit " + kit.getName() + " from " + arena.getName());
            arena.save();
        }
    }
}
