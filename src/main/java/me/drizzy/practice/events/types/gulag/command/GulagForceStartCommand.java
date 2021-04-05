package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="gulag forcestart", permission="array.staff")
public class GulagForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getGulag().onRound();
    }
}
