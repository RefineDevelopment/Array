

package me.drizzy.practice.party.command;

import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.enums.PartyPrivacyType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

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
        if (party.getPrivacy() == PartyPrivacyType.CLOSED && party.getInvite(player.getUniqueId()) == null) {
            player.sendMessage(CC.RED + "You have not been invited to that party.");
            return;
        }
        if (Tournament.CURRENT_TOURNAMENT != null) {
            for (final Player pplayer : party.getPlayers()) {
                if (Tournament.CURRENT_TOURNAMENT.isParticipating(pplayer)) {
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
