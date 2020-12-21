package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@CommandMeta(label = { "arena setspawn" }, permission = "practice.staff")
public class ArenaSetSpawnCommand {
    public void execute(final Player player, @CPL("arena") final Arena arena, @CPL("1/2") final Integer pos) {
        final Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        final String path = "arenas." + arena.getName();
        final FileConfiguration configuration = Array.get().getArenasConfig().getConfiguration();
            if (pos.equals(1)) {
                arena.setSpawn1(loc);
                configuration.set(path + ".spawn1", LocationUtil.serialize(loc));
            }
            if (pos.equals(2)) {
                arena.setSpawn2(loc);
                configuration.set(path + ".spawn2", LocationUtil.serialize(loc));
            } else {
            player.sendMessage(CC.RED + "Please set spawn 1 and 2!");
            }
            player.sendMessage(ChatColor.GREEN + "Successfully set the position of " + arena.getName() + " (Position: " + pos + ")");
            arena.save();
            for (final Arena arenas : Arena.getArenas()) {
                arenas.save();
            }
        }
    }