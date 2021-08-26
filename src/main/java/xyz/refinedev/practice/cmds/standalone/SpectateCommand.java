package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
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
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        if (profile.getParty() != null) {
            player.sendMessage(Locale.ERROR_PARTY.toString());
            return;
        }

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (!targetProfile.getSettings().isAllowSpectators() && !player.hasPermission("array.profile.silent")) {
            player.sendMessage(Locale.ERROR_NOSPEC.toString());
            return;
        }

        if (targetProfile.getMatch() != null && !targetProfile.getMatch().isFreeForAllMatch()) {
            for ( TeamPlayer teamPlayer : targetProfile.getMatch().getTeamPlayers() ) {
                Player inMatchPlayer = teamPlayer.getPlayer();
                if (inMatchPlayer != null) {
                    Profile inMatchProfile = Profile.getByUuid(inMatchPlayer.getUniqueId());

                    if (!inMatchProfile.getSettings().isAllowSpectators() && !player.hasPermission("array.profile.silent")) {
                        player.sendMessage(Locale.ERROR_MATCHNOSPEC.toString());
                        return;
                    }
                }
            }
        }

        if (targetProfile.isInFight() || targetProfile.isInTournament()) {
            targetProfile.getMatch().addSpectator(player, target);
        } else if (targetProfile.isInEvent()) {
            targetProfile.getEvent().addSpectator(player);
        } else {
            player.sendMessage(Locale.ERROR_FREE.toString());
        }
    }

    @Command(name = "show", aliases = "view", desc = "Show spectators")
    public void show(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (!profile.isInMatch()) {
            player.sendMessage(Locale.MATCH_NOT_IN.toString());
            return;
        } else if (!profile.isSpectating()) {
            player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
            return;
        }
        profile.getMatch().toggleSpectators(player);
    }

    @Command(name = "hide", aliases = "disable", desc = "Hide spectators")
    public void hide(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (!profile.isInMatch()) {
            player.sendMessage(Locale.MATCH_NOT_IN.toString());
            return;
        } else if (!profile.isSpectating()) {
            player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
            return;
        }
        profile.getMatch().toggleSpectators(player);
    }
}
