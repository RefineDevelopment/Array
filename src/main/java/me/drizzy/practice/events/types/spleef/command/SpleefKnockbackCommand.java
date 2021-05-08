package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef setknockbackprofile", "spleef setkb", "spleef knockback", "spleef setknockback"}, permission = "array.dev")
public class SpleefKnockbackCommand {

    public void execute(Player player, @CPL("knockback-profile") String kb) {
        if (kb == null) {
            player.sendMessage(CC.translate("&7Please specify a Knockback Profile."));
        } else {
            Array.getInstance().getSpleefManager().setSpleefKnockbackProfile(kb);
            player.sendMessage(CC.translate("&7Successfully updated the knockback profile to &c " + kb));
        }
    }
}
