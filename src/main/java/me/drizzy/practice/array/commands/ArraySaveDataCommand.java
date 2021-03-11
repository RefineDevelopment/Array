package me.drizzy.practice.array.commands;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label={"array save", "array savedata"}, permission="array.dev")
public class ArraySaveDataCommand {
    public void execute(Player player) {
        Profile.getProfiles().values().forEach(Profile::save);
        player.sendMessage(CC.translate("&8[&b&lArray&8] &bSaving profiles..."));
    }
}
