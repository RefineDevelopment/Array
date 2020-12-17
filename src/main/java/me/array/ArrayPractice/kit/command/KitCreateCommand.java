package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.entity.Player;
import rip.verse.jupiter.knockback.KnockbackProfile;

@CommandMeta(label = "kit create", permission = "practice.kit.create")
public class KitCreateCommand {

	public void execute(Player player, @CPL("name") String kitName, @CPL("knockback-profile") String knockback) {
		if (Kit.getByName(kitName) != null) {
			player.sendMessage(CC.RED + "A kit with that name already exists.");
			return;
		}

		Kit kit = new Kit(kitName, knockback);
		kit.save();

		Kit.getKits().add(kit);

		player.sendMessage(CC.GREEN + "You created a new kit.");
	}

}
