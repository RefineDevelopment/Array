package me.drizzy.practice.party.command;

import me.drizzy.practice.enums.PartyPrivacyType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

@CommandMeta(label = { "party open", "p open" })
public class PartyOpenCommand
{
    public void execute(final Player player) {
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
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }
        profile.getParty().setPrivacy(PartyPrivacyType.OPEN);
    }
}
