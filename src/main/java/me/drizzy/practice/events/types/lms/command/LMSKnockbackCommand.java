package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo setknockbackprofile", permission = "array.dev")
public class LMSKnockbackCommand {

    public void execute(Player player, @CPL("knockback-profile") String kb) {
          if (kb == null) {
              player.sendMessage(CC.RED + "Please Specify a Knockback Profile.");
          }
          else {
              Array.getInstance().getLMSManager().setLmsKnockbackProfile(kb);
              player.sendMessage(CC.GREEN + "Successfully set the knockback profile!");
          }
    }
}
