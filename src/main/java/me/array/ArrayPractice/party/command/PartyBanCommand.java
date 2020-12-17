

package me.array.ArrayPractice.party.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CPL;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "party ban", "p ban" })
public class PartyBanCommand
{
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }
        if (target == null) {
            player.sendMessage(CC.RED + "That player is not online");
            return;
        }
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(CC.RED + "You cannot ban yourself from your party.");
            return;
        }
        if (profile.getParty().getBanned().contains(target)) {
            player.sendMessage(CC.RED + "That player is already banned.");
            return;
        }
        if (profile.getParty().containsPlayer(target)) {
            profile.getParty().leave(target, true);
        }
        player.sendMessage(CC.GREEN + "Successfully banned that player");
        profile.getParty().ban(target);
    }
}
