package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "spleef cooldown", permission = "array.staff")
public class SpleefCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSpleefManager().getCooldown().hasExpired()) {
			sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
			return;
		}
		sender.sendMessage(CC.translate("&7Successfully reset the &cSpleef Event &7cooldown."));
		Array.getInstance().getSpleefManager().setCooldown(new Cooldown(0));
	}


}
