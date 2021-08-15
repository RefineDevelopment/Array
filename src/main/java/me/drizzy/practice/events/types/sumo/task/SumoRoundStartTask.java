package me.drizzy.practice.events.types.sumo.task;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.events.types.sumo.SumoState;
import me.drizzy.practice.events.types.sumo.SumoTask;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SumoRoundStartTask extends SumoTask {

	public SumoRoundStartTask(Sumo sumo) {
		super(sumo, SumoState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getSumo().broadcastMessage(Locale.EVENT_ROUND_STARTED.toString());
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

			(this.getSumo()).setRoundStart(System.currentTimeMillis());
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

			this.getSumo().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
		}
	}

}
