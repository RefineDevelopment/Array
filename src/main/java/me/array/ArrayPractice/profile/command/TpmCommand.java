

package me.array.ArrayPractice.profile.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "tpm", "togglepm", "togglemsg", "msgtoggle" })
public class TpmCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final boolean scoreboardstate = profile.getOptions().isPrivateMessages();
        player.sendMessage(CC.translate("&fPrivate Messages: " + (scoreboardstate ? "&cOff" : "&aOn")));
        profile.getOptions().setPrivateMessages(!scoreboardstate);
    }
}
