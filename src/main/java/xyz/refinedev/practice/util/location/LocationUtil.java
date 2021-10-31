package xyz.refinedev.practice.util.location;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.types.kit.BattleRushMatch;
import xyz.refinedev.practice.match.types.kit.MLGRushMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.profile.Profile;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class LocationUtil {

    public String serialize(@Nullable Location location) {
        if (location == null) return "empty";
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() +
                ":" + location.getYaw() + ":" + location.getPitch();
    }

    public Location deserialize(String source) {
        if (source == null) {
            return null;
        }

        String[] split = source.split(":");
        World world = Bukkit.getServer().getWorld(split[0]);

        if (world == null) {
            return null;
        }

        return new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public boolean isTeamPortalSolo(Player player) {
        Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());
        SoloBridgeMatch match = (SoloBridgeMatch) profile.getMatch();

        if (match.getTeamPlayerA().getPlayer() == player) {
            return checkIfListContainsLocation(match.getPlayerAPortals(), player.getLocation());
        } else {
            return checkIfListContainsLocation(match.getPlayerBPortals(), player.getLocation());
        }
    }

    public boolean isSelfPortal(Player player) {
        Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());
        BattleRushMatch match = (BattleRushMatch) profile.getMatch();

        if (match.getTeamPlayerA().getPlayer() == player) {
            return checkIfListContainsLocation(match.getPlayerAPortals(), player.getLocation());
        } else {
            return checkIfListContainsLocation(match.getPlayerBPortals(), player.getLocation());
        }
    }

    public boolean isSelfBed(Player player) {
        Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());

        if (profile.getMatch() instanceof SoloBedwarsMatch) {
            SoloBedwarsMatch match = (SoloBedwarsMatch) profile.getMatch();

            if (match.getTeamPlayerA().getPlayer() == player) {
                return checkIfListContainsLocation(match.getPlayerABed(), player.getLocation());
            } else {
                return checkIfListContainsLocation(match.getPlayerBBed(), player.getLocation());
            }
        } else if (profile.getMatch() instanceof TeamBedwarsMatch) {
            TeamBedwarsMatch match = (TeamBedwarsMatch) profile.getMatch();

            if (match.getTeamA().containsPlayer(player)) {
                //return checkIfListContainsLocation(match.getPlayerABed(), player.getLocation());
            } else {
                //return checkIfListContainsLocation(match.getPlayerBBed(), player.getLocation());
            }
        } else if (profile.getMatch() instanceof MLGRushMatch) {
            MLGRushMatch match = (MLGRushMatch) profile.getMatch();

            if (match.getTeamPlayerA().getPlayer() == player) {
                return checkIfListContainsLocation(match.getPlayerABed(), player.getLocation());
            } else {
                return checkIfListContainsLocation(match.getPlayerBBed(), player.getLocation());
            }
        }
        return false;
    }

    public boolean isTeamPortalTeam(Player player) {
        Profile profile = Array.getInstance().getProfileManager().getByUUID(player.getUniqueId());
        TeamBridgeMatch match = (TeamBridgeMatch) profile.getMatch();

        if (match.getTeamA().containsPlayer(player)) {
            return checkIfListContainsLocation(match.getTeamAPortals(), player.getLocation());
        } else {
            return checkIfListContainsLocation(match.getTeamBPortals(), player.getLocation());
        }
    }

    public List<Location> getCircle(Location center, float radius, int amount) {
        List<Location> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double a = 2 * Math.PI / amount * i;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            list.add(center.clone().add(x, 0, z));
        }
        return list;
    }

    public List<Location> getNearbyPortalLocations(Location start) {
        start = new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ());
        int radius = 20;
        List<Location> blocks = new ArrayList<>();
        for (double x = start.getX() - (double)radius; x <= start.getX() + (double)radius; x += 1.0) {
            for (double y = start.getY() - (double)radius; y <= start.getY() + (double)radius; y += 1.0) {
                for (double z = start.getZ() - (double)radius; z <= start.getZ() + (double)radius; z += 1.0) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    if (loc.getBlock().getType().equals(Material.ENDER_PORTAL)) blocks.add(loc);
                    if (blocks.size() <= 8) continue;
                    return blocks;
                }
            }
        }
        return blocks;
    }

    public List<Location> getNearbyBedLocations(Location start) {
        start = new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ());
        int radius = 10;

        List<Location> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x += 1.0) {
            for (double y = start.getY() - radius; y <= start.getY() + radius; y += 1.0) {
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z += 1.0) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    if (loc.getBlock().getType().equals(Material.BED_BLOCK)) blocks.add(loc);
                    if (blocks.size() <= 1) continue;
                    return blocks;
                }
            }
        }
        return blocks;
    }


    /**
     * Checks if the List contains the provided location
     *
     * @param locations {@link List}
     * @param toCheck   {@link Location}
     * @return          {@link Boolean}
     */
    public boolean checkIfListContainsLocation(List<Location> locations, Location toCheck) {
        for (Location loc : locations) {
            return loc.getBlockX() == toCheck.getBlockX() && loc.getBlockZ() == toCheck.getBlockZ();
        }
        return false;
    }

}