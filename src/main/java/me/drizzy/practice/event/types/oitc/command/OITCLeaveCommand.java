package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.oitc.OITC;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "OITC leave")
public class OITCLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        OITC activeOITC = Array.getInstance().getOITCManager().getActiveOITC();

        if (activeOITC == null) {
            player.sendMessage(CC.RED + "There isn't any active OITC Events.");
            return;
        }

        if (!profile.isInOITC() || !activeOITC.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active OITC Event.");
            return;
        }

        Array.getInstance().getOITCManager().getActiveOITC().handleLeave(player);
    }

}
