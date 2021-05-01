package me.drizzy.practice.essentials.commands.staff;

import com.lunarclient.bukkitapi.LunarClientAPI;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.*;

@CommandMeta(label = { "unfollow" }, permission = "array.staff")
public class UnFollowCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.isFollowMode()) {
            player.sendMessage(CC.translate("&cYou aren't following anybody."));
            return;
        }

        Profile.getByUuid(profile.getFollowing().getUniqueId()).getFollower().remove(player);
        profile.setFollowMode(false);
        profile.setFollowing(null);

        player.sendMessage(CC.translate("&7You have &cexited &7follow mode."));
        if (LunarClientAPI.getInstance().isRunningLunarClient(player)) {
            LunarClientAPI.getInstance().giveAllStaffModules(player);
        }
    }
}
