package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class SpectateCommand {

    @Command(name = "", desc = "Spectate a target player", usage = "<target>")
    public void spectate(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_UNAVAILABLE.toString());
            return;
        }

        if (profile.getParty() != null) {
            player.sendMessage(Locale.ERROR_PARTY.toString());
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (!targetProfile.getSettings().isAllowSpectators() && !player.hasPermission("array.staff.silent")) {
            player.sendMessage(Locale.ERROR_NOSPEC.toString());
            return;
        }

        if (targetProfile.getMatch() != null && !targetProfile.getMatch().isFreeForAllMatch()) {
            for ( TeamPlayer teamPlayer : targetProfile.getMatch().getTeamPlayers() ) {
                Player inMatchPlayer = teamPlayer.getPlayer();
                if (inMatchPlayer != null) {
                    Profile inMatchProfile = Profile.getByUuid(inMatchPlayer.getUniqueId());

                    if (!inMatchProfile.getSettings().isAllowSpectators() && !player.hasPermission("array.staff.silent")) {
                        player.sendMessage(Locale.ERROR_MATCHNOSPEC.toString());
                        return;
                    }
                }
            }
        }

        if (targetProfile.isInFight() || targetProfile.isInTournament()) {
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
            player.sendMessage(Locale.ERROR_FREE.toString());
        }
    }
}
