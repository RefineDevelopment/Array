

package me.drizzy.practice.party.command;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.enums.PartyPrivacyType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

@CommandMeta(label = { "party invite", "p invite" })
public class PartyInviteCommand
{
    public void execute(final Player player, @CPL("player") final Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }
        if (profile.getParty().getInvite(target.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "That player has already been invited to your party.");
            return;
        }
        if (profile.getParty().containsPlayer(target)) {
            player.sendMessage(CC.RED + "That player is already in your party.");
            return;
        }
        if (profile.getParty().getPrivacy() == PartyPrivacyType.OPEN) {
            player.sendMessage(CC.RED + "The party state is Open. You do not need to invite players.");
            return;
        }
        final Profile targetData = Profile.getByUuid(target.getUniqueId());
        if (targetData.isBusy(target)) {
            player.sendMessage(target.getDisplayName() + CC.RED + " is currently busy.");
            return;
        }
        profile.getParty().invite(target);
    }
}
