package me.drizzy.practice.array.commands.staff;

import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;

import java.util.UUID;

@CommandMeta(label="uuid", permission="array.dev")
public class GetUUIDCommand {
    public void execute(Player player, @CPL("name") String name) {
        UUID uuid = ArrayCache.getUUID(name);

        if (uuid == null) {
            player.sendMessage(CC.RED + "Error!");
        }

        if (uuid != null) {
            player.sendMessage(CC.GREEN + uuid.toString());
        }

    }
}
