package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tsb", "togglesb", "sb toggle", "togglesidebar"})
public class ArrayToggleScoreboardCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean scoreboardstate = profile.getSettings().isShowScoreboard();
        player.sendMessage(CC.translate("&7Scoreboard: " + (!scoreboardstate ? "&aOn" : "&cOff")));
        profile.getSettings().setShowScoreboard(!scoreboardstate);
    }

}
