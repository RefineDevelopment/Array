package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TitleAPI;

@RequiredArgsConstructor
public class MatchStartTask extends BukkitRunnable {

    private final Array plugin = Array.getInstance();
    private final Match match;
    private int ticks;

    @Override
    public void run() {
        int seconds = 5 - ticks;

        if (match.isEnding()) {
            cancel();
            return;
        }

        final String replace = Locale.MATCH_COUNTDOWN.toString().replace("<seconds>", String.valueOf((seconds)));
        if (seconds == 0) {
            match.getPlayers().forEach(PlayerUtil::allowMovement);
            match.setState(MatchState.FIGHTING);
            match.setStartTimestamp(System.currentTimeMillis());
            match.broadcastMessage(Locale.MATCH_STARTED.toString());
            if (plugin.getConfigHandler().isDISCLAIMER_ENABLED()) {
                match.broadcastMessage("");
                Locale.MATCH_DISCLAIMER.toList().forEach(match::broadcastMessage);
            }
            //match.getPlayers().forEach(TitleAPI::clearTitle);
            match.getPlayers().forEach(TitleAPI::sendMatchStart);
            match.broadcastSound(Sound.NOTE_BASS);
            cancel();
            return;
        }

        //match.getPlayers().forEach(TitleAPI::clearTitle);
        match.getPlayers().forEach(TitleAPI::sendMatchCountdown);
        match.broadcastMessage(replace);
        match.broadcastSound(Sound.NOTE_PLING);
        ticks++;
    }

}
