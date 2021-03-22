package me.drizzy.practice.array.commands;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arraysecretcommand", permission = "array.dev")
public class ArrayDebugCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player);
        if (profile.getKitEditor().isActive()) {
            player.sendMessage("You are still editing");
            player.sendMessage(profile.getState().toString());
        } else {
            player.sendMessage("No you are not editing");
        }
    }
}
