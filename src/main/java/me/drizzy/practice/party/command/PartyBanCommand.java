

package me.drizzy.practice.party.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

@CommandMeta(label = { "party ban", "p ban" })
public class PartyBanCommand
{
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!player.hasPermission("array.donator")) {
            player.sendMessage(CC.translate("&7You do not have permission to use Party Settings."));
            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.purgemc.club &7!"));
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
