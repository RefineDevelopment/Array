package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena tp", permission = "array.dev")
public class ArenaTpCommand {

    public void execute(Player player, @CPL("Arena") Arena arena) {
        if (arena != null) {
            player.teleport(arena.getSpawn1());
            player.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Successfully teleported to the arena!");
        }
    }

}
