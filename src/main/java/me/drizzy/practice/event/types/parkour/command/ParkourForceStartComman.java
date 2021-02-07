package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label={"parkour forcestart"}, permission="parkour.forcestart")
public class ParkourForceStartComman {
    public void execute(Player player) {
        Profile profile =Profile.getByUuid(player);
        profile.getParkour().onRound();
    }
}
