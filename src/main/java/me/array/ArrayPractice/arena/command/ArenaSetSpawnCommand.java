package me.array.ArrayPractice.arena.command;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.*;
import me.array.ArrayPractice.arena.*;
import com.qrakn.honcho.command.*;
import org.bukkit.*;
import me.array.ArrayPractice.arena.impl.*;

import java.util.Set;

@CommandMeta(label = { "arena setspawn" }, permission = "practice.staff")
public class ArenaSetSpawnCommand
{
    public void execute(final Player player, @CPL("arena") final Arena arena, @CPL("1/2") final Integer pos) {
            final Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        if (pos >= 2) {
            player.sendMessage(CC.translate("&cPlease Set Spawn 1 and 2!"));
        }
            if (pos.equals(1)) {
                arena.setSpawn1(loc);
            }
            else if (pos.equals(2)) {
                arena.setSpawn2(loc);
            }
            player.sendMessage(ChatColor.GREEN + "Successfully set the position of " + arena.getName() + " (Position: " + pos + ")");
            arena.save();
        }
     }
