package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.wizard.Wizard;
import me.drizzy.practice.event.types.wizard.WizardState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "wizard join")
public class WizardJoinCommand {

	public static void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		Wizard activeWizard= Array.getInstance().getWizardManager().getActiveWizard();

		if (profile.isBusy(player) || profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the wizard right now.");
			return;
		}

		if (activeWizard == null) {
			player.sendMessage(CC.RED + "There isn't any active Wizard Events right now.");
			return;
		}

		if (activeWizard.getState() != WizardState.WAITING) {
			player.sendMessage(CC.RED + "This Wizard Event is currently on-going and cannot be joined.");
			return;
		}

		activeWizard.handleJoin(player);
	}

}
