package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit setkb", "kit setknockback"}, permission = "array.dev")
public class KitSetKnockbackProfileCommand {

    public void execute(Player player, Kit kit, @CPL("KnockbackProfile") String knockbackProfile) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7That kit does not exist."));
            return;
        }

        kit.setKnockbackProfile(knockbackProfile);
        kit.save();

        player.sendMessage(CC.translate("&8[&b&lArray&8] &7Updated knockback profile for &b" + kit.getName() +  " &7to &b" + knockbackProfile));
    }

}
