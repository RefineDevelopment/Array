package me.array.ArrayPractice.arena.command;

import org.bukkit.entity.*;
import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.arena.*;
import org.bukkit.*;
import me.array.ArrayPractice.arena.impl.*;
import java.util.*;

@CommandMeta(label = { "arena create" }, permission = "practice.staff")
public class ArenaCreateCommand
{
    public void execute(final Player player, @CPL("name") final String name, @CPL("type: STANDALONE/SHARED") final String type) {
        if (!type.equalsIgnoreCase("STANDALONE") && !type.equalsIgnoreCase("SHARED")) {
            return;
        }
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
            arena.setType(type);
            final StandaloneArena sarena = (StandaloneArena)Arena.getByName(name);
            sarena.getDuplicates().add(arena);
            player.sendMessage(ChatColor.GREEN + "Duplicate arena " + name + " saved (#" + sarena.getDuplicates().size() + ")");
        }
        else {
            if (type.equalsIgnoreCase("STANDALONE")) {
                arena = new StandaloneArena(name);
            }
            else {
                arena = new SharedArena(name);
            }
            final Location loc1 = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
            arena.setSpawn1(loc1);
            arena.setSpawn2(loc1);
            player.sendMessage(ChatColor.GREEN + type + " arena " + name + " saved");
        }
        arena.save();
        Arena.getArenas().add(arena);
        for (final Arena arenas : Arena.getArenas()) {
            arenas.save();
        }
    }
}
