package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label={"spleef forcestart"}, permission="spleef.forcestart")
public class SpleefForceStartCommand {
    public void execute(Player player) {
        Profile profile =Profile.getByUuid(player);
        profile.getSpleef().onRound();
    }
}
