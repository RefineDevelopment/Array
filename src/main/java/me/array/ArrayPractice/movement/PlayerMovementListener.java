package me.array.ArrayPractice.movement;

import me.array.ArrayPractice.event.impl.sumo.SumoManager;
import me.array.ArrayPractice.event.impl.sumo.SumoState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Location to = e.getTo();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (new SumoManager().getActiveSumo() == null) {
            return;
        }

        if (profile.isInSumo() || profile.getSumo().getState() == SumoState.ROUND_FIGHTING)
            if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
                profile.getSumo().handleDeath(player);
        }
    }
}