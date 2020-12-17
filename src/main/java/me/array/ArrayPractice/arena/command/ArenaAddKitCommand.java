package me.array.ArrayPractice.arena.command;

import me.array.ArrayPractice.Array;
import org.bukkit.entity.*;
import me.array.ArrayPractice.arena.*;
import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.kit.*;
import org.bukkit.*;

@CommandMeta(label = { "arena addkit" }, permission = "practice.staff")
public class ArenaAddKitCommand
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
        if (!arena.getKits().contains(kit.getName())) {
            arena.getKits().add(kit.getName());
        }
        player.sendMessage(ChatColor.GREEN + "Successfully added the kit " + kit.getName() + " to " + arena.getName());
        arena.save();
    }
}
