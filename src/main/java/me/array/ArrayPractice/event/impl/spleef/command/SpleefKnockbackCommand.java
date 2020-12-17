package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "spleef setknockbackprofile", permission = "practice.staff")
public class SpleefKnockbackCommand {

    public void execute(Player player, @CPL("knockback-profile") String kb) {
        if (kb == null) {
            player.sendMessage(CC.RED + "Please Specify a Knockback Profile.");
        }
        else {
            Array.get().getSpleefManager().setSpleefKnockbackProfile(kb);
            player.sendMessage(CC.GREEN + "Successfully set the knockback profile!");
        }
    }
}
