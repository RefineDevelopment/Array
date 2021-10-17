package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentTask;
import xyz.refinedev.practice.tournament.TournamentType;
import xyz.refinedev.practice.tournament.impl.SoloTournament;
import xyz.refinedev.practice.tournament.impl.TeamTournament;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/28/2021
 * Project: Array
 */

@RequiredArgsConstructor
@SuppressWarnings("all")
@Getter @Setter
public class TournamentManager {

    private final Array plugin;
    private Tournament currentTournament;

    /**
     * Creates a tournament for a certain kit
     *
     * @param kit kit to create the tournament with
     */
    public void createTournament(Player player, Kit kit, int max, TournamentType tournamentType) {
        switch (tournamentType) {
            case SOLO: {
                this.currentTournament = new SoloTournament(plugin, player.getName(), max, kit);
            }
            case TEAM: {
                this.currentTournament = new TeamTournament(plugin, player.getName(), max, kit);
            }
        }

        new TournamentTask(plugin, this.currentTournament).runTaskTimer(plugin, 20L, 20L);

        Bukkit.broadcastMessage(Locale.TOURNAMENT_BROADCAST.toString()
                .replace("<host_name>", this.currentTournament.getHost())
                .replace("<kit>", this.currentTournament.getKit().getDisplayName())
                .replace("<tournament_type>", this.currentTournament.getType().name()));
    }

}
