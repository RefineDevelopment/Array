package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "gulag setknockbackprofile", permission = "array.dev")
public class GulagKnockbackCommand {

    public void execute(Player player, @CPL("knockback-profile") String kb) {
        if (kb == null) {
            player.sendMessage(CC.RED + "Please Specify a Knockback Profile.");
        }
        else {
            Array.getInstance().getGulagManager().setGulagKnockbackProfile(kb);
            player.sendMessage(CC.GREEN + "Successfully set the knockback profile!");
        }
    }
}
