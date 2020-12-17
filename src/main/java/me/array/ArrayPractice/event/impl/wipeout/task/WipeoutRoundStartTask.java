package me.array.ArrayPractice.event.impl.wipeout.task;

import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutState;
import me.array.ArrayPractice.event.impl.wipeout.WipeoutTask;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;

public class WipeoutRoundStartTask extends WipeoutTask {

	public WipeoutRoundStartTask(Wipeout wipeout) {
		super(wipeout, WipeoutState.ROUND_STARTING);
	}

	@Override
	public void onRun() {
		if (getTicks() >= 3) {
			this.getWipeout().broadcastMessage(CC.AQUA + "The wipeout has started!");
			this.getWipeout().setEventTask(null);
			this.getWipeout().setState(WipeoutState.ROUND_FIGHTING);
			this.getWipeout().getPlayers().forEach(PlayerUtil::allowMovement);

			((Wipeout) this.getWipeout()).setRoundStart(System.currentTimeMillis());
		} else {
			int seconds = getSeconds();

			this.getWipeout().broadcastMessage("&b" + seconds + "...");
		}
	}

}
