package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit getinv", "kit getinventory"}, permission = "array.dev")
public class KitGetInvCommand {

    public void execute(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7A kit with that name does not exist."));
            return;
        }

        player.getInventory().setArmorContents(kit.getKitInventory().getArmor());
        player.getInventory().setContents(kit.getKitInventory().getContents());
        player.addPotionEffects(kit.getKitInventory().getEffects());
        player.updateInventory();
        player.sendMessage(CC.translate("&8[&b&lArray&8] &7You received the kit's inventory."));
    }

}
