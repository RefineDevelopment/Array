package me.drizzy.practice.party.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.command.command.CommandMeta;

@CommandMeta(label = { "party create", "p create" })
public class PartyCreateCommand {

    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() != null) {
            player.sendMessage(CC.RED + "You already have a party.");
            return;
        }
        if (!profile.isInLobby()) {
            player.sendMessage(CC.RED + "You must be in the lobby to create a party.");
            return;
        }
        profile.setParty(new Party(player));
        profile.refreshHotbar();
        player.sendMessage(Locale.PARTY_CREATED.toString());
    }
}
