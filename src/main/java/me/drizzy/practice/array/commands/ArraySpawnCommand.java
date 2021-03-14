package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.array.essentials.Essentials;
import org.bukkit.entity.Player;

@CommandMeta(label= {"spawn", "array spawn"}, permission = "array.staff")
public class ArraySpawnCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.isInSomeSortOfFight() && !profile.isInLobby()) {
            player.sendMessage(CC.translate("Unable to teleport to spawn, Please finish your current task!"));
        }
        Essentials.teleportToSpawn(player);
        profile.refreshHotbar();
    }
}
