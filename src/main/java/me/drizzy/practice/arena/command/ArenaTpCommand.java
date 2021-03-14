package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena tp", permission = "array.dev")
public class ArenaTpCommand {

    public void execute(Player player, @CPL("Arena") Arena arena) {
        if (arena != null) {
            player.teleport(arena.getSpawn1());
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully &bteleported &7to the arena &b" + arena.getName() + "&7!"));
        }
    }

}
