package me.drizzy.practice.match.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "cancelmatch", permission = "array.staff")
public class CancelMatchCommand {
    public void execute(Player player, @CPL("target") Player target) {
        if (target == null) {
            player.sendMessage(CC.translate("&7That player doesn't exist!"));
            return;
        }
        Profile profile = Profile.getByPlayer(target);
        if (!profile.isInFight()) {
            player.sendMessage(CC.translate("&7That player is not in a fight!"));
            return;
        }
        profile.getMatch().onEnd();
        player.sendMessage(CC.translate("&7Successfully cancelled &c" + profile.getName() + "'s &7Match!"));
    }
}
