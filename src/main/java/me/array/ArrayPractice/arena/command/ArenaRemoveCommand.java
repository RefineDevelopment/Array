package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"arena remove", "arena delete"}, permission = "practice.admin.arena")
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