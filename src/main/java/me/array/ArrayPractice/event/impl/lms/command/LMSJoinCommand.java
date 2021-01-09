package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.lms.LMS;
import me.array.ArrayPractice.event.impl.lms.LMSState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms join")
public class LMSJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = Practice.getInstance().getLMSManager().getActiveLMS();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the lms right now.");
            return;
        }

        if (activeLMS == null) {
            player.sendMessage(CC.RED + "There isn't any active LMS Events right now.");
            return;
        }

        if (activeLMS.getState() != LMSState.WAITING) {
            player.sendMessage(CC.RED + "This LMS Event is currently on-going and cannot be joined.");
            return;
        }

        Practice.getInstance().getLMSManager().getActiveLMS().handleJoin(player);
    }

}
