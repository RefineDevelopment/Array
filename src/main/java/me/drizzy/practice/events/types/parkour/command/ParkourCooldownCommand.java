package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "parkour cooldown", permission = "array.staff")
public class ParkourCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getParkourManager().getCooldown().hasExpired()) {
			sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
			return;
		}
		sender.sendMessage(CC.translate("&7Successfully reset the &cParkour Event &7cooldown."));
		Array.getInstance().getParkourManager().setCooldown(new Cooldown(0));
	}

}
