

package me.array.ArrayPractice.party.command;

import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.party.PartyPrivacy;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.tournament.TournamentManager;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "party join", "p join" })
public class PartyJoinCommand
{
    public void execute(final Player player, final Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }
        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot join a party while frozen.");
            return;
        }
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isBusy(player)) {
            player.sendMessage(CC.RED + "You can not do that right now");
            return;
        }
        if (profile.getParty() != null) {
            player.sendMessage(CC.RED + "You already have a party.");
            return;
        }
        final Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        final Party party = targetProfile.getParty();
        if (party == null) {
            player.sendMessage(CC.RED + "A party with that name could not be found.");
            return;
        }
        if (party.getPrivacy() == PartyPrivacy.CLOSED && party.getInvite(player.getUniqueId()) == null) {
            player.sendMessage(CC.RED + "You have not been invited to that party.");
            return;
        }
        if (TournamentManager.CURRENT_TOURNAMENT != null) {
            for (final Player pplayer : party.getPlayers()) {
                if (TournamentManager.CURRENT_TOURNAMENT.isParticipating(pplayer)) {
                    player.sendMessage(CC.RED + "The party is in tournament");
                    return;
                }
            }
        }
        if (party.getPlayers().size() >= party.getLimit()) {
            player.sendMessage(CC.RED + "That party is full and cannot hold anymore players.");
            return;
        }
        if (party.getBanned().contains(player)) {
            player.sendMessage(CC.RED + "You have been banned from that party");
            return;
        }
        party.join(player);
    }
}
