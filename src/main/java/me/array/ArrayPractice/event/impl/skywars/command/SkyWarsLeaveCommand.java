package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars leave")
public class SkyWarsLeaveCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        SkyWars activeSkyWars = Practice.get().getSkyWarsManager().getActiveSkyWars();

        if (activeSkyWars == null) {
            player.sendMessage(CC.RED + "There isn't any active SkyWars Events.");
            return;
        }

        if (!profile.isInSkyWars() || !activeSkyWars.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not apart of the active SkyWars Event.");
            return;
        }

        Practice.get().getSkyWarsManager().getActiveSkyWars().handleLeave(player);
    }

}
