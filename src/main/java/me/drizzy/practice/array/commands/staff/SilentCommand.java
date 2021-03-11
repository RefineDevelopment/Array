package me.drizzy.practice.array.commands.staff;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "silent", permission = "array.staff")
public class SilentCommand {


    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&cYou are currently following somebody!"));
            return;
        }

        profile.setSilent(!profile.isSilent());

        player.sendMessage(CC.translate("&7You have " + (profile.isSilent() ? "&aenabled" : "&cdisabled") + " &7silent mode."));
    }

    public void execute(Player player, @CPL("player") Player target) {
        Profile profile = Profile.getByUuid(target.getUniqueId());

        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&cThat person is currently following somebody!"));
            return;
        }

        profile.setSilent(!profile.isSilent());

        player.sendMessage(CC.translate("&7You have " + (profile.isSilent() ? "&aenabled" : "&cdisabled") + " &7silent mode for " + target.getName() + "."));
    }

}
