package xyz.refinedev.practice.event.impl.brackets.solo.task;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.impl.brackets.solo.BracketsSolo;
import xyz.refinedev.practice.event.task.EventRoundStartTask;
import xyz.refinedev.practice.util.other.PlayerUtil;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

public class BracketsSoloRoundStartTask extends EventRoundStartTask {
    
    public BracketsSoloRoundStartTask(BracketsSolo event) {
        super(event);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getEvent().broadcastMessage(Locale.EVENT_ROUND_STARTED.toString());
            this.getEvent().setEventTask(null);
            this.getEvent().setState(EventState.ROUND_FIGHTING);

            Player playerA = this.getEvent().getRoundPlayerA().getPlayer();
            Player playerB = this.getEvent().getRoundPlayerB().getPlayer();

            if (playerA != null) {
                playerA.playSound(playerA.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                PlayerUtil.allowMovement(playerA);
            }

            if (playerB != null) {
                playerB.playSound(playerB.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                PlayerUtil.allowMovement(playerB);
            }

            this.getEvent().setRoundStart(System.currentTimeMillis());
        } else {
            int seconds = getSeconds();
            Player playerA = this.getEvent().getRoundPlayerA().getPlayer();
            Player playerB = this.getEvent().getRoundPlayerB().getPlayer();

            if (playerA != null) {
                playerA.playSound(playerA.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            }

            if (playerB != null) {
                playerB.playSound(playerB.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            }

            this.getEvent().broadcastMessage(Locale.EVENT_START_COUNTDOWN.toString().replace("<seconds>", String.valueOf(seconds)));
        }
    }
}
