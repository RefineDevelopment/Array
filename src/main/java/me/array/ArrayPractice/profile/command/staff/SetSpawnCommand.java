package me.array.ArrayPractice.profile.command.staff;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "setspawn" }, permission = "practice.setspawn")
public class SetSpawnCommand
{
    public void execute(final Player player) {
        Array.get().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&aSuccefully set the lobby spawn!"));
    }
}
