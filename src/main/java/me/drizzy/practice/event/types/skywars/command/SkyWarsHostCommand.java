package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"skywars host"}, permission = "practice.host.skywars")
public class SkyWarsHostCommand {

    public static void execute(Player player) {
        if (Array.getInstance().getSkyWarsManager().getActiveSkyWars() != null) {
            player.sendMessage(CC.RED + "There is already an active SkyWars Event.");
            return;
        }

        if (!Array.getInstance().getSkyWarsManager().getCooldown().hasExpired()) {
            player.sendMessage(CC.RED + "There is an active cooldown for the SkyWars Event.");
            return;
        }

        Array.getInstance().getSkyWarsManager().setActiveSkyWars(new SkyWars(player));

        for (Player other : Array.getInstance().getServer().getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(other.getUniqueId());

            if (profile.isInLobby()) {
                if (!profile.getKitEditor().isActive()) {
                PlayerUtil.reset(player, false);
                profile.refreshHotbar();
                }
            }
        }
    }

}
