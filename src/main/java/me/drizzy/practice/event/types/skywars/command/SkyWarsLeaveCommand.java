package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars leave")
public class SkyWarsLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        SkyWars activeSkyWars = Array.getInstance().getSkyWarsManager().getActiveSkyWars();

        if (activeSkyWars == null) {
            player.sendMessage(CC.RED + "There isn't any active SkyWars Events.");
            return;
        }

        if (!profile.isInSkyWars() || !activeSkyWars.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active SkyWars Event.");
            return;
        }

        Array.getInstance().getSkyWarsManager().getActiveSkyWars().handleLeave(player);
    }

}
