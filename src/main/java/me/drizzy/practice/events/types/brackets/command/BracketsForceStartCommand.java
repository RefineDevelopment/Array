package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label="brackets forcestart", permission="array.staff")
public class BracketsForceStartCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getBrackets().onRound();
    }
}
