package xyz.refinedev.practice.tournament;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
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

public class TournamentTask extends BukkitRunnable {

    private int countdown = 60;

    public TournamentTask() {
        Bukkit.broadcastMessage(Locale.TOURNAMENT_BROADCAST.toString()
                .replace("<host_name>", Tournament.getCurrentTournament().getHost())
                .replace("<kit>", Tournament.getCurrentTournament().getKit().getDisplayName())
                .replace("<tournament_type>", Tournament.getCurrentTournament().getType().name()));
    }

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
            Tournament.getCurrentTournament().setTask(null);
            this.cancel();
            if (Tournament.getCurrentTournament().getParticipatingCount() < Tournament.getCurrentTournament().getParticipantsToStart()) {
                Tournament.getCurrentTournament().end(null);
                Tournament.setCurrentTournament(null);
            } else {
                Tournament.getCurrentTournament().start();
            }
        }
    }
}
