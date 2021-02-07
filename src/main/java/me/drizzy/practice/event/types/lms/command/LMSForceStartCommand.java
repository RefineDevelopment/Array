package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label="lms forcestart", permission="lms.forcestart")
public class LMSForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getLms().onRound();
    }
}
