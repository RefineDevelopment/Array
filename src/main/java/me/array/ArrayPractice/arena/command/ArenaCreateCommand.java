package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.impl.SharedArena;
import me.array.ArrayPractice.arena.impl.StandaloneArena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena create", permission = "practice.admin.arena")
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
            player.sendMessage(ChatColor.GREEN + "Duplicate arena " + name + " saved (#" + sarena.getDuplicates().size() + ")");
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
            player.sendMessage(ChatColor.GREEN + type + " arena " + name + " saved");
        }
        arena.save();
        Arena.getArenas().add(arena);

        for (Arena arenas : Arena.getArenas()) {
            arenas.save();
        }
    }

}