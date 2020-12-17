package me.array.ArrayPractice.arena.command;

import me.array.ArrayPractice.Array;
import org.bukkit.entity.*;
import me.array.ArrayPractice.arena.*;
import com.qrakn.honcho.command.*;
import org.bukkit.*;

@CommandMeta(label = { "arena setpoint" }, permission = "practice.admin.arena")
public class ArenaSetPointCommand
{
    public void execute(final Player player, @CPL("arena") final Arena arena, @CPL("1/2") final Integer pos) {
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena does not exist");
            return;
        }
        final Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        if (pos.equals(1)) {
            arena.setPoint1(loc);
        }
        else if (pos.equals(2)) {
            arena.setPoint2(loc);
        }
        player.sendMessage(ChatColor.GREEN + "Successfully set the point of " + arena.getName() + " (Point: " + pos + ")");
        arena.save();
    }
}
