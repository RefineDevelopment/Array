package me.drizzy.practice.event.types.wizard.task;

import me.drizzy.practice.event.types.wizard.Wizard;
import me.drizzy.practice.event.types.wizard.WizardState;
import me.drizzy.practice.event.types.wizard.WizardTask;

public class WizardRoundEndTask extends WizardTask {

	public WizardRoundEndTask(Wizard wizard) {
		super(wizard, WizardState.ROUND_ENDING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			if (this.getWizard().canEnd()) {
				this.getWizard().end();
			} else {
				this.getWizard().onRound();
			}
		}
	}

}
