

package me.array.ArrayPractice.party.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

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
