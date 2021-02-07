package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.event.types.skywars.SkyWarsState;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars join")
public class SkyWarsJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        SkyWars activeSkyWars = Array.getInstance().getSkyWarsManager().getActiveSkyWars();

        if (profile.isBusy(player) || profile.getParty() != null) {
            player.sendMessage(CC.RED + "You cannot join the skywars right now.");
            return;
        }

        if (activeSkyWars == null) {
            player.sendMessage(CC.RED + "There isn't any active SkyWars Events right now.");
            return;
        }

        if (activeSkyWars.getState() != SkyWarsState.WAITING) {
            player.sendMessage(CC.RED + "This SkyWars Event is currently on-going and cannot be joined.");
            return;
        }

        Array.getInstance().getSkyWarsManager().getActiveSkyWars().handleJoin(player);
    }

}
