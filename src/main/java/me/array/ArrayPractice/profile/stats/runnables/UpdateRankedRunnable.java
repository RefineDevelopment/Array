package me.array.ArrayPractice.profile.stats.runnables;

import lombok.RequiredArgsConstructor;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public class UpdateRankedRunnable implements Runnable {

    @Override
    public void run() {
        for (final Profile profile : Profile.getProfiles().values()) {
            profile.save();
            Profile.loadAllProfiles();
        }
        Kit.getKits().forEach(Kit::updateKitLeaderboards);
        Profile.loadGlobalLeaderboards();
        Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Warning " + ChatColor.GRAY + "Updating Leaderboards, This Might Cause Some Lag!");
    }
}
