package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.lms.LMS;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms leave")
public class LMSLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = Practice.get().getLMSManager().getActiveLMS();

        if (activeLMS == null) {
            player.sendMessage(CC.RED + "There isn't any active LMS Events.");
            return;
        }

        if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active LMS Event.");
            return;
        }

        Practice.get().getLMSManager().getActiveLMS().handleLeave(player);
    }

}
