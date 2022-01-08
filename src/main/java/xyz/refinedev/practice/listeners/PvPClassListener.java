package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.events.ArmorEquipEvent;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/9/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class PvPClassListener implements Listener {

    private final Array plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInFight()) return;

        Match match = profile.getMatch();
        if (!match.isHCFMatch()) return;

        plugin.getPvpClassManager().setEquippedClass(player, null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorChange(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isInFight()) return;

        Match match = profile.getMatch();
        if (!match.isHCFMatch()) return;

        plugin.getPvpClassManager().attemptEquip(player);
    }
}
