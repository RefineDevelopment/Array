package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit getloadout", permission = "practice.kit")
public class KitGetLoadoutCommand {

    public void execute(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
        player.addPotionEffects(kit.getKitLoadout().getEffects());
        player.updateInventory();

        player.sendMessage(CC.GREEN + "You received the kit's loadout.");
    }

}
