package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandMeta(label = {"follow"}, permission = "practice.command.follow")
public class FollowCommand {
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&cYou are already following somebody, /unfollow before following someone again."));
            return;
        }
        profile.setFollowMode(true);
        profile.setSilent(true);
        profile.setFollowing(target);
        Profile.getByUuid(target.getUniqueId()).getFollower().add(player);
        player.sendMessage(CC.translate("&7You have &astarted &7following " + target.getName() + "."));

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        if (targetProfile.isInSomeSortOfFight()) {
            if (targetProfile.isInMatch()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(Practice.get(), () -> player.chat("/spec " + target.getName()), 20L);
            }
        }
    }
}
