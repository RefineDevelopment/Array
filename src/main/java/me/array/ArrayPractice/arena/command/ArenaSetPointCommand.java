package me.array.ArrayPractice.arena.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setpoint", permission = "practice.admin.arena")
public class ArenaSetPointCommand {

    public void execute(Player player, @CPL("arena") Arena arena, @CPL("1/2") Integer pos) {
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Arena does not exist");
            return;
        }

        Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

        if (pos.equals(1)) {
            arena.setPoint1(loc);
        } else if (pos.equals(2)) {
            arena.setPoint2(loc);
        }
        player.sendMessage(ChatColor.GREEN + "Successfully set the point of " + arena.getName() + " (Point: " + pos + ")");
        arena.save();
    }

}