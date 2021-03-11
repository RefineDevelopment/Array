package me.drizzy.practice.util;

import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationUtils {
    public static String getString(Location loc) {
        StringBuilder builder = new StringBuilder();

        if (loc == null) return "unset";

        builder.append(loc.getX()).append("|");
        builder.append(loc.getY()).append("|");
        builder.append(loc.getZ()).append("|");
        builder.append(loc.getWorld().getName()).append("|");
        builder.append(loc.getYaw()).append("|");
        builder.append(loc.getPitch());

        return builder.toString();
    }

    public static Location getLocation(String s) {
        if (s == null || s.equals("unset") || s.equals("")) return null;

        String[] data = s.split("\\|");
        double x = Double.parseDouble(data[0]);
        double y = Double.parseDouble(data[1]);
        double z = Double.parseDouble(data[2]);
        World world = Bukkit.getWorld(data[3]);
        Float yaw = Float.parseFloat(data[4]);
        Float pitch = Float.parseFloat(data[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static boolean isSameLocation(Location loc1, Location loc2) {
        return loc1 != null && loc1.equals(loc2);
    }

    public static boolean isTeamPortal(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        TheBridgeMatch match = (TheBridgeMatch) profile.getMatch();

        TheBridgeArena arena = (TheBridgeArena) match.getArena();

        if (match.getTeamPlayerA().getPlayer() == player) {
            return arena.getRedCuboid().contains(player.getLocation());
        } else {
            return arena.getBlueCuboid().contains(player.getLocation());
        }
    }

}