package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"arena remove", "arena delete"}, permission = "array.dev")
public class ArenaRemoveCommand {

    public void execute(Player player, @CPL("Arena") Arena arena) {

        if (arena != null) {
            if (arena.isActive()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena is currently active, please try again later!"));
                return;
            }

            arena.delete();
            Arena.getArenas().remove(arena);
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully removed the arena &c" + arena.getDisplayName()));
        }
    }

}