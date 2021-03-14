package me.drizzy.practice.array.commands.donator;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "fly", permission = "array.donator+")
public class FlyCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if ((profile.isInLobby() || profile.isInQueue()) || profile.getPlayer().hasPermission("array.staff")) {
            if (player.hasPermission("array.donator+") || profile.getPlayer().hasPermission("array.staff")) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.updateInventory();
                    player.sendMessage(CC.GRAY + "You are no longer flying.");
                } else {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.updateInventory();
                    player.sendMessage(CC.AQUA + "You are now flying.");
                }
            }
        } else {
            player.sendMessage(CC.RED + "You cannot fly right now.");
        }
    }

}
