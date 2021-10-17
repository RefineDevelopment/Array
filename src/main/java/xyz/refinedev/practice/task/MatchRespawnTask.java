package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.TitleAPI;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/10/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchRespawnTask extends BukkitRunnable {

    private final Array plugin;
    private final Player player;
    private final Match match;

    private int ticks;

    @Override
    public void run() {
        int seconds = 3 - ticks;

        if (match.isEnding()) {
            cancel();
            return;
        }

        final String replace = Locale.MATCH_COUNTDOWN.toString().replace("<seconds>", String.valueOf((seconds)));
        if (seconds == 0) {
            player.setMetadata("noDenyMove", new FixedMetadataValue(plugin, true));
            match.setupPlayer(player);
            this.cancel();
            return;
        }

        TitleAPI.sendRespawnCountdown(player, ticks);
        player.sendMessage(replace);
        Button.playNeutral(player);
        ticks++;
    }
}
