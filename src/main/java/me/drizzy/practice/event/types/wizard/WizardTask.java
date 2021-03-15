package me.drizzy.practice.event.types.wizard;

import lombok.Getter;
import me.drizzy.practice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class WizardTask extends BukkitRunnable {

	private int ticks;
	private Wizard wizard;
	private WizardState eventState;

	public WizardTask(Wizard wizard, WizardState eventState) {
		this.wizard=wizard;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.getInstance().getWizardManager().getActiveWizard() == null ||
		    !Array.getInstance().getWizardManager().getActiveWizard().equals(wizard) || wizard.getState() != eventState) {
			cancel();
			return;
		}

		onRun();

		ticks++;
	}

	public int getSeconds() {
		return 3 - ticks;
	}

	public abstract void onRun();

}
