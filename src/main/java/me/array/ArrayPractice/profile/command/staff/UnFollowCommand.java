package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.*;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.*;

@CommandMeta(label = { "unfollow" }, permission = "practice.command.unfollow")
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
        profile.setSilent(false);
        profile.setFollowing(null);

        player.sendMessage(CC.translate("&7You have &cexited &7follow mode."));
    }
}
