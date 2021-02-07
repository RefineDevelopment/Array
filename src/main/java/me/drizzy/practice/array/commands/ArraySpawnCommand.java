package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label= {"spawn", "leave"}, permission = "practice.staff")
public class ArraySpawnCommand {
    public void execute(Player player) {
        Profile profile=new Profile(player.getUniqueId());
        if (profile.isInSomeSortOfFight() && !profile.isInLobby()) {
            player.sendMessage(CC.translate("Unable to teleport to spawn, Please finish your current task!"));
        }
        Array.getInstance().getEssentials().teleportToSpawn(player);
        profile.refreshHotbar();
    }
}
