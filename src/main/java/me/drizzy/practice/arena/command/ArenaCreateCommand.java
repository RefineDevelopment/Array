package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.SharedArena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena create", permission = "array.dev")
public class ArenaCreateCommand {

    public void execute(Player player, @CPL("name") String name, @CPL("type: STANDALONE/SHARED") String type) {
        if (!type.equalsIgnoreCase("STANDALONE") && !type.equalsIgnoreCase("SHARED")) return;

        if (name == null) {
            player.sendMessage("Enter a name");
            return;
        }

        Arena arena;

        if (Arena.getArenas().contains(Arena.getByName(name))) {
            if (type.equalsIgnoreCase("shared")) {
                player.sendMessage("You cant make a shared arena duped");
                return;
            }
            arena = new Arena(name);

            Location loc1 = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            arena.setSpawn1(loc1);
            arena.setSpawn2(loc1);
            StandaloneArena sarena = (StandaloneArena) Arena.getByName(name);
            sarena.getDuplicates().add(arena);
            player.sendMessage(CC.translate("&8[&b&lArray&8] &a") +  "Duplicate arena " + name + " saved (#" + sarena.getDuplicates().size() + ")");
        } else {
            if (type.equalsIgnoreCase("STANDALONE")) {
                arena = new StandaloneArena(name);
            } else {
                arena = new SharedArena(name);
            }

            Location loc1 = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            arena.setSpawn1(loc1);
            arena.setSpawn2(loc1);
            player.sendMessage(CC.translate("&8[&b&lArray&8] &a") +  type + " arena " + name + " saved");
        }
        arena.save();
        Arena.getArenas().add(arena);

        for (Arena arenas : Arena.getArenas()) {
            arenas.save();
        }
    }

}