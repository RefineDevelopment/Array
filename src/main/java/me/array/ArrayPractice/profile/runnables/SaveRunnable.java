package me.array.ArrayPractice.profile.runnables;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Bukkit;

public class SaveRunnable implements Runnable {
    public void run() {
        for ( Arena arena : Arena.getArenas() ) {
            arena.save();
        }
        for ( Kit kit : Kit.getKits() ) {
            kit.save();
        }
        for ( Profile profile : Profile.getProfiles().values() ) {
            profile.save();
            Profile.loadAllProfiles();
        }
        Bukkit.getLogger().info(CC.translate("&c&Warning &7Saving all Data, this might cause some lag!"));
    }
}
