package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label= {"spawn", "leave"}, permission = "practice.staff")
public class SpawnCommand {
    public void execute(Player player) {
        Profile profile=new Profile(player.getUniqueId());
        if (profile.isInSomeSortOfFight() && !profile.isInLobby()) {
            player.sendMessage(CC.translate("Unable to teleport to spawn, Please finish your current task!"));
        }

        Profile.getProfiles().put(player.getUniqueId(), profile);

        profile.setName(player.getName());
        Array.get().getEssentials().teleportToSpawn(player);

        profile.refreshHotbar();
        profile.handleVisibility();

        for ( Profile otherProfile : Profile.getProfiles().values() ) {
            otherProfile.handleVisibility(otherProfile.getPlayer(), player);
        }
    }
}
