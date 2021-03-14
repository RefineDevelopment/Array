package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "wizard cancel", permission = "array.staff")
public class WizardCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getWizardManager().getActiveWizard() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Wizard event.");
			return;
		}

		Array.getInstance().getWizardManager().getActiveWizard().end();
	}

}
