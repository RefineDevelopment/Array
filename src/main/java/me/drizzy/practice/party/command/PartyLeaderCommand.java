

package me.drizzy.practice.party.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

@CommandMeta(label = { "party leader", "p leader", "party promote", "p promote" })
public class PartyLeaderCommand
{
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }
        if (!profile.getParty().containsPlayer(target)) {
            player.sendMessage(CC.RED + "That player is not a member of your party.");
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(CC.RED + "You cannot yourself leader your party, because you have it already.");
            return;
        }
        profile.getParty().leader(player, target);
    }
}
