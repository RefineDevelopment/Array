package me.drizzy.practice.array.commands;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"array save", "array savedata"}, permission="practice.dev")
public class ArraySaveDataCommand {
    public void execute(Player p) {
        Profile.getProfiles().values().forEach(Profile::save);
        p.sendMessage(CC.translate("&8[&b&lArray&8] &a") + "Saving profiles...");
    }
}
