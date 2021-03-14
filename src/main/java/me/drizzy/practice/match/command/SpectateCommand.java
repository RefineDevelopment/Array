package me.drizzy.practice.match.command;

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

        if (playerProfile.isBusy(player)) {
            player.sendMessage(CC.RED + "You must be in the lobby and not queueing to spectate.");
            return;
        }

        if (playerProfile.getParty() != null) {
            player.sendMessage(CC.RED + "You must leave your party to spectate a match.");
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (targetProfile.isInTournament(target)) {
            player.sendMessage(CC.RED + "That player is in a tournament, Tournament spectating is currently in development!");
            return;
        }

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

        if (targetProfile.isInFight()) {
            targetProfile.getMatch().addSpectator(player, target);
        } else if (targetProfile.isInSumo()) {
            targetProfile.getSumo().addSpectator(player);
        } else if (targetProfile.isInBrackets()) {
            targetProfile.getBrackets().addSpectator(player);
        } else if (targetProfile.isInLMS()) {
            targetProfile.getLms().addSpectator(player);
        } else if (targetProfile.isInParkour()) {
            targetProfile.getParkour().addSpectator(player);
        } else if (targetProfile.isInSpleef()) {
            targetProfile.getSpleef().addSpectator(player);
        } else {
            player.sendMessage(CC.RED + "That player is not in a match or running event.");
        }

    }

}
