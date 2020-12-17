package me.array.ArrayPractice.arena.command;

import org.bukkit.entity.*;
import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.arena.*;
import org.bukkit.*;

@CommandMeta(label = { "arena remove", "arena delete" }, permission = "practice.admin.arena")
public class ArenaRemoveCommand
{
    public void execute(final Player player, @CPL("name") final String name) {
        if (name == null) {
            player.sendMessage("Enter a name");
            return;
        }
        final Arena arena = Arena.getByName(name);
        if (arena != null) {
            arena.delete();
            Arena.getArenas().remove(arena);
            player.sendMessage(ChatColor.RED + "Arena " + name + " removed");
        }
    }
}
