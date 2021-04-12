package me.drizzy.practice.events.types.gulag.task;

import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.gulag.GulagState;
import me.drizzy.practice.events.types.gulag.GulagTask;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GulagRoundStartTask extends GulagTask {

	public GulagRoundStartTask(Gulag gulag) {
		super(gulag, GulagState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getGulag().broadcastMessage(CC.AQUA + "The round has started!");
			this.getGulag().setEventTask(null);
			this.getGulag().setState(GulagState.ROUND_FIGHTING);

			Player playerA = this.getGulag().getRoundPlayerA().getPlayer();
			Player playerB = this.getGulag().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
				PlayerUtil.allowMovement(playerA);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
				PlayerUtil.allowMovement(playerB);
			}

			(this.getGulag()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();
			Player playerA = this.getGulag().getRoundPlayerA().getPlayer();
			Player playerB = this.getGulag().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			}

			this.getGulag().broadcastMessage("&b" + seconds + "...");
		}
	}

}
