package xyz.refinedev.practice.tournament;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.util.chat.Clickable;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentTask extends BukkitRunnable {

    private final Array plugin;
    private final Tournament tournament;
    private int countdown = 60;

    @Override
    public void run() {
        countdown--;
        if (countdown % 10 == 0 || countdown <= 10) {
            if (countdown > 0) {
                Clickable clickable = new Clickable(Locale.TOURNAMENT_COUNTDOWN.toString().replace("<seconds>", String.valueOf(countdown)), Locale.TOURNAMENT_HOVER.toString(), "/tournament join");
                Bukkit.getOnlinePlayers().forEach(clickable::sendToPlayer);
            }
        }
        if (countdown <= 0) {
            this.cancel();
            if (tournament.getParticipatingCount() < tournament.getParticipantsToStart()) {
                tournament.end(null);
                plugin.getTournamentManager().setCurrentTournament(null);
            } else {
                tournament.start();
            }
        }
    }
}
