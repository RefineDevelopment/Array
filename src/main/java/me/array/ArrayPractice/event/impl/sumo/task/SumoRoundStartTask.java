package me.array.ArrayPractice.event.impl.sumo.task;

import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.event.impl.sumo.SumoState;
import me.array.ArrayPractice.event.impl.sumo.SumoTask;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SumoRoundStartTask extends SumoTask {

	public SumoRoundStartTask(Sumo sumo) {
		super(sumo, SumoState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getSumo().broadcastMessage(CC.AQUA + CC.BOLD + "The round has started!");
			this.getSumo().setEventTask(null);
			this.getSumo().setState(SumoState.ROUND_FIGHTING);

			Player playerA = this.getSumo().getRoundPlayerA().getPlayer();
			Player playerB = this.getSumo().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
				PlayerUtil.allowMovement(playerA);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
				PlayerUtil.allowMovement(playerB);
			}

			((Sumo) this.getSumo()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();
			Player playerA = this.getSumo().getRoundPlayerA().getPlayer();
			Player playerB = this.getSumo().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			}

			this.getSumo().broadcastMessage("&b" + seconds + "...");
		}
	}

}
