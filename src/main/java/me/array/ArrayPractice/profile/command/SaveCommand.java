package me.array.ArrayPractice.profile.command;


import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label={"leaderboards reload", "lb reload", "save", "data save", "practice save"}, permission="practice.staff")
public class SaveCommand {
    public void execute(Player player) {
        Profile.getProfiles().values().forEach(Profile::save);
        Profile.loadAllProfiles();
        Kit.getKits().forEach(Kit::save);
        Kit.getKits().forEach(Kit::updateKitLeaderboards);
        Profile.loadGlobalLeaderboards();
        Arena.getArenas().forEach(Arena::save);
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7&oReloading all Stats and Leaderboards!"));
    }
}
