package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo setknockbackprofile", permission = "array.dev")
public class SumoKnockbackCommand {

    public void execute(Player player, @CPL("nms-profile") String kb) {
          if (kb == null) {
              player.sendMessage(CC.RED + "Please Specify a Knockback Profile.");
          }
          else {
              Array.getInstance().getSumoManager().setSumoKnockbackProfile(kb);
              player.sendMessage(CC.GREEN + "Successfully set the nms profile!");
          }
    }
}
