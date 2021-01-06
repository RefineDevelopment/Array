package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit setkb", "kit setknockback"}, permission = "practice.kit")
public class KitSetKnockbackProfileCommand {

    public void execute(Player player, Kit kit, @CPL("KnockbackProfile") String knockbackProfile) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        kit.setKnockbackProfile(knockbackProfile);
        kit.save();

        player.sendMessage(CC.GREEN + "You updated the kit's knockbackprofile to" + knockbackProfile);
    }

}
