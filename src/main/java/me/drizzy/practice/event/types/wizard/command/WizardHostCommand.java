package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.wizard.Wizard;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "wizard host" }, permission = "array.host.wizard")
public class WizardHostCommand {

	public static void execute(Player player) {
		if (Array.getInstance().getWizardManager().getActiveWizard() != null) {
			player.sendMessage(CC.RED + "There is already an active Wizard Event.");
			return;
		}

		if (!Array.getInstance().getWizardManager().getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "There is a Wizard Event cooldown active.");
			return;
		}

		Array.getInstance().getWizardManager().setActiveWizard(new Wizard(player));

		for (Player other : Array.getInstance().getServer().getOnlinePlayers()) {
			Profile profile = Profile.getByUuid(other.getUniqueId());

			if (profile.isInLobby()) {
				if (!profile.getKitEditor().isActive()) {
					profile.refreshHotbar();
				}
			}
		}
	}

}
