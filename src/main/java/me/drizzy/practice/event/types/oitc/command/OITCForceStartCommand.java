package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="OITC forcestart", permission="OITC.forcestart")
public class OITCForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getOITC().onRound();
    }
}
