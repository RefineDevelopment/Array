package xyz.refinedev.practice.match.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchPotionTrackTask extends BukkitRunnable {

    private final Player shooter;

    @Override
    public void run() {
        Profile shooterData = Profile.getByPlayer(shooter);

        if (shooterData.isInFight()) {
            int potions = 0;
            for ( ItemStack item : shooter.getInventory().getContents()) {
                if (item == null)
                    continue;
                if (item.getType() == Material.AIR)
                    continue;
                if (item.getType() != Material.POTION)
                    continue;
                if (item.getDurability() != (short) 16421)
                    continue;
                potions++;
            }
            shooterData.getMatch().getTeamPlayer(shooter).setPotions(potions);
        } else {
            cancel();
        }
    }
}
