package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"arena remove", "arena delete"}, permission = "array.dev")
public class ArenaRemoveCommand {

    public void execute(Player player, @CPL("name") String name) {
        if (name == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Please provide a valid name."));
            return;
        }
        Arena arena = Arena.getByName(name);

        if (arena != null) {
            arena.delete();
            Arena.getArenas().remove(arena);
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully removed the arena &b" + name));
        }
    }

}