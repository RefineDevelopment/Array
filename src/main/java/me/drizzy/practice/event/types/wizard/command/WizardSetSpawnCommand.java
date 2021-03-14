package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.wizard.WizardManager;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "wizard setspawn", permission = "array.dev")
public class WizardSetSpawnCommand {

	public void execute(Player player, @CPL("[one|two|spec]") String position) {
		WizardManager wizard = Array.getInstance().getWizardManager();
		if (!(position.equals("one") || position.equals("two") || position.equals("spec"))) {
			player.sendMessage(CC.RED + "The position must be one/two/spec.");
		} else {
			if (position.equals("one")) {
				wizard.setWizardSpawn1(player.getLocation());
			} else if (position.equals("two")){
				wizard.setWizardSpawn2(player.getLocation());
			} else {
				wizard.setWizardSpectator(player.getLocation());
			}

			player.sendMessage(CC.GREEN + "Updated wizard's spawn location " + position + ".");

			wizard.save();
		}
	}

}
