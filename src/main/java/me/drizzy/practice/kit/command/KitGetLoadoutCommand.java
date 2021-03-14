package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit getloadout", permission = "array.dev")
public class KitGetLoadoutCommand {

    public void execute(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c"))+ "A kit with that name does not exist.");
            return;
        }

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
        player.addPotionEffects(kit.getKitLoadout().getEffects());
        player.updateInventory();

        player.sendMessage((CC.translate("&8[&b&lArray&8] &a"))+ "You received the kit's loadout.");
    }

}
