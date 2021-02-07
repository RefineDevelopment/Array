package me.drizzy.practice.event.types.spleef;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class SpleefResetTask extends BukkitRunnable {

    private Spleef match;

    @Override
    public void run() {
        if (match.getPlacedBlocks().size() > 0) {
            match.getPlacedBlocks().forEach(l -> l.getBlock().setType(Material.AIR));
            match.getPlacedBlocks().clear();
        }
        if (match.getChangedBlocks().size() > 0) {
            match.getChangedBlocks().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
            match.getChangedBlocks().clear();
        }
    }

}