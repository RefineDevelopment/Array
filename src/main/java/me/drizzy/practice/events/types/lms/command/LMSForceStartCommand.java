package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label="lms forcestart", permission="array.dev")
public class LMSForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getLms() == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMS"));
            return;
        }
        profile.getLms().onRound();
        player.sendMessage(CC.translate("&7Successfully force started the &cLMS Event&7!"));
    }
}
