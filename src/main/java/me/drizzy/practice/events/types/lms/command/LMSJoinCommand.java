package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.lms.LMSState;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms join")
public class LMSJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = Array.getInstance().getLMSManager().getActiveLMS();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeLMS == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMS"));
            return;
        }

        if (activeLMS.getState() != LMSState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARED.toString().replace("<event>", "LMS"));
            return;
        }
        activeLMS.handleJoin(player);
    }

}
