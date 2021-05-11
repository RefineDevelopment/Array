package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setmin", permission = "array.dev")
public class ArenaSetMinCommand {
    public void execute(Player player, @CPL("arena") Arena arena) {

        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist."));
            return;
        }


        arena.setMin(player.getLocation());
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cMin &7Position for the arena &c" + arena.getDisplayName() + "&7!"));

    }

}