package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "wizard cooldown", permission = "array.staff")
public class WizardCooldownCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getWizardManager().getCooldown().hasExpired()) {
			sender.sendMessage(CC.RED + "There isn't a Wizard Event cooldown.");
			return;
		}

		sender.sendMessage(CC.GREEN + "You reset the Wizard Event cooldown.");

		Array.getInstance().getWizardManager().setCooldown(new Cooldown(0));
	}

}
