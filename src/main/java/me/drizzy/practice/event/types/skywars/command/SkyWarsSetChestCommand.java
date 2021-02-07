package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.event.types.skywars.ChestType;
import me.drizzy.practice.event.types.skywars.SkywarsChests;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

@CommandMeta(label = "skywars setchest", permission = "practice.skywars.setchest")
public class SkyWarsSetChestCommand {

    public void execute(Player player, @CPL(value = "type") String stringType) {
        try {
            ChestType.valueOf(stringType);
        } catch (Exception e) {
            player.sendMessage(CC.translate("&cInvalid chest type."));
            return;
        }
        ChestType type = ChestType.valueOf(stringType);
        Location playerLookingAt = player.getTargetBlock((Set<Material>) null, 100).getLocation();

        if (SkywarsChests.getFromLocation(playerLookingAt) != null) {
            SkywarsChests chest = SkywarsChests.getFromLocation(playerLookingAt);
            chest.setType(type);
            chest.save();
            player.sendMessage(CC.translate("&aSuccessfully set existing chest at current location to type " + type.name()));
        } else {
            SkywarsChests chest = new SkywarsChests(UUID.randomUUID(), playerLookingAt, type);
            chest.save();
            player.sendMessage(CC.translate("&aSuccessfully saved new chest on type " + type.name()));
        }

    }

}
