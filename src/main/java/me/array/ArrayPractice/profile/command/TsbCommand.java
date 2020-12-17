

package me.array.ArrayPractice.profile.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "tsb", "togglesb", "sb toggle", "togglesidebar" })
public class TsbCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final boolean scoreboardstate = profile.getOptions().isShowScoreboard();
        player.sendMessage(CC.translate("&fScoreboard: " + (scoreboardstate ? "&cOff" : "&aOn")));
        profile.getOptions().setShowScoreboard(!scoreboardstate);
    }
}
