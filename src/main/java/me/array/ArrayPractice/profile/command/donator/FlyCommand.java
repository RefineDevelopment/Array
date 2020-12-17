

package me.array.ArrayPractice.profile.command.donator;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "fly" }, permission = "essentials.fly")
public class FlyCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInLobby() || profile.isInQueue() || profile.getPlayer().hasPermission("practice.staff")) {
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.updateInventory();
                player.sendMessage(CC.AQUA + "You are no longer flying.");
            }
            else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.updateInventory();
                player.sendMessage(CC.AQUA + "You are now flying.");
            }
        }
        else {
            player.sendMessage(CC.RED + "You cannot fly right now.");
        }
    }
}
