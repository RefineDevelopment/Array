package me.drizzy.practice.match.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spectate", "spec"})
public class SpectateCommand {

    public void execute(Player player, Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }

        Profile playerProfile = Profile.getByUuid(player.getUniqueId());

        if (playerProfile.isBusy()) {
            player.sendMessage(CC.RED + "You must be in the lobby and not queueing to spectate.");
            return;
        }

        if (playerProfile.getParty() != null) {
            player.sendMessage(CC.RED + "You must leave your party to spectate a match.");
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (!targetProfile.getSettings().isAllowSpectators() && !player.hasPermission("array.staff")) {
            player.sendMessage(CC.RED + "That player is not allowing spectators.");
            return;
        }

        if(targetProfile.getMatch() != null) {
            if (!targetProfile.getMatch().isFreeForAllMatch()) {
                for ( TeamPlayer teamPlayer : targetProfile.getMatch().getTeamPlayers() ) {
                    Player inMatchPlayer=teamPlayer.getPlayer();
                    if (inMatchPlayer != null) {
                        Profile inMatchProfile=Profile.getByUuid(inMatchPlayer.getUniqueId());

                        if (!inMatchProfile.getSettings().isAllowSpectators() && !player.hasPermission("array.staff")) {
                            player.sendMessage(CC.RED + "This match includes player that is not allowing spectators.");
                            return;
                        }
                    }
                }
            }
        }

        if (targetProfile.isInFight() || targetProfile.isInTournament()) {
            targetProfile.getMatch().addSpectator(player, target);
        } else if (targetProfile.isInSumo()) {
            targetProfile.getSumo().addSpectator(player);
            PlayerUtil.spectator(player);
            player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());
        } else if (targetProfile.isInBrackets()) {
            targetProfile.getBrackets().addSpectator(player);
            PlayerUtil.spectator(player);
            player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());
        } else if (targetProfile.isInLMS()) {
            targetProfile.getLms().addSpectator(player);
            PlayerUtil.spectator(player);
            player.teleport(Array.getInstance().getLMSManager().getLmsSpawn());
        } else if (targetProfile.isInParkour()) {
            targetProfile.getParkour().addSpectator(player);
            PlayerUtil.spectator(player);
            player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
        } else if (targetProfile.isInSpleef()) {
            targetProfile.getSpleef().addSpectator(player);
            PlayerUtil.spectator(player);
            player.teleport(Array.getInstance().getSpleefManager().getSpleefSpawn());
        } else {
            player.sendMessage(CC.RED + "That player is not in a match or running events.");
        }

    }

}
