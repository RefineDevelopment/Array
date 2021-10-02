package xyz.refinedev.practice.util.location;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.impl.TheBridgeArena;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;

@UtilityClass
public class LocationUtils {

    public String getString(Location loc) {

        if (loc == null) return "unset";

        return loc.getX() + "|" +
                loc.getY() + "|" +
                loc.getZ() + "|" +
                loc.getWorld().getName() + "|" +
                loc.getYaw() + "|" +
                loc.getPitch();
    }

    public Location getLocation(String s) {
        if (s == null || s.equals("unset") || s.equals("")) return null;

        String[] data = s.split("\\|");
        double x = Double.parseDouble(data[0]);
        double y = Double.parseDouble(data[1]);
        double z = Double.parseDouble(data[2]);
        World world = Bukkit.getWorld(data[3]);
        float yaw = Float.parseFloat(data[4]);
        float pitch = Float.parseFloat(data[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean isSameLocation(Location loc1, Location loc2) {
        return loc1 != null && loc1.equals(loc2);
    }

    public boolean isTeamPortalSolo(Player player) {
        Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());
        SoloBridgeMatch match = (SoloBridgeMatch) profile.getMatch();
        TheBridgeArena arena = (TheBridgeArena) match.getArena();

        if (match.getTeamPlayerA().getPlayer() == player) {
            return arena.getRedPortal().contains(player.getLocation());
        } else {
            return arena.getBluePortal().contains(player.getLocation());
        }
    }

    public boolean isTeamPortalTeam(Player player) {
        Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());
        TeamBridgeMatch match = (TeamBridgeMatch) profile.getMatch();
        TheBridgeArena arena = (TheBridgeArena) match.getArena();

        if (match.getTeamA().containsPlayer(player)) {
            return arena.getRedPortal().contains(player.getLocation());
        } else {
            return arena.getBluePortal().contains(player.getLocation());
        }
    }

}