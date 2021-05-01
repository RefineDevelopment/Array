package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label={"parkour forcestart"}, permission="array.staff")
public class ParkourForceStartCommand {
    public void execute(Player player) {
        Profile profile =Profile.getByPlayer(player);
        profile.getParkour().onRound();
    }
}
