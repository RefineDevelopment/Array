package me.drizzy.practice.match.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "cancelmatch", permission = "array.staff")
public class CancelMatchCommand {
    public void execute(Player player, @CPL("Profile")Profile profile) {
        if(profile == null) {
            player.sendMessage(CC.translate("&7That player doesn't exist!"));
            return;
        }
        if (!profile.isInFight()) {
            player.sendMessage(CC.translate("&7That player is not in a fight!"));
            return;
        }
        if (profile.getMatch() == null) {
            player.sendMessage(CC.translate("&7That player is not in a fight!"));
            return;
        }
        profile.getMatch().end();
        player.sendMessage(CC.translate("&7Successfully cancelled &b" + profile.getName() + "'s &7Match!"));
    }
}
