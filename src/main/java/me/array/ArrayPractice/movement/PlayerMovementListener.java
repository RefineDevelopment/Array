package me.array.ArrayPractice.movement;

import me.array.ArrayPractice.event.impl.sumo.SumoState;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Location to = e.getTo();
        Location from = e.getFrom();
        Profile playerData =Profile.getByUuid(player.getUniqueId());

        if (playerData.getState() == ProfileState.IN_FIGHT) {
            Match match = playerData.getMatch();
            Player opponentPlayer = playerData.getMatch().getOpponentPlayer(player);

            if (match == null) {
                return;
            }

            if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isSpleef()) {

                if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
                    match.handleDeath(player, opponentPlayer, false);
                }


                if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
                    if (match.getState() == MatchState.STARTING) {
                        player.teleport(from);
                        ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                    }
                }
            }
        }
        if (playerData.isInEvent() && playerData.isInSumo()) {
          if (playerData.getSumo().getState() == SumoState.ROUND_FIGHTING)
            if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
                playerData.getSumo().handleDeath(player);
            }
        }
    }
}