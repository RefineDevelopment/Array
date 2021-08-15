package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms leave")
public class LMSLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = Array.getInstance().getLMSManager().getActiveLMS();

        if (activeLMS == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMS"));
            return;
        }

        if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "LMS"));
            return;
        }
        activeLMS.handleLeave(player);
    }

}
