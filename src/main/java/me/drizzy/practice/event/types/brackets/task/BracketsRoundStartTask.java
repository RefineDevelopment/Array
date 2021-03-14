package me.drizzy.practice.event.types.brackets.task;

import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.brackets.BracketsState;
import me.drizzy.practice.event.types.brackets.BracketsTask;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BracketsRoundStartTask extends BracketsTask {

	public BracketsRoundStartTask(Brackets brackets) {
		super(brackets, BracketsState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getBrackets().broadcastMessage(CC.AQUA + "The round has started!");
			this.getBrackets().setEventTask(null);
			this.getBrackets().setState(BracketsState.ROUND_FIGHTING);

			Player playerA = this.getBrackets().getRoundPlayerA().getPlayer();
			Player playerB = this.getBrackets().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
			}

			(this.getBrackets()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();
			Player playerA = this.getBrackets().getRoundPlayerA().getPlayer();
			Player playerB = this.getBrackets().getRoundPlayerB().getPlayer();

			if (playerA != null) {
				playerA.playSound(playerA.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			}

			if (playerB != null) {
				playerB.playSound(playerB.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
			}

			this.getBrackets().broadcastMessage("&b" + seconds + "...");
		}
	}

}
