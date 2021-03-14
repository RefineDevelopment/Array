package me.drizzy.practice.array.commands.staff;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandMeta(label = {"follow"}, permission = "array.staff")
public class FollowCommand {
    public void execute(final Player player, @CPL("player") final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isFollowMode()) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7You are already following somebody, /unfollow before following someone again."));
            return;
        }
        profile.setFollowMode(true);
        profile.setSilent(true);
        profile.setFollowing(target);
        Profile.getByUuid(target.getUniqueId()).getFollower().add(player);
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7You have &bstarted &7following &b" + target.getName() + "&7."));

        Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        if (targetProfile.isInSomeSortOfFight()) {
            if (targetProfile.isInMatch()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(Array.getInstance(), () -> player.chat("/spec " + target.getName()), 20L);
            }
        }
    }
}
