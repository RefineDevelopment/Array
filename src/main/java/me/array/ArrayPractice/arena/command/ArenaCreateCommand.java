package me.array.ArrayPractice.arena.command;

import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.*;
import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.arena.*;
import org.bukkit.*;
import me.array.ArrayPractice.arena.impl.*;
import java.util.*;

@CommandMeta(label = { "arena create" }, permission = "practice.staff")
public class ArenaCreateCommand {
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
            player.sendMessage(CC.RED + "Arena already exists with this name!");
            return;
        } else {
            if (type.equalsIgnoreCase("STANDALONE")) {
                arena=new StandaloneArena(name);
            } else {
                arena=new SharedArena(name);
            }
            final Location loc1=new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
            arena.setSpawnA(loc1);
            arena.setSpawnB(loc1);
            player.sendMessage(ChatColor.GREEN + type + " Arena " + name + " saved.");
        }
        arena.save();
        Arena.getArenas().add(arena);
        for ( final Arena arenas : Arena.getArenas() ) {
            arenas.save();
        }
    }
}
