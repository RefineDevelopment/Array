package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"arena remove", "arena delete"}, permission = "practice.dev")
public class ArenaRemoveCommand {

    public void execute(Player player, @CPL("name") String name) {
        if (name == null) {
            player.sendMessage("Enter a name");
            return;
        }
        Arena arena = Arena.getByName(name);

        if (arena != null) {
            arena.delete();
            Arena.getArenas().remove(arena);
            player.sendMessage(ChatColor.RED + "Arena " + name + " removed");
        }
    }

}