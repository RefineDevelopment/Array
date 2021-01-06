package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tsb", "togglesb", "sb toggle", "togglesidebar"})
public class TsbCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean scoreboardstate = profile.getOptions().isShowScoreboard();
        player.sendMessage(CC.translate("&7Scoreboard: " + (!scoreboardstate ? "&aOn" : "&cOff")));
        profile.getOptions().setShowScoreboard(!scoreboardstate);
    }

}
