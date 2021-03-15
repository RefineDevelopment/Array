package me.drizzy.practice.event.types.wizard.task;

import me.drizzy.practice.event.types.wizard.Wizard;
import me.drizzy.practice.event.types.wizard.WizardState;
import me.drizzy.practice.event.types.wizard.WizardTask;
<<<<<<< Updated upstream
import org.bukkit.Sound;
import org.bukkit.entity.Player;
=======
>>>>>>> Stashed changes

public class WizardRoundEndTask extends WizardTask {

	public WizardRoundEndTask(Wizard wizard) {
		super(wizard, WizardState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getWizard().canEnd()) {
<<<<<<< Updated upstream
				Player playerA = this.getWizard().getRoundPlayerA().getPlayer();
				Player playerB = this.getWizard().getRoundPlayerB().getPlayer();

				if (playerA != null) {
					playerA.setAllowFlight(false);
				}

				if (playerB != null) {
					playerB.setAllowFlight(false);
				}

=======
>>>>>>> Stashed changes
				this.getWizard().end();
			} else {
				this.getWizard().onRound();
			}
		}
	}

}
