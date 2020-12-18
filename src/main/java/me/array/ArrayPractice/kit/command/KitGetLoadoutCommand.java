package me.array.ArrayPractice.kit.command;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit getloadout", permission = "practice.staff")
public class KitGetLoadoutCommand {

	public void execute(Player player, Kit kit) {
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name does not exist.");
			return;
		}

		player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
		player.getInventory().setContents(kit.getKitLoadout().getContents());
		player.updateInventory();

		player.sendMessage(CC.GREEN + "You received the kit's loadout.");
	}

}
