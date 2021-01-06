package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"skywars host"}, permission = "practice.skywars.host")
public class SkyWarsHostCommand {

    public static void execute(Player player) {
        if (Practice.get().getSkyWarsManager().getActiveSkyWars() != null) {
            player.sendMessage(CC.RED + "There is already an active SkyWars Event.");
            return;
        }

        if (!Practice.get().getSkyWarsManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the SkyWars Event.");
            return;
        }

        Practice.get().getSkyWarsManager().setActiveSkyWars(new SkyWars(player));

        for (Player other : Practice.get().getServer().getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(other.getUniqueId());

            if (profile.isInLobby()) {
                if (!profile.getKitEditor().isActive()) {
                    profile.refreshHotbar();
                }
            }
        }
    }

}
