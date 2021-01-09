package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.skywars.SkyWarsState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars join")
public class SkyWarsJoinCommand {

    public static void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        SkyWars activeSkyWars = Practice.getInstance().getSkyWarsManager().getActiveSkyWars();

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

        Practice.getInstance().getSkyWarsManager().getActiveSkyWars().handleJoin(player);
    }

}
