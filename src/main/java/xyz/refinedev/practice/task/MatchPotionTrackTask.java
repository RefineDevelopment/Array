package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
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

    private final Array plugin;
    private final Match match;

    @Override
    public void run() {
        for ( Player shooter : match.getPlayers() ) {
            Profile shooterProfile = plugin.getProfileManager().getByPlayer(shooter);
            TeamPlayer teamPlayer = match.getTeamPlayer(shooter);

            if (teamPlayer == null)  continue;

            if (match.isEnding()) {
                this.cancel();
                return;
            } else if (!match.isFighting()) {
                return;
            }

            if (shooterProfile.isInFight()) {
                int potions = 0;
                for ( ItemStack item : shooter.getInventory().getContents() ) {
                    if (item == null) continue;
                    if (item.getType() == Material.AIR) continue;
                    if (item.getType() != Material.POTION) continue;
                    if (item.getDurability() != (short) 16421) continue;
                    potions++;
                }
                teamPlayer.setPotions(potions);
            }
        }
    }
}
