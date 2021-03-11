package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.lms.LMS;
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
            player.sendMessage(CC.RED + "There isn't any active LMS Events.");
            return;
        }

        if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active LMS Event.");
            return;
        }

        Array.getInstance().getLMSManager().getActiveLMS().handleLeave(player);
    }

}
