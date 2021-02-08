package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.event.types.skywars.SkywarsChests;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars chests clear", permission="array.event.skywars")
public class SkywarsClearChestsCommand {
    public void execute(Player player) {
        for ( SkywarsChests chests : SkywarsChests.chestsList ) {
            chests.delete();
        }
        player.sendMessage(CC.translate("&8[&b&lArray&8] &bCleared all chests!"));
    }
}
