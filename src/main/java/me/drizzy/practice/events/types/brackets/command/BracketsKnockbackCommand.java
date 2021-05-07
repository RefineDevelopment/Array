package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets setknockbackprofile", "brackets setkb", "brackets knockback", "brackets setknockback"}, permission = "array.dev")
public class BracketsKnockbackCommand {

    public void execute(Player player, @CPL("knockback-profile") String kb) {
        if (kb == null) {
            player.sendMessage(CC.translate("&7Please specify a Knockback Profile."));
        } else {
            Array.getInstance().getBracketsManager().setBracketsKnockbackProfile(kb);
            player.sendMessage(CC.translate("&7Successfully updated the knockback profile to &c " + kb));
        }
    }
}
