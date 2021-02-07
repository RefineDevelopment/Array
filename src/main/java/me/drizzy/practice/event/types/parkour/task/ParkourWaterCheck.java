package me.drizzy.practice.event.types.parkour.task;

import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParkourWaterCheck extends BukkitRunnable {
    private Parkour parkour;

    public ParkourWaterCheck(Parkour parkour) {
        this.parkour = parkour;
    }

    @Override
    public void run() {
        if(parkour == null || parkour.getRemainingPlayers().isEmpty() || parkour.getRemainingPlayers().size() <= 1){
            return;
        }

        for(Player player : parkour.getRemainingPlayers()){
            if(player == null || Profile.getByUuid(player.getUniqueId()).getState() != ProfileState.IN_EVENT){
                return;
            }

            Profile profile = Profile.getByUuid(player.getUniqueId());
            Block legs = player.getLocation().getBlock();
            Block head = legs.getRelative(BlockFace.UP);
            if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                if (profile.getParkour().isFighting(player)) {
                    if (profile.getParkour().getEventPlayer(player).getLastLocation() != null) {
                        player.teleport(profile.getParkour().getEventPlayer(player).getLastLocation());
                    } else {
                        player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
                    }
                }
            }
        }
    }
}