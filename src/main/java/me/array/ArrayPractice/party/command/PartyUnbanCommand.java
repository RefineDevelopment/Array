

package me.array.ArrayPractice.party.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CPL;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "party unban", "p unban" })
public class PartyUnbanCommand
{
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!player.hasPermission("practice.donator")) {
            player.sendMessage(CC.translate("&7You do not have permission to use Party Settings."));
            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
            return;
        }
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
            player.sendMessage(CC.RED + "You cannot unban yourself from your party.");
            return;
        }
        if (!profile.getParty().getBanned().contains(target)) {
            player.sendMessage(CC.RED + "That player is not banned from your party.");
            return;
        }
        player.sendMessage(CC.GREEN + "Successfully unbanned that player");
        profile.getParty().unban(target);
    }
}
