package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.oitc.OITC;
import me.drizzy.practice.event.types.oitc.OITCState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "OITC join")
public class OITCJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        OITC activeOITC = Array.getInstance().getOITCManager().getActiveOITC();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the OITC right now.");
            return;
        }

        if (activeOITC == null) {
            player.sendMessage(CC.RED + "There isn't any active KoTH Events right now.");
            return;
        }

        if (activeOITC.getState() != OITCState.WAITING) {
            player.sendMessage(CC.RED + "This KoTH Event is currently on-going and cannot be joined.");
            return;
        }

        Array.getInstance().getOITCManager().getActiveOITC().handleJoin(player);
    }

}
