

package me.array.ArrayPractice.profile.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "tduel", "toggleduel", "dueltoggle" })
public class TduelCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final boolean scoreboardstate = profile.getOptions().isReceiveDuelRequests();
        player.sendMessage(CC.translate("&fReceiving Duels: " + (scoreboardstate ? "&cOff" : "&aOn")));
        profile.getOptions().setReceiveDuelRequests(!scoreboardstate);
    }
}
