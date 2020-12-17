package me.array.ArrayPractice.match.task;

import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchState;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartTask extends BukkitRunnable {

	private final Match match;
	private int ticks;

	public MatchStartTask(Match match) {
		this.match = match;
	}

	@Override
	public void run() {
		int seconds=5 - ticks;

		if (match.isEnding()) {
			cancel();
			return;
		}

		if (match.isHCFMatch() || match.isKoTHMatch()) {
			if (seconds == 0) {
				match.setState(MatchState.FIGHTING);
				match.setStartTimestamp(System.currentTimeMillis());
				match.broadcastMessage(CC.GREEN + "Match Started!");
				match.broadcastSound(Sound.LEVEL_UP);
				cancel();
				return;
			}

			match.broadcastMessage(CC.WHITE + "Starting in " + CC.AQUA + seconds + CC.WHITE +  "...");
			match.broadcastSound(Sound.NOTE_PLING);
		} else {
			if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isParkour()) {
				if (seconds == 2) {
					match.getPlayers().forEach(PlayerUtil::allowMovement);
					match.setState(MatchState.FIGHTING);
					match.setStartTimestamp(System.currentTimeMillis());
					match.broadcastMessage(CC.GREEN + "The round has started!");
					match.broadcastSound(Sound.NOTE_BASS);
					cancel();
					return;
				}

				match.broadcastMessage(CC.AQUA + (seconds - 2) + CC.WHITE + "...");
				match.broadcastSound(Sound.NOTE_PLING);
			} else {
				if (seconds == 0) {
					match.setState(MatchState.FIGHTING);
					match.setStartTimestamp(System.currentTimeMillis());
					match.broadcastMessage(CC.GREEN + "Match Started!");
					match.broadcastSound(Sound.NOTE_BASS);
					cancel();
					return;
				}

				match.broadcastMessage(CC.WHITE + "Starting in " + CC.AQUA + seconds + CC.WHITE +  "...");
				match.broadcastSound(Sound.NOTE_PLING);
			}
		}

		ticks++;
	}

}
