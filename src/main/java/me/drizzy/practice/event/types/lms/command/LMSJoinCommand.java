package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.lms.LMSState;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms join")
public class LMSJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = Array.getInstance().getLMSManager().getActiveLMS();

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

        Array.getInstance().getLMSManager().getActiveLMS().handleJoin(player);
    }

}
