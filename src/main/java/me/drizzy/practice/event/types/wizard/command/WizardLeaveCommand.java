package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.wizard.Wizard;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "wizard leave")
public class WizardLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Wizard activeWizard= Array.getInstance().getWizardManager().getActiveWizard();

		if (activeWizard == null) {
			player.sendMessage(CC.RED + "There isn't any active Wizard Events.");
			return;
		}

		if (!profile.isInWizard() || !activeWizard.getEventPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are not apart of the active Wizard Event.");
			return;
		}

		activeWizard.handleLeave(player);
	}

}
