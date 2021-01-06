package me.array.ArrayPractice.event.impl.sumo.task;

import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SumoWaterCheck extends BukkitRunnable {
    private Sumo sumo;

    public SumoWaterCheck(Sumo sumo) {
        this.sumo = sumo;
    }

    @Override
    public void run() {
        if(sumo == null || sumo.getRemainingPlayers().isEmpty() || sumo.getRemainingPlayers().size() <= 1){
            return;
        }

        for(Player player : sumo.getRemainingPlayers()){
            if(player == null || Profile.getByUuid(player.getUniqueId()).getState() != ProfileState.IN_EVENT){
                return;
            }


            Block legs = player.getLocation().getBlock();
            Block head = legs.getRelative(BlockFace.UP);
            if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                sumo.handleDeath(player);
            }
        }
    }
}