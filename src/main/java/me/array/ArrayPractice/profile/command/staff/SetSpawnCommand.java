package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "setspawn" }, permission = "practice.staff")
public class SetSpawnCommand
{
    public void execute(final Player player) {
        Practice.getInstance().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&aSuccefully set the lobby spawn!"));
    }
}

