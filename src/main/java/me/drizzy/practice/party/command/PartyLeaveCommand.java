

package me.drizzy.practice.party.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

@CommandMeta(label = { "party leave", "p leave" })
public class PartyLeaveCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }
        if (profile.getParty().getLeader().getUuid().equals(player.getUniqueId())) {
            profile.getParty().disband();
        }
        else {
            profile.getParty().leave(player, false);
        }
    }
}
