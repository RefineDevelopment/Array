package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setspawn", permission = "practice.dev")
public class ArenaSetSpawnCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("1/2") Integer pos, @CPL("Arena number, 1 if original, 2+ if dupe arena") Integer dupe) {
        if (dupe == 1) {
            if (arena == null) {
                player.sendMessage(ChatColor.RED + "Arena does not exist");
                return;
            }

            Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            if (pos.equals(1)) {
                arena.setSpawn1(loc);
            } else if (pos.equals(2)) {
                arena.setSpawn2(loc);
            }
            player.sendMessage(ChatColor.GREEN + "Successfully set the position of " + arena.getName() + " (Position: " + pos + ") (Original Arena)");
            arena.save();
        } else if (dupe > 1) {
            if (arena == null) {
                player.sendMessage(ChatColor.RED + "Arena does not exist");
                return;
            }

            Arena darena = ((StandaloneArena) arena).getDuplicates().get(dupe - 2);

            if (darena != null) {
                Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                        player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

                if (pos.equals(1)) {
                    darena.setSpawn1(loc);
                } else if (pos.equals(2)) {
                    darena.setSpawn2(loc);
                }

                player.sendMessage(ChatColor.GREEN + "Successfully set the position of " + arena.getName() + " (Position: " + pos + ") (Dupe Arena #" + (dupe - 1) + ")");
                arena.save();
            }
        }
    }

}