package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"array save ", "array update"}, permission="array.dev")
public class ArraySaveCommand {
    public void execute(Player p) {
        Array.getInstance().getTaskThread().execute(() -> {
            Profile.getProfiles().values().forEach(Profile::save);
            Profile.getProfiles().values().forEach(Profile::load);
            Kit.getKits().forEach(Kit::save);
            Kit.getKits().forEach(Kit::updateKitLeaderboards);
            Profile.loadGlobalLeaderboards();
            Arena.getArenas().forEach(Arena::save);
            p.sendMessage(CC.translate("&8[&c&lArray&8] &7&oReloaded all Stats and Leaderboards!"));
        });
    }
}
