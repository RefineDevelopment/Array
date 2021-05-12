package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta( label = "arena setbridgespawn", permission="array.dev")
public class ArenaSetBridgdeSpawnCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("[red|blue]") String pos) {
        if (arena == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist."));
            return;
        }

        if (arena.getType() == ArenaType.THEBRIDGE) {
            Location loc=new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            if (pos.equals("red")) {
                arena.setSpawn1(loc);
            } else if (pos.equals("blue")) {
                arena.setSpawn2(loc);
            } else {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Invalid argument."));
            }
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated the position of &c" + arena.getName() + "&8&o (&7&oPosition: " + pos + "&8)"));
            arena.save();

        } else {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please use the command /arena setspawn to set the spawn for a non bridge arena."));
        }
    }

}