

package me.array.ArrayPractice.party.command;

import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.party.PartyMessage;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "party create", "p create" })
public class PartyCreateCommand
{
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
        player.sendMessage(PartyMessage.CREATED.format(new Object[0]));
    }
}
