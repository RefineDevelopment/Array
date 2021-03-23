package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"sumo forcestart"}, permission="array.staff")
public class SumoForceStartCommand {
    public void execute(Player player) {
        Profile profile =Profile.getByUuid(player);
        profile.getSumo().onRound();
    }
}
