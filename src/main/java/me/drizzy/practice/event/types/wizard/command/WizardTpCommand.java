package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "wizard tp", permission = "array.staff")
public class WizardTpCommand {

	public void execute(Player player) {
		player.teleport(Array.getInstance().getWizardManager().getWizardSpectator());
		player.sendMessage(CC.GREEN + "Teleported to wizard's spawn location.");
	}

}
