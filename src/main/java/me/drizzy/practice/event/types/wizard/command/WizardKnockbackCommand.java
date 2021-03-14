package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "wizard setknockbackprofile", permission = "array.staff")
public class WizardKnockbackCommand {

    public void execute(Player player, @CPL("knockback-profile") String kb) {
        if (kb == null) {
            player.sendMessage(CC.RED + "Please Specify a Knockback Profile.");
        }
        else {
            Array.getInstance().getWizardManager().setWizardKnockbackProfile(kb);
            player.sendMessage(CC.GREEN + "Successfully set the knockback profile!");
        }
    }
}
