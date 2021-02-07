package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena tp", permission = "practice.dev")
public class ArenaTpCommand {

    public void execute(Player player, @CPL("Arena") Arena arena) {
        if (arena != null) {
            player.teleport(arena.getSpawn1());
        }
    }

}
